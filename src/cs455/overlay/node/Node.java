package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;

import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Alec on 1/23/2017.
 */
public interface Node {
    HashMap<String,TCPSender> senders = new HashMap();
    void onEvent(Event event);
    void addSocket(Socket socket);
    void removeSocket(Socket socket);
    void handleConsoleInput(String input);
}
