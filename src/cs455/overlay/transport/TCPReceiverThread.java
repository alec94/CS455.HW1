package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Alec on 1/23/2017.
 */
public class TCPReceiverThread implements Runnable {
    private Socket socket;
    private DataInputStream din;
    private Node parentNode;

    public TCPReceiverThread(Socket socket, Node parentNode) throws IOException{
        this.socket = socket;
        this.parentNode = parentNode;
        din = new DataInputStream(socket.getInputStream());
        System.out.println("New ReceiverThread receiving on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
    }

    public int getPort(){return this.socket.getLocalPort();}

    public void run() {
        int dataLength;
        while(socket != null){
            try{
                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data,0,dataLength);

                Event event = EventFactory.parseEvent(data);

                parentNode.onEvent(event);

            }catch (SocketException se){
                System.out.println("TCPReciverThread SocketException: " + se.getMessage());
                parentNode.removeSocket(socket);
                break;
            }catch (IOException ioe){
                System.out.println("TCPRecieverThread IOException: " + ioe.getMessage());
                parentNode.removeSocket(socket);
                break;
            }
        }
    }
}
