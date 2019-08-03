package selector.handler;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

class PooledReadHandlerAnswer {
    private final ExecutorService pool;
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;
    private final Queue<Runnable> selectorActions;

    PooledReadHandlerAnswer(ExecutorService pool,
                            Map<SocketChannel, Queue<ByteBuffer>> pendingData,
                            Queue<Runnable> selectorActions) {
        this.pool = pool;
        this.pendingData = pendingData;
        this.selectorActions = selectorActions;
    }

    void handle(SelectionKey key) throws IOException {
        if (key.isValid() && key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int read = sc.read(buf);
            if (read == -1) {
                pendingData.remove(sc);
                return;
            }
            if (read > 0) {
                pool.submit(() -> {
                    buf.flip();
                    BufferTransformer.transformBytes(buf, UnaryOperator.identity());
                    pendingData.get(sc).add(buf);
                    selectorActions.add(() -> key.interestOps(SelectionKey.OP_WRITE));
                    key.selector().wakeup();
                });
            }
        }
    }
}
