package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alec on 1/23/2017.
 */
public class TCPServerThread implements Runnable{
    private ServerSocket serverSocket;
    private int port;

    public TCPServerThread(int port) throws IOException {
        this.port = port;
    }

    public void run(){
        while (true) {
            try {
                this.serverSocket = new ServerSocket(this.port);
                System.out.println("New serverSocket listening on " + serverSocket.getInetAddress() +":" + serverSocket.getLocalPort());

                Socket socket = serverSocket.accept();

                TCPReceiverThread receiverThread = new TCPReceiverThread(socket);
                Thread t = new Thread(receiverThread);
                t.start();

            }catch (IOException ioe){
                System.out.println("TCPServerThread: " + ioe.getMessage());
            }
        }
    }
}
