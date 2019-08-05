package server.workshop;

import server.answer.ServerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class ThreadPoolServerWorkshop extends ServerAnswer {

    public ThreadPoolServerWorkshop(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        // run event loop over selector, hint: ThreadPooledEventLoopWorkshop.runOver
    }

    public static void main(String[] args) throws IOException {
        new ThreadPoolServerWorkshop(81).start();
    }
}