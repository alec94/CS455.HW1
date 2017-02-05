package cs455.overlay.node;

import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Register;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Alec on 1/23/2017.
 * Registry Node
 */
public class Registry implements Node {
    private ArrayList<String> messagingNodes;

    public synchronized void onEvent(Event event){
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

                if(senders.containsKey(key)){
                   if(!messagingNodes.contains(key)){
                       messagingNodes.add(key);
                       System.out.println("New Messaging node registered, " + key);
                   }
                }else {
                    System.out.println("Cannot find sender for key: " + key);
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

    private void createReceiver(Socket socket){
        try {
            TCPReceiverThread receiverThread = new TCPReceiverThread(socket, this);

            Thread thread = new Thread(receiverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println("Error creating new TCPRecieverThread: " + ioe.getMessage());
        }
    }

    public void addSocket(Socket socket){

        createReceiver(socket);

        String key = socket.getInetAddress() + ":" + socket.getPort();
        key = key.replace("/","");

        if (!senders.containsKey(key)) {
            try {
                senders.put(key, new TCPSender(socket));
            }catch (IOException ioe){
                System.out.println("Error creating new TCPSender. " + ioe.getMessage());
            }
        }
    }

    public Registry(int PortNumber) throws IOException{

        try {
            TCPServerThread serverThread = new TCPServerThread(PortNumber,this);
            Thread thread = new Thread(serverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println("TCPServerThread Start Error: " + ioe.getMessage());
        }

        ConsoleThread terminal = new ConsoleThread(this);

        Thread thread = new Thread(terminal);
        thread.start();

        messagingNodes = new ArrayList<>();
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
                break;
            case "send-overlay-link-weights":
                break;
            case "start":
                break;
            default:
                System.out.println("Unknown command: " + string);        
        }
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
