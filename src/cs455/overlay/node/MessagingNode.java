package cs455.overlay.node;

import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Register;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.plugin.viewer.IExplorerPluginObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

/**
 * Created by Alec on 1/23/2017.
 * Messaging node
 */
public class MessagingNode extends BaseNode {
    private ConsoleThread terminal;
    private TCPServerThread serverThread;

    public synchronized void onEvent(Event event, String socketKey){

        switch (event.getType()) {
            case LinkWeights:
                break;
            case Message:
                break;
            case MessagingNodesList:
                createOverlayConnections(((MessagingNodesList) event).getKeys());
                break;

            case TaskComplete:
                break;
            case TaskInitiate:
                break;
            case TaskSummaryRequest:
                break;
            case TaskSummaryResponse:
                break;
            case Register:
                event = (Register) event;
                String newKey = ((Register) event).IPAddress + ":" + ((Register) event).Port;
                //System.out.println("Register event received, " + newKey);
                updateKey(socketKey, newKey);

                break;
        }
    }

    private void createOverlayConnections(String[] keys){

        System.out.println("Setting up overlay connections to " + Arrays.toString(keys));

        for (String key : keys) {
            String [] params = key.split(":");
            String IPAddress = params[0];
            int Port = Integer.parseInt(params[1]);

            try {
                addSocket(new Socket(IPAddress, Port),key);

                String[] registerParams = this.nodeKey.split(":");

                senders.get(key).sendData(new Register(registerParams[0],Integer.parseInt(registerParams[1])).getBytes());

            }catch (IOException ioe){
                System.out.print("Error opening socket to " + key +": " + ioe.getMessage());
            }
        }
    }

    private void Register(String registryHost, int registyPort) throws IOException{
        String key = registryHost + ":" + registyPort;
        addSocket(new Socket(registryHost, registyPort),key);

        String[] registerParams = this.nodeKey.split(":");

        senders.get(key).sendData(new Register(registerParams[0],Integer.parseInt(registerParams[1])).getBytes());
    }

    public void handleConsoleInput(String string){
        String[] inputArray = string.split(" ");

        switch (inputArray[0]){
            case "print-shortest-path":
                break;
            case "exit-overlay":
                break;
            default:
                System.out.println("Unknown command: " + string);
        }
    }

    private MessagingNode(String registryHost, int registryPort) throws IOException{

        //port = 0 for automatic available port
        startServerThread(0);

        this.Register(registryHost, registryPort);

        this.terminal = new ConsoleThread(this);

        Thread thread = new Thread(this.terminal);
        thread.start();

        System.out.println("New messaging node started, key: " + this.nodeKey);

    }

   public static void main(String[] args){
        if (args.length > 2){
            System.out.println("You must provide the host and port for the registry.");
        }
        int RegistryPort = 0;

        try {
           RegistryPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException fe){
            System.out.println("Error: port number must be an integer.");
            System.exit(-1);
        }

        try {
            MessagingNode messagingNode = new MessagingNode(args[0], RegistryPort);
        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

   }
}
