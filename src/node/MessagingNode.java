package node;

import transport.TCPReceiverThread;
import transport.TCPSender;
import wireformats.Event;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alec on 1/23/2017.
 */
public class MessagingNode implements Node {
    public int NodeID;
    private Socket socket;
    private TCPSender sender;
    private TCPReceiverThread receiver;

    public void onEvent(Event event){

    }

    private void Register(){

    }

   public MessagingNode(int NodeID,String Hostname) throws IOException{
        this.NodeID = NodeID;
        this.socket = new Socket();
        this.sender = new TCPSender(this.socket);
        this.receiver = new TCPReceiverThread(new ServerSocket());
        receiver.run();

   }
}
