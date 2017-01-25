package wireformats;

/**
 * Created by Alec on 1/24/2017.
 */
public class Register_Response implements Event{
    Boolean Success;

    public EventType getType(){
        return EventType.Register;
    }

    public Byte[] getBytes(){
        Byte[] Bytes = null;

        return Bytes;
    }

    Register_Response(Boolean Success){
        this.Success = Success;
    }
}
