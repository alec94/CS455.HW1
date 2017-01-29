package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Alec on 1/23/2017.
 */
public class TCPSender {
    private Socket socket;
    private DataOutputStream dataOutputStream;

    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        System.out.println("New TCPSender communicating with " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public int getPort() {
        return socket.getPort();
    }

    public void sendData(byte[] data) throws IOException {
        int dataLength = data.length;

        dataOutputStream.writeInt(dataLength);
        dataOutputStream.write(data, 0, dataLength);
        dataOutputStream.flush();
    }
}
