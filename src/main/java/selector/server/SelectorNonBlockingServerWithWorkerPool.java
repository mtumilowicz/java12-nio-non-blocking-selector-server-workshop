package selector.server;

import selector.handler.PooledSelectorKeysHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SelectorNonBlockingServerWithWorkerPool {

    private final int port;

    public SelectorNonBlockingServerWithWorkerPool(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        new SelectorNonBlockingServerWithWorkerPool(81).start();
    }
    
    public void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        new PooledSelectorKeysHandler().handle(selector);
    }
}