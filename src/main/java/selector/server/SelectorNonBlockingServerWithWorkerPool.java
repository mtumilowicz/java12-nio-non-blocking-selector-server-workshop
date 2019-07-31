package selector.server;

import selector.handler.PooledSelectorKeysHandler;
import server.XNBServer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorNonBlockingServerWithWorkerPool extends XNBServer {

    public SelectorNonBlockingServerWithWorkerPool(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new PooledSelectorKeysHandler().handle(selector);

    }

    public static void main(String[] args) throws IOException {
        new SelectorNonBlockingServerWithWorkerPool(81).start();
    }

    private void log(String message) {
        System.out.println(message);
    }
}