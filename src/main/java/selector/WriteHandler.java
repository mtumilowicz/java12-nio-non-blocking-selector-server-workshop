package selector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

/**
 * Created by mtumilowicz on 2019-07-24.
 */
public class WriteHandler {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    public void handle(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        Queue<ByteBuffer> queue = pendingData.get(sc);
        while(!queue.isEmpty()) {
            ByteBuffer buf = queue.peek();
            int written = sc.write(buf);
            if (written == -1) {
                sc.close();
                pendingData.remove(sc);
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
