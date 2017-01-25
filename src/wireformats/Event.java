package wireformats;

/**
 * Created by Alec on 1/23/2017.
 */
public interface Event {
    EventType getType();
    Byte[] getBytes();

    enum EventType{
          Deregister
        , LinkWeights
        , Message
        , MessagingNodesList
        , Protocol
        , Register
        , TaskComplete
        , TaskInitiate
        , TaskSummaryRequest
        , TaskSummaryResponse
    }
}
