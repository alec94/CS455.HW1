package cs455.overlay.wireformats;

/**
 * Created by Alec on 1/24/2017.
 */
public class Register_Response implements Event{
    Boolean Success;

    public EventType getType(){
        return EventType.Register_Response;
    }

    public byte[] getBytes(){
        byte[] Bytes = null;

        return Bytes;
    }

    Register_Response(Boolean Success){
        this.Success = Success;
    }
}
