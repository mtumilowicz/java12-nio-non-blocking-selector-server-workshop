package selector.server;

import selector.handler.SelectorKeysHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SingleThreadedSelectorNonBlockingServer {

    private final int port;

    public SingleThreadedSelectorNonBlockingServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedSelectorNonBlockingServer(81).start();
    }

    public void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        new SelectorKeysHandler().handle(selector);
    }
}