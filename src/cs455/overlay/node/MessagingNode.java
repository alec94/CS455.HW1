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
import java.util.ConcurrentModificationException;

/**
 * Created by Alec on 1/23/2017.
 * Messaging node
 */
public class MessagingNode implements Node {
    public int NodeID;
    private ConsoleThread terminal;
    private TCPServerThread serverThread;

    public synchronized void onEvent(Event event){

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
        }
    }

    private void createOverlayConnections(String[] keys){
        for (String key : keys) {
            String [] params = key.split(":");
            String IPAddress = params[0];
            int Port = Integer.parseInt(params[1]);

            try {
                Socket tmpSocket = new Socket(IPAddress, Port);

                senders.put(key, new TCPSender(tmpSocket));

                TCPReceiverThread receiverThread = new TCPReceiverThread(tmpSocket, this);
                Thread thread = new Thread(receiverThread);
                thread.start();

            }catch (IOException ioe){
                System.out.print("Error opening socket to " + key +": " + ioe.getMessage());
            }
        }
    }

    private void Register(String registryHost, int registyPort) throws IOException{
        TCPSender tmpSender = new TCPSender(new Socket(registryHost, registyPort));

        tmpSender.sendData(new Register(InetAddress.getLocalHost().getHostAddress().replace("/",""),this.serverThread.getPort()).getBytes());

        tmpSender.close();
    }

    private void createReceiver(Socket socket){
        try {
            TCPReceiverThread receiverThread = new TCPReceiverThread(socket, this);

            Thread thread = new Thread(receiverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println("Error creating new TCPRecieverThread: " + ioe.getMessage());
        }
    }

    public synchronized void addSocket(Socket socket){

        createReceiver(socket);

        String key = socket.getInetAddress() + ":" + socket.getPort();

        if (!senders.containsKey(key)) {
            try {
                senders.put(key, new TCPSender(socket));
            }catch (IOException ioe){
                System.out.println("Error creating new TCPSender. " + ioe.getMessage());
            }

        }
    }

    public synchronized void removeSocket(Socket socket){
        String key = socket.getInetAddress() + ":" + socket.getPort();

        if (senders.containsKey(key)){
            senders.remove(key);
        }else{
            System.out.println("Unable to remove socket for " + key);
        }
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

        try {
            //use 0 for automatic port number
            this.serverThread = new TCPServerThread(0,this);
            Thread thread = new Thread(serverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

        this.Register(registryHost, registryPort);

        this.terminal = new ConsoleThread(this);

        Thread thread = new Thread(this.terminal);
        thread.start();

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
