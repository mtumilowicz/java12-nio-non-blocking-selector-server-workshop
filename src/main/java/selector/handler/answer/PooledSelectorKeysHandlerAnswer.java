package selector.handler.answer;

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
public class PooledSelectorKeysHandlerAnswer {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final PendingMessagesAnswer pendingMessages = new PendingMessagesAnswer();
    private final Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();
    private final AcceptHandlerAnswer acceptHandler = new AcceptHandlerAnswer(pendingMessages);
    private final PooledReadHandlerAnswer readHandler = new PooledReadHandlerAnswer(pool, pendingMessages, selectorActions);
    private final WriteHandlerAnswer writeHandler = new WriteHandlerAnswer(pendingMessages);

    public void handle(Selector selector) throws IOException {
        while (true) {
            selector.select();
            runAndClearSelectorActions();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(this::handleKey);
            keys.clear();
        }
    }

    private void runAndClearSelectorActions() {
        selectorActions.forEach(Runnable::run);
        selectorActions.clear();
    }

    private void handleKey(SelectionKey key) {
        try {
            acceptHandler.handle(key);
            readHandler.handle(key);
            writeHandler.handle(key);
        } catch (Exception ex) {
            // workshops
        }
    }
}
