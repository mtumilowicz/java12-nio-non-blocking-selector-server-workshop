package handler.workshop;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

class ThreadPooledIncomingMessageWorkshop extends IncomingMessageWorkshop {
    private final ExecutorService pool;
    private final Queue<Runnable> selectorActions;

    ThreadPooledIncomingMessageWorkshop(ExecutorService pool,
                                        PendingMessagesWorkshop pendingMessages,
                                        Queue<Runnable> selectorActions) {
        super(pendingMessages);
        this.pool = pool;
        this.selectorActions = selectorActions;
    }

    @Override
    void switchToWrite(SelectionKey key) {
        // queue switching action, hint: selectorActions.add, key.interestOps, OP_WRITE
        // wakeup selector, hint: key.selector().wakeup()
    }

    @Override
    void handleIncomingMessage(SocketChannel client, ByteBuffer buffer, SelectionKey key) {
        // submit handlingIncomingMessage to the pool
    }
}
