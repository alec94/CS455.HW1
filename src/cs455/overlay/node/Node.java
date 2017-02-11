package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Alec on 1/23/2017.
 */
public interface Node {
    HashMap<String,TCPSender> senders = new HashMap<>();
    HashMap<String,TCPReceiverThread> receivers = new HashMap<>();
    void onEvent(Event event, String socketKey);
    void addSocket(Socket socket, String socketKey);
    void removeSocket(String socketKey);
    void handleConsoleInput(String input);
}
