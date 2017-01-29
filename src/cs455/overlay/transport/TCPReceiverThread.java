package cs455.overlay.transport;

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

    public TCPReceiverThread(Socket socket) throws IOException{
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
        System.out.println("New ReceiverThread communicating with " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public void run() {
        int dataLength;
        while(socket != null){
            try{
                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data,0,dataLength);

            }catch (SocketException se){
                System.out.println("TCPReciverThread SocketException: " + se.getMessage());
                break;
            }catch (IOException ioe){
                System.out.println("TCPRecieverThread IOException: " + ioe.getMessage());
                break;
            }


        }
    }

}
