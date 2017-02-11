package cs455.overlay.node;

import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Register;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alec on 1/23/2017.
 * Registry Node
 */
public class Registry extends BaseNode{
    private ArrayList<String> messagingNodes;

    private void createOverlay(int numberOfConnections){
       for(int i = 0; i < messagingNodes.size(); i++){
           String[] keys = new String[2];
           String nodeKey = messagingNodes.get(i);
           keys[0] = messagingNodes.get((i+1) % messagingNodes.size());
           keys[1] = messagingNodes.get((i+2) % messagingNodes.size());

           MessagingNodesList list = new MessagingNodesList(keys);

           TCPSender sender = senders.get(nodeKey);

           System.out.println("Connecting " + nodeKey + " to " + Arrays.toString(keys));

           sender.sendData(list.getBytes());
       }
    }

    public synchronized void onEvent(Event event, String socketKey){
        String key;

        switch (event.getType()) {
            case Deregister:
                Deregister deregisterEvent = (Deregister) event;

                key = deregisterEvent.IPAddress + ":" + deregisterEvent.Port;

                if (messagingNodes.contains(key)){
                    messagingNodes.remove(key);
                }

                if(senders.containsKey(key)){
                    senders.remove(key);
                }

                if(receivers.containsKey(key)){
                    senders.remove(key);
                }

                break;
            case LinkWeights:
                break;
            case Message:
                break;
            case MessagingNodesList:
                break;
            case Register:
                Register registerEvent = (Register) event;

                key = registerEvent.IPAddress + ":" + registerEvent.Port;

                if (!messagingNodes.contains(key)){
                    updateKey(socketKey, key);

                    messagingNodes.add(key);

                    System.out.println("New messaging node registered, " + key);
                } else {
                    System.out.println("Messaging node is already registered, " + key);
                }

                break;
            case TaskComplete:
                break;
            case TaskInitiate:
                break;
            case TaskSummaryRequest:
                break;
            case TaskSummaryResponse:
                break;
        }

    }

    public void handleConsoleInput(String string){
        String[] inputArray = string.split(" ");
        switch (inputArray[0]){
            case "list-messaging-nodes":
                if (messagingNodes.isEmpty()){
                    System.out.println("Not connected to any MessagingNodes.");
                }else {
                    for (Object messagingNode : messagingNodes.toArray()) {
                        System.out.println(messagingNode);
                    }
                }
                break;
            case "list-weights":
                break;
            case "setup-overlay":
                if (inputArray.length < 2){
                    System.out.println("You must specify the number of connections.");
                    System.out.println("USAGE: setup-overlay <number-of-connections.");
                }else {
                    try{
                        int numberOfConnections = Integer.parseInt(inputArray[1]);
                        createOverlay(numberOfConnections);
                    }catch (NumberFormatException nfe){
                        System.out.println("<number-of-connections> must be an integer.");
                    }
                }
                break;
            case "send-overlay-link-weights":
                break;
            case "start":
                break;
            default:
                if (string != ""){
                    System.out.println("Unknown command: " + string);
                }
        }
    }

    public Registry(int PortNumber) throws IOException{

        startServerThread(PortNumber);

        ConsoleThread terminal = new ConsoleThread(this);

        Thread thread = new Thread(terminal);
        thread.start();

        messagingNodes = new ArrayList<>();

        System.out.println("New registry node started, key: " + this.nodeKey);
    }

    public static void main(String [] args){
        if (args.length < 1){
            System.out.println("You must specify the port number.");
            System.exit(-1);
        }

        int Port = 0;

        try{
            Port = Integer.parseInt(args[0]);
        }catch (NumberFormatException fe){
            System.out.println("Error: Port number must be an integer.");
            System.exit(-1);
        }

        try {
            Registry registry = new Registry(Port);
        }catch (IOException ioe){
            System.out.println("Registry Error: " + ioe.getMessage());
        }
    }

}
