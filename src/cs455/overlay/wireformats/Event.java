package cs455.overlay.wireformats;

/**
 * Created by Alec on 1/23/2017.
 * base interface used by all event objects
 */
@SuppressWarnings("unused")
public interface Event {
    EventType getType();

    byte[] getBytes();

    enum EventType {
        Deregister, LinkWeights, Message, MessagingNodesList, Register, TaskComplete, TaskInitiate, TaskSummaryRequest, TaskSummaryResponse, KeyMessage
    }
}
