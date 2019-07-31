package selector.server;

import selector.handler.PooledSelectorKeysHandler;
import server.XServer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SelectorNonBlockingServerWithWorkerPool extends XServer {

    public SelectorNonBlockingServerWithWorkerPool(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        new SelectorNonBlockingServerWithWorkerPool(81).start();
    }

    @Override
    protected void processSockets(ServerSocketChannel ssc) throws IOException {
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        new PooledSelectorKeysHandler().handle(selector);
    }

    private void log(String message) {
        System.out.println(message);
    }
}