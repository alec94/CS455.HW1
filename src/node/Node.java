package node;

import wireformats.Event;

/**
 * Created by Alec on 1/23/2017.
 */
public interface Node {
    void onEvent(Event event);
}
