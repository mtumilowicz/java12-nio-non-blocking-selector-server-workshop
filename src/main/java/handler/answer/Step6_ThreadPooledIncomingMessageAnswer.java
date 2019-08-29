package handler.answer;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

class Step6_ThreadPooledIncomingMessageAnswer extends Step4_IncomingMessageAnswer {
    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;

    Step6_ThreadPooledIncomingMessageAnswer(ExecutorService pool,
                                            Step1_PendingMessagesAnswer pendingMessages,
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
    void handleIncomingMessage(SocketChannel client, ByteBuffer buffer, SelectionKey key) {
        pool.submit(() -> super.handleIncomingMessage(client, buffer, key));
    }
}
