package handler.workshop;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mtumilowicz on 2019-07-30.
 */
public class ThreadPooledEventLoopWorkshop {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final PendingMessagesWorkshop pendingMessages = new PendingMessagesWorkshop();
    private final Queue<Runnable> switchKeysActions = new ConcurrentLinkedQueue<>();
    private final ClientConnectionWorkshop clientConnection = new ClientConnectionWorkshop(pendingMessages);
    private final ThreadPooledIncomingMessageWorkshop incomingMessage = new ThreadPooledIncomingMessageWorkshop(pool, pendingMessages, switchKeysActions);
    private final OutgoingMessageWorkshop outgoingMessage = new OutgoingMessageWorkshop(pendingMessages);

    public void runOver(Selector selector) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            switchKeysAndClearActions();
            keys.forEach(this::runOperationOf);
            keys.clear();
        }
    }

    private void switchKeysAndClearActions() {
        switchKeysActions.forEach(Runnable::run);
        switchKeysActions.clear();
    }

    private void runOperationOf(SelectionKey key) {
        try {
            clientConnection.tryAccept(key);
            incomingMessage.tryReceive(key);
            outgoingMessage.trySend(key);
        } catch (Exception ex) {
            // workshops
        }
    }
}
