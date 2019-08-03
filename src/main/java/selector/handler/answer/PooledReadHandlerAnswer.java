package selector.handler.answer;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

class PooledReadHandlerAnswer extends ReadHandlerAnswer {
    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;

    PooledReadHandlerAnswer(ExecutorService pool,
                            PendingMessages pendingMessages,
                            Queue<Runnable> selectorActions) {
        super(pendingMessages);
        this.pool = pool;
        this.selectorActions = selectorActions;
    }

    @Override
    void switchToWrite(SelectionKey key) {
        selectorActions.add(() -> key.interestOps(SelectionKey.OP_WRITE));
        key.selector().wakeup();
    }

    @Override
    void prepareConnectionForWriting(SelectionKey key, ByteBuffer buf) {
        pool.submit(() -> prepareForSendingToClient(key, buf));
    }
}
