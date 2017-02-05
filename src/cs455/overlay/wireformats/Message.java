package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;

/**
 * Created by Alec on 1/23/2017.
 */
public class Message implements Event {
    private EventType Type;

    public byte[] getBytes(){
        byte[] marshelledBytes = null;

        return marshelledBytes;
    }

    public EventType getType(){
        return this.Type;
    }
}
