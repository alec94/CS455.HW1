package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Alec on 1/23/2017.
 * Registry Node
 */
public class Registry implements Node {
    private ArrayList<Socket> MessagingNodes;
    private TCPReceiverThread receiver;

    public void onEvent(Event event){

    }

    public Registry(int PortNumber) throws IOException{

        try {
            TCPServerThread serverThread = new TCPServerThread(PortNumber);
            Thread thread = new Thread(serverThread);
            thread.start();

        }catch (IOException ioe){
            System.out.println("TCPServerThread Start Error: " + ioe.getMessage());
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
