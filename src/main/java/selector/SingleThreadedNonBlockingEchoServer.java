package selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by mtumilowicz on 2019-07-24.
 */
public class SingleThreadedNonBlockingEchoServer {

    private final int port;

    public SingleThreadedNonBlockingEchoServer(int port) {
        this.port = port;
    }

    public SingleThreadedNonBlockingEchoServer() {
        this.port = 81;
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedNonBlockingEchoServer().start();
    }

    public void start() throws IOException {
        log("Creating server socket on port " + port);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);
        log("Created server socket on port " + port);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            var keys = selector.selectedKeys();
            keys.stream().filter(SelectionKey::isValid).forEach(SelectionKeyHandler::handle);
            keys.clear();
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
