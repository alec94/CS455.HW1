package cs455.overlay.wireformats;

/**
 * Created by Alec on 1/23/2017.
 */
public class TaskInitiate implements Event {
    private EventType Type;

    public byte[] getBytes(){
        byte[] marshelledBytes = null;

        return marshelledBytes;
    }

    public EventType getType(){
        return this.Type;
    }
}
