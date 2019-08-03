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

class PooledReadHandlerAnswer extends XReadHandlerAnswer {
    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;

    PooledReadHandlerAnswer(ExecutorService pool,
                            Map<SocketChannel, Queue<ByteBuffer>> pendingData,
                            Queue<Runnable> selectorActions) {
        super(pendingData);
        this.pool = pool;
        this.selectorActions = selectorActions;
    }

    @Override
    void switchToWrite(SelectionKey key) {
        selectorActions.add(() -> key.interestOps(SelectionKey.OP_WRITE));
        key.selector().wakeup();
    }

    @Override
    void xxx(SelectionKey key, ByteBuffer buf) {
        pool.submit(() -> {
            writeToBuffer(key, buf);
        });
    }
}
