package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * Contains network stats from a messagingNode
 */
public class TaskSummaryResponse implements Event {
    private final EventType Type;
    private final int sendTracker;
    private final int receiveTracker;
    private final int relayTracker;
    private final long receiveSummation;
    private final long sendSummation;
    private final String sourceKey;

    public TaskSummaryResponse(String sourceKey, int sendTracker, int receiveTracker, int relayTracker, long sendSummation, long receiveSummation) {
        this.sourceKey = sourceKey;
        this.sendTracker = sendTracker;
        this.receiveTracker = receiveTracker;
        this.relayTracker = relayTracker;
        this.sendSummation = sendSummation;
        this.receiveSummation = receiveSummation;
        this.Type = EventType.TaskSummaryResponse;
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            //write source key
            byte[] sourceKeyBytes = this.sourceKey.getBytes("UTF-8");
            dataOutputStream.writeInt(sourceKeyBytes.length);
            dataOutputStream.write(sourceKeyBytes);

            //write network stats
            dataOutputStream.writeInt(this.sendTracker);
            dataOutputStream.writeInt(this.receiveTracker);
            dataOutputStream.writeInt(this.relayTracker);
            dataOutputStream.writeLong(this.sendSummation);
            dataOutputStream.writeLong(this.receiveSummation);

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();

        } catch (IOException ioe) {
            System.out.println("Error getting bytes for " + Type + " event. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public EventType getType() {
        return this.Type;
    }

    public int getSendTracker() {
        return this.sendTracker;
    }

    public int getReceiveTracker() {
        return this.receiveTracker;
    }

    public int getRelayTracker() {
        return this.relayTracker;
    }

    public long getSendSummation() {
        return this.sendSummation;
    }

    public long getReceiveSummation() {
        return this.receiveSummation;
    }

    public String getSourceKey() {
        return this.sourceKey;
    }
}
