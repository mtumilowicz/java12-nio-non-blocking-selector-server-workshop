package selector.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class PooledReadHandler {
    private final ExecutorService pool;
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;
    private final Queue<Runnable> selectorActions;

    public PooledReadHandler(ExecutorService pool,
                             Map<SocketChannel, Queue<ByteBuffer>> pendingData,
                             Queue<Runnable> selectorActions) {
        this.pool = pool;
        this.pendingData = pendingData;
        this.selectorActions = selectorActions;
    }

    public void handle(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocateDirect(80);
        int read = sc.read(buf);
        if (read == -1) {
            pendingData.remove(sc);
            return;
        }
        if (read > 0) {
            pool.submit(() -> {
                transform(buf, UnaryOperator.identity());
                pendingData.get(sc).add(buf);
                selectorActions.add(() -> key.interestOps(SelectionKey.OP_WRITE));
                key.selector().wakeup();
            });
        }
    }

    private static void transform(ByteBuffer buf, UnaryOperator<Byte> transformation) {
        buf.flip();
        IntStream.range(0, buf.limit()).forEach(i -> buf.put(i, transformation.apply(buf.get(i))));
    }
}
