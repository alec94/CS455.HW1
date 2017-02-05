package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alec on 1/23/2017.
 */
public class TCPServerThread implements Runnable {
    private ServerSocket serverSocket;
    private int port;
    private Node parentNode;

    public TCPServerThread(int port, Node parentNode) throws IOException {
        this.port = port;
        this.parentNode = parentNode;
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    public String getIPAddress(){
        return serverSocket.getInetAddress().getHostAddress();
    }
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.println("New serverSocket listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            while (true) {

                try {
                    Socket socket = serverSocket.accept();

                    this.parentNode.addSocket(socket);
                }catch (Exception e){
                    System.out.println("Exception in TCPServerThread loop: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException ioe) {
            System.out.println("TCPServerThread: " + ioe.getMessage());
        }

    }
}
