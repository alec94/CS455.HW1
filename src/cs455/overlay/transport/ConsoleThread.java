package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.util.Scanner;

/**
 * Created by Alec on 1/30/2017.
 * reads input from the console and passes it to the node
 */
public class ConsoleThread implements Runnable {
    private final Node parentNode;

    public ConsoleThread(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            this.parentNode.handleConsoleInput(line);
        }
    }
}
