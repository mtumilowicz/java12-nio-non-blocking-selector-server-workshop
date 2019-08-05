package handler.answer;

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
public class ThreadPooledEventLoopAnswer {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final PendingMessagesAnswer pendingMessages = new PendingMessagesAnswer();
    private final Queue<Runnable> switchKeysToWriteActions = new ConcurrentLinkedQueue<>();
    private final ClientConnectionAnswer clientConnection = new ClientConnectionAnswer(pendingMessages);
    private final ThreadPooledIncomingMessageAnswer incomingMessage = new ThreadPooledIncomingMessageAnswer(pool, pendingMessages, switchKeysToWriteActions);
    private final OutgoingMessageAnswer outgoingMessage = new OutgoingMessageAnswer(pendingMessages);

    public void runOver(Selector selector) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            switchKeysToWriteAndClear();
            keys.forEach(this::runOperationOf);
            keys.clear();
        }
    }

    private void switchKeysToWriteAndClear() {
        switchKeysToWriteActions.forEach(Runnable::run);
        switchKeysToWriteActions.clear();
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
