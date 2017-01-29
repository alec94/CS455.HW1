package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Alec on 1/23/2017.
 * Messaging node
 */
public class MessagingNode implements Node {
    public int NodeID;
    private TCPSender registry;
    private ArrayList<Socket> senders;
    public void onEvent(Event event){

    }

    private void Register(String registryHost, int registyPort) throws IOException{
        registry = new TCPSender(new Socket(registryHost, registyPort));
    }

    private void addSocket(Socket socket){
        senders.add(socket);
    }

    private MessagingNode(String registryHost, int registryPort) throws IOException{

        try {
            //use 0 for automatic port number
            TCPServerThread serverThread = new TCPServerThread(0);
            Thread thread = new Thread(serverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

        this.Register(registryHost, registryPort);

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
