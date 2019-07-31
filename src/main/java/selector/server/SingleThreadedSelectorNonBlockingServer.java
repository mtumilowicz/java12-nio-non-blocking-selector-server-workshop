package selector.server;

import selector.handler.SelectorKeysHandler;
import server.XNBServer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SingleThreadedSelectorNonBlockingServer extends XNBServer {

    public SingleThreadedSelectorNonBlockingServer(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new SelectorKeysHandler().handle(selector);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedSelectorNonBlockingServer(81).start();
    }

    private void log(String message) {
        System.out.println(message);
    }
}