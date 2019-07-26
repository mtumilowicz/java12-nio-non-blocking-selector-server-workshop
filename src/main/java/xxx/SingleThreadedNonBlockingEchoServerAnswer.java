package xxx;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by mtumilowicz on 2019-07-24.
 */
public class SingleThreadedNonBlockingEchoServerAnswer {

    private final int port;

    public SingleThreadedNonBlockingEchoServerAnswer(int port) {
        this.port = port;
    }

    public SingleThreadedNonBlockingEchoServerAnswer() {
        this.port = 81;
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedNonBlockingEchoServerAnswer().start();
    }

    public void start() throws IOException {
        log("Creating server socket on port " + port);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);
        log("Created server socket on port " + port);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
        AcceptHandler acceptHandler = new AcceptHandler(pendingData);
        ReadHandler readHandler = new ReadHandler(pendingData);
        WriteHandler writeHandler = new WriteHandler(pendingData);

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        acceptHandler.handle(key);
                    } else if (key.isReadable()) {
                        readHandler.handle(key);
                    } else if (key.isWritable()) {
                        writeHandler.handle(key);
                    }
                }
            }
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
