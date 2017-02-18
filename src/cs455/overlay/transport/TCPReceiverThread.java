package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Alec on 1/23/2017.
 */
public class TCPReceiverThread implements Runnable {
    private final Socket socket;
    private DataInputStream din;
    private final Node parentNode;
    //socketKey is the unigue identifier for node the socket is connected to
    private String socketKey;

    public TCPReceiverThread(Socket socket, Node parentNode, String socketKey) throws IOException {
        this.socket = socket;
        this.parentNode = parentNode;
        this.socketKey = socketKey;
        din = new DataInputStream(socket.getInputStream());
        //System.out.println("New ReceiverThread receiving on " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
    }

    public void setSocketKey(String socketKey) {
        this.socketKey = socketKey;
    }

    public void close() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException ioe) {
            System.out.print("Error closing TCPSender: " + ioe.getMessage());
        }
    }

    public void run() {
        int dataLength;
        while (socket != null) {
            try {
                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);

                Event event = EventFactory.parseEvent(data);
                //System.out.println("Data received.");
                parentNode.onEvent(event, this.socketKey);

            } catch (SocketException se) {
                System.out.println("TCPReciverThread SocketException: " + se.getMessage() + ", " + socketKey);
                parentNode.removeSocket(socketKey);
                break;
            } catch (IOException ioe) {
                System.out.println("TCPRecieverThread IOException: " + ioe.getMessage() + ", " + socketKey);
                parentNode.removeSocket(socketKey);
                break;
            }
        }
    }
}
