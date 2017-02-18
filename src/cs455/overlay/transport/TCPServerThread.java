package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Alec on 1/23/2017.
 * Listens for new connections with a server socket
 */
public class TCPServerThread implements Runnable {
    private ServerSocket serverSocket;
    private final int port;
    private final Node parentNode;

    public TCPServerThread(int port, Node parentNode) {
        this.port = port;
        this.parentNode = parentNode;
    }

    //gets key for this serverSocket, this is used as the identifier for this node.
    public String getKey() {
        String key = "";

        try {
            key = InetAddress.getLocalHost().getHostAddress().replace("/", "") + ":" + serverSocket.getLocalPort();
        } catch (UnknownHostException e) {
            System.out.println("Error getting host for serverSocket, " + e.getMessage());
        }

        return key;
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            //System.out.println("New serverSocket listening on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
            while (true) {

                try {
                    Socket socket = serverSocket.accept();
                    this.parentNode.addSocket(socket, socket.getInetAddress() + ":" + socket.getPort());
                } catch (Exception e) {
                    System.out.println("Exception in TCPServerThread loop: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException ioe) {
            System.out.println("TCPServerThread: " + ioe.getMessage());
        }

    }
}
