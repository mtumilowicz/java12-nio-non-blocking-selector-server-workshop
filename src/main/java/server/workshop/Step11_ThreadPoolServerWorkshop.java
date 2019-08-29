package server.workshop;

import server.answers.Step9_ServerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class Step11_ThreadPoolServerWorkshop extends Step9_ServerWorkshop {

    public Step11_ThreadPoolServerWorkshop(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        // run event loop over selector, hint: ThreadPooledEventLoopWorkshop.runOver
    }

    public static void main(String[] args) throws IOException {
        new Step11_ThreadPoolServerWorkshop(81).start();
    }
}