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
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = read(key, buf);
            closeClientIfEnd(bytesRead, key);
        }
    }

    private void switchToWrite(SelectionKey key) {
        selectorActions.add(() -> key.interestOps(SelectionKey.OP_WRITE));
        key.selector().wakeup();
    }

    private int read(SelectionKey key, ByteBuffer buf) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        int read = client.read(buf);
        if (read > 0) {
            pool.submit(() -> {
                writeToBuffer(key, buf);
            });
        }

        return read;
    }

    private void writeToBuffer(SelectionKey key, ByteBuffer buf) {
        SocketChannel client = (SocketChannel) key.channel();
        buf.flip();
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
        pendingData.get(client).add(buf);
        switchToWrite(key);
    }

    private void closeClientIfEnd(int read, SelectionKey key) throws IOException {
        if (read == -1) {
            SocketChannel client = (SocketChannel) key.channel();
            pendingData.remove(client);
            client.close();
        }
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
