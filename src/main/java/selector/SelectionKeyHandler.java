package selector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mtumilowicz on 2019-07-25.
 */
public class SelectionKeyHandler {
    public static void handle(SelectionKey key) {
        Map<SocketChannel, Queue<ByteBuffer>> dataToHandle = new HashMap<>();

        try {
            if (key.isAcceptable()) {
                accept(key, dataToHandle);
            } else if (key.isReadable()) {
                read(key, dataToHandle);
            } else if (key.isWritable()) {
                write(key, dataToHandle);
            }
        } catch (IOException e) {
            // workshops
        }
    }

    public static void accept(SelectionKey key, Map<SocketChannel, Queue<ByteBuffer>> dataToHandle) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept(); // never null, nonblocking
        System.out.println("Someone connected: " + sc);
        sc.configureBlocking(false);
        dataToHandle.put(sc, new ConcurrentLinkedQueue<>());

        sc.register(key.selector(), SelectionKey.OP_READ);
    }

    public static void read(SelectionKey key, Map<SocketChannel, Queue<ByteBuffer>> dataToHandle) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocateDirect(80);
        int read = client.read(buf);
        if (read > 0) {
            buf.flip();
            for (int i = 0; i < buf.limit(); i++) {
                buf.put(i, buf.get(i));
            }
            dataToHandle.get(client).add(buf);
            key.interestOps(SelectionKey.OP_WRITE);
        }
        closeAndRemoveClientIfEnd(read, client, dataToHandle);
    }

    private static void closeAndRemoveClientIfEnd(int read, SocketChannel client, Map<SocketChannel, Queue<ByteBuffer>> dataToHandle) throws IOException {
        if (read == -1) {
            client.close();
            dataToHandle.remove(client);
        }
    }

    public static void write(SelectionKey key, Map<SocketChannel, Queue<ByteBuffer>> dataToHandle) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = dataToHandle.get(sc);
        while(!queue.isEmpty()) {
            ByteBuffer buf = queue.peek();
            int written = sc.write(buf);
            if (written == -1) {
                sc.close();
                dataToHandle.remove(sc);
                return;
            }
            if (buf.hasRemaining()) {
                return;
            } else {
                queue.remove();
            }
        }
        key.interestOps(SelectionKey.OP_READ);
    }
}
