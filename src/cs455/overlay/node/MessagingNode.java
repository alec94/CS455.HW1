package cs455.overlay.node;

import com.sun.deploy.util.ArrayUtil;
import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Utils;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Alec on 1/23/2017.
 * Messaging node
 */
public class MessagingNode extends NetworkNode {
    private ConsoleThread terminal;
    private TCPServerThread serverThread;
    private RoutingCache routingCache;
    private String RegistryKey;
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long receiveSummation = 0;
    private long sendSummation = 0;
    private String[] nodeList;


    public synchronized void onEvent(Event event, String socketKey) {

        switch (event.getType()) {
            case LinkWeights:
                event = (LinkWeights) event;
                calculateRouting(((LinkWeights) event).getLinkWeights(), ((LinkWeights) event).getNodeList());
                break;
            case Message:
                handleMessage((Message) event);
                break;
            case MessagingNodesList:
                createOverlayConnections(((MessagingNodesList) event).getKeys());
                break;
            case TaskInitiate:
                startRounds(((TaskInitiate) event).getRounds());
                break;
            case TaskSummaryRequest:
                sendTaskSummary();
                break;
            case Register:
                event = (Register) event;
                String newKey = ((Register) event).IPAddress + ":" + ((Register) event).Port;
                //System.out.println("Register event received, " + newKey);
                updateKey(socketKey, newKey);

                break;
            default:
                System.out.println("Invalid message type: " + event.getType());
                break;
        }
    }

    private void sendTaskSummary() {
        TaskSummaryResponse response = new TaskSummaryResponse(this.nodeKey, this.sendTracker, this.receiveTracker, this.relayTracker, this.sendSummation, this.receiveSummation);

        senders.get(this.RegistryKey).sendData(response.getBytes());
        System.out.println("Task summary sent.");
    }

    private void handleMessage(Message message) {
        if (message.getDestinationKey().equals(this.nodeKey)) {
            receiveSummation += message.getPayload();
            receiveTracker++;
        } else {
            //System.out.println("Forwarding messaging to " + message.getDestinationKey());
            senders.get(routingCache.getNextNodeKey(message.getDestinationKey(), senders)).sendData(message.getBytes());
            relayTracker++;
        }
    }

    private void startRounds(int numberOfRounds) {

        //remove self from list of possible destinations
        String[] newList = Utils.removeElement(this.nodeList, this.nodeKey);
        System.out.println("Starting rounds, count: " + numberOfRounds);
        for (int i = 0; i < numberOfRounds; i++) {
            System.out.println("Starting round " + (i + 1));
            //gets random destination node
            String destinationKey = newList[ThreadLocalRandom.current().nextInt(0, newList.length)];
            for (int j = 0; j < 5; j++) {
                //get random payload between 2147483647 and -2147483648.
                int payload = ThreadLocalRandom.current().nextInt();
                sendSummation += payload;
                Message message = new Message(payload, destinationKey, this.nodeKey);
                //send to next node in the routing plan

                String nextNode = routingCache.getNextNodeKey(destinationKey, senders);

                //System.out.println("Destination: " + destinationKey + ", payload: " + payload + ", next node: " + nextNode);

                senders.get(nextNode).sendData(message.getBytes());
                sendTracker++;
            }
        }
        System.out.println("Task Complete.");
        senders.get(this.RegistryKey).sendData(new TaskComplete((byte) 1, this.nodeKey).getBytes());
    }

    private void calculateRouting(int[][] linkWeights, String[] nodeList) {
        int myIndex = Arrays.asList(nodeList).indexOf(this.nodeKey);
        this.nodeList = nodeList;
        ShortestPath router = new ShortestPath(nodeList.length);

        this.routingCache = router.dijkstra(linkWeights, myIndex, nodeList);
        System.out.println("Done calculating paths");
    }

    private void createOverlayConnections(String[] keys) {
        System.out.println("Setting up overlay connections to " + Arrays.toString(keys));

        for (String key : keys) {
            String[] params = key.split(":");
            String IPAddress = params[0];
            int Port = Integer.parseInt(params[1]);

            try {
                addSocket(new Socket(IPAddress, Port), key);

                String[] registerParams = this.nodeKey.split(":");

                senders.get(key).sendData(new Register(registerParams[0], Integer.parseInt(registerParams[1])).getBytes());

            } catch (IOException ioe) {
                System.out.print("Error opening socket to " + key + ". " + ioe.getMessage());
            }
        }

        System.out.println("All connections are established. Number of connections created: " + keys.length);
        System.out.println("Total connections: " + (senders.size() - 1));
    }

    private void Register(String registryHost, int registyPort) throws IOException {
        this.RegistryKey = registryHost + ":" + registyPort;
        addSocket(new Socket(registryHost, registyPort), RegistryKey);

        String[] registerParams = this.nodeKey.split(":");

        senders.get(RegistryKey).sendData(new Register(registerParams[0], Integer.parseInt(registerParams[1])).getBytes());
    }

    public void handleConsoleInput(String string) {
        String[] inputArray = string.split(" ");

        switch (inputArray[0]) {
            case "print-shortest-path":
                routingCache.printPaths();
                break;
            case "exit-overlay":
                exitOverlay();
                break;
            default:
                System.out.println("Unknown command: " + string);
        }
    }

    private void exitOverlay() {
        String[] temp = this.nodeKey.split(":");
        senders.get(this.RegistryKey).sendData(new Deregister(temp[0], Integer.parseInt(temp[1])).getBytes());

        for (TCPSender sender : senders.values()) {
            sender.close();
        }

        for (TCPReceiverThread receiver : receivers.values()) {
            receiver.close();
        }

    }

    private MessagingNode(String registryHost, int registryPort) throws IOException {

        //port = 0 for automatic available port
        startServerThread(0);

        this.Register(registryHost, registryPort);

        this.terminal = new ConsoleThread(this);

        Thread thread = new Thread(this.terminal);
        thread.start();

        System.out.println("New messaging node started, key: " + this.nodeKey);

    }

    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("You must provide the host and port for the registry.");
        }
        int RegistryPort = 0;

        try {
            RegistryPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException fe) {
            System.out.println("Error: port number must be an integer.");
            System.exit(-1);
        }

        try {
            MessagingNode messagingNode = new MessagingNode(args[0], RegistryPort);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

    }
}
