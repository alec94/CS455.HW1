package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Alec on 2/10/2017.
 * base node that Registry and MessagingNode both extend
 * contains methods used by both Registry and MessagingNode
 */
public abstract class NetworkNode implements Node {
    String nodeKey;

    void startServerThread(int port) {
            TCPServerThread serverThread = new TCPServerThread(port, this);

            Thread thread = new Thread(serverThread);
            thread.start();

            this.nodeKey = serverThread.getKey();
    }

    void sendToAll(byte[] data) {
        for (TCPSender sender : senders.values()) {
            sender.sendData(data);
        }
    }

    synchronized void updateKey(String oldKey, String newKey) {

        //System.out.println("Exchanging oldKey: " + oldKey + " for newKey: " + newKey);

        if (senders.containsKey(oldKey)) {
            senders.put(newKey, senders.remove(oldKey));
            senders.get(newKey).setSocketKey(newKey);
        } else {
            System.out.println("Could not find sender for " + oldKey);
            System.out.println("Available senders: " + senders.keySet().toString());
        }

        if (receivers.containsKey(oldKey)) {
            receivers.put(newKey, receivers.remove(oldKey));
            receivers.get(newKey).setSocketKey(newKey);
        } else {
            System.out.println("Could not find receiver for " + oldKey);
            System.out.println("Available receivers: " + receivers.keySet().toString());
        }
    }

    private void createReceiver(Socket socket, String key) {
        try {
            TCPReceiverThread receiverThread = new TCPReceiverThread(socket, this, key);
            receivers.put(key, receiverThread);
            Thread thread = new Thread(receiverThread);
            thread.start();

        } catch (IOException ioe) {
            System.out.println("Error creating new TCPReceiverThread: " + ioe.getMessage());
        }
    }

    public synchronized void addSocket(Socket socket, String socketKey) {
        //System.out.println("Adding socket, " + socketKey);

        createReceiver(socket, socketKey);

        if (!senders.containsKey(socketKey)) {
            try {
                senders.put(socketKey, new TCPSender(socket, socketKey));
            } catch (IOException ioe) {
                System.out.println("Error creating new TCPSender. " + ioe.getMessage());
            }
        }
    }

    public synchronized void removeSocket(String socketKey) {
        //System.out.println("Removing socket, " + socketKey);

        if (senders.containsKey(socketKey)) {
            senders.get(socketKey).close();
            senders.remove(socketKey);
        } else {
            System.out.println("Unable to remove sender for " + socketKey);
            System.out.println("Available senders: " + senders.keySet().toString());
        }

        if (receivers.containsKey(socketKey)) {
            receivers.get(socketKey).close();
            receivers.remove(socketKey);
        } else {
            System.out.println("Unable to remove receiver for " + socketKey);
            System.out.println("Available receivers: " + receivers.keySet().toString());
        }
    }
}
