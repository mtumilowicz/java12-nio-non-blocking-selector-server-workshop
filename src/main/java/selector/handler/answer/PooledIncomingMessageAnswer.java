package selector.handler.answer;

import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

class PooledIncomingMessageAnswer extends IncomingMessageAnswer {
    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;

    PooledIncomingMessageAnswer(ExecutorService pool,
                                PendingMessagesAnswer pendingMessages,
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
    void handleIncomingMessage(Runnable prepareForSending, Runnable switchToWrite) {
        pool.submit(() -> {
            prepareForSending.run();
            switchToWrite.run();
        });
    }

}
