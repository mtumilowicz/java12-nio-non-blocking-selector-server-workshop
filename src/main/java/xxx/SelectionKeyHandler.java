package xxx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Created by mtumilowicz on 2019-07-25.
 */
public class SelectionKeyHandler {
    public static void handle(SelectionKey key) {
        Map<SocketChannel, Queue<ByteBuffer>> dataToHandle = new HashMap<>();
        AcceptHandler acceptHandler = new AcceptHandler(dataToHandle);
        ReadHandler readHandler = new ReadHandler(dataToHandle);
        WriteHandler writeHandler = new WriteHandler(dataToHandle);

        try {
            if (key.isAcceptable()) {
                acceptHandler.handle(key);
            } else if (key.isReadable()) {
                readHandler.handle(key);
            } else if (key.isWritable()) {
                writeHandler.handle(key);
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
            read(key, buf, client, dataToHandle);
        }
        closeAndRemoveClientIfEnd(read, client, dataToHandle);
    }

    private static void read(SelectionKey key, ByteBuffer buf, SocketChannel client, Map<SocketChannel, Queue<ByteBuffer>> dataToHandle) throws IOException {
        buf.flip();
        transformBytesInBuffer(UnaryOperator.identity(), buf);
        dataToHandle.get(client).add(buf);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void transformBytesInBuffer(UnaryOperator<Byte> transformation, ByteBuffer buf) {
        IntStream.range(0, buf.limit()).forEach(i -> buf.put(i, transformation.apply(buf.get(i))));
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
