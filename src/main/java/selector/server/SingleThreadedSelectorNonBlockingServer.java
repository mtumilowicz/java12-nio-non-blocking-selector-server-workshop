package selector.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SingleThreadedSelectorNonBlockingServer {

    public static void main(String[] args) throws IOException {
        SingleThreadedSelectorNonBlockingServer.start();
    }

    public static void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(81));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        SelectorKeysHandler.handle(selector);
    }
}