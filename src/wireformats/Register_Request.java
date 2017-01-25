package wireformats;

import java.util.IntSummaryStatistics;

/**
 * Created by Alec on 1/23/2017.
 */
public class Register_Request implements Event {
    public int Port;
    public String IPAddress;

    public EventType getType(){
        return EventType.Register;
    }

    public Byte[] getBytes(){
        Byte[] Bytes = null;

        return Bytes;
    }

    Register_Request(int Port, String IPAddress){
        this.Port = Port;
        this.IPAddress = IPAddress;
    }
}
