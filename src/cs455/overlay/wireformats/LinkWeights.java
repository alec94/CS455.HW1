package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * Contains the information on link weights and list of all messaging nodes
 */
public class LinkWeights implements Event {
    private EventType Type;
    private int[][] linkWeights;
    private String[] nodeList;

    public byte[] getBytes(){
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            dataOutputStream.writeInt(nodeList.length);

            //write link weights from adjacency matrix
            for (int i = 0; i < nodeList.length; i++){
                for (int j = 0; j < nodeList.length; j++){
                    dataOutputStream.writeInt(linkWeights[i][j]);
                }
            }

            for (String node : nodeList){
                byte[] nodeBytes = node.getBytes();
                dataOutputStream.writeInt(nodeBytes.length);
                dataOutputStream.write(nodeBytes);
            }

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();
        }catch (IOException ioe){
            System.out.println("Error marshalling bytes for LinkWeights. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public EventType getType(){
        return this.Type;
    }

    public int[][] getLinkWeights(){
        return this.linkWeights;
    }

    public String[] getNodeList(){
        return this.nodeList;
    }

    public LinkWeights(int[][] weights, String[] nodeList){
        this.linkWeights = weights;
        this.nodeList = nodeList;
        this.Type = EventType.LinkWeights;
    }
}
