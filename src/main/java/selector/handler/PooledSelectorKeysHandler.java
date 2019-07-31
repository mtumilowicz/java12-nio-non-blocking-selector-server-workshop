package selector.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mtumilowicz on 2019-07-30.
 */
public class PooledSelectorKeysHandler {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
    private final Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();
    private final AcceptHandler acceptHandler = new AcceptHandler(pendingData);
    private final PooledReadHandler readHandler = new PooledReadHandler(pool, pendingData, selectorActions);
    private final WriteHandler writeHandler = new WriteHandler(pendingData);

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
        } catch (
                Exception ex) {
            // workshops
        }
    }
}
