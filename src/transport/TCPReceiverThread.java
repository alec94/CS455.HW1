package transport;

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
    private DataInputStream dataInputStream;

    public TCPReceiverThread(Socket socket) throws IOException{
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void run(){
        int dataLength;

        while (socket != null){
            try{
                dataLength = dataInputStream.readInt();

                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);
            }catch (SocketException se){
                System.out.println(se.getMessage());
                break;
            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
                break;
            }
        }
    }
}
