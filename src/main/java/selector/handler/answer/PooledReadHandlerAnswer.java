package selector.handler.answer;

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
        if (canBeRead(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = read(key, client, buf);
            closeClientIfEnd(bytesRead, client);
        }
    }

    private void wakeupSelector(SelectionKey key) {
        key.selector().wakeup();
    }

    private void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private int read(SelectionKey key, SocketChannel client, ByteBuffer buf) throws IOException {
        int read = client.read(buf);
        if (read > 0) {
            pool.submit(() -> {
                buf.flip();
                BufferTransformer.transformBytes(buf, UnaryOperator.identity());
                pendingData.get(client).add(buf);
                selectorActions.add(() -> this.switchToWrite(key));
                wakeupSelector(key);
            });
        }

        return read;
    }

    private void closeClientIfEnd(int read, SocketChannel client) throws IOException {
        if (read == -1) {
            pendingData.remove(client);
            client.close();
        }
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
