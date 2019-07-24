package selector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mtumilowicz on 2019-07-24.
 */
public class AcceptHandler {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    public void handle(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept(); // never null, nonblocking
        System.out.println("Someone connected: " + sc);
        sc.configureBlocking(false);
        pendingData.put(sc, new ConcurrentLinkedQueue<>());

        sc.register(key.selector(), SelectionKey.OP_READ);
    }
}
