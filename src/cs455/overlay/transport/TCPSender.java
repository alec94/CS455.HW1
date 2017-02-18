package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Alec on 1/23/2017.
 * Handles sending data to a socket
 */
public class TCPSender {
    private final Socket socket;
    private DataOutputStream dataOutputStream;
    //socketKey is the unique identifier for the node the socket is connected to
    private String socketKey;

    public TCPSender(Socket socket, String socketKey) throws IOException {
        this.socket = socket;
        this.socketKey = socketKey;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        //System.out.println("New TCPSender runningTask to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
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

    public synchronized void sendData(byte[] data) {
        int dataLength = data.length;
        try {
            dataOutputStream.writeInt(dataLength);
            dataOutputStream.write(data, 0, dataLength);
            dataOutputStream.flush();
        } catch (IOException ioe) {
            System.out.println("TCPSender IOException: " + ioe.getMessage() + ", " + socketKey);

        }
    }
}
