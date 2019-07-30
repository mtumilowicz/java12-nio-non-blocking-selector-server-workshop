package selector.server;

import selector.handler.AcceptHandler;
import selector.handler.PooledReadHandler;
import selector.handler.WriteHandler;

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
    final ExecutorService pool = Executors.newFixedThreadPool(10);
    final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
    final Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();
    final AcceptHandler acceptHandler = new AcceptHandler(pendingData);
    final PooledReadHandler readHandler = new PooledReadHandler(pool, pendingData, selectorActions);
    final WriteHandler writeHandler = new WriteHandler(pendingData);

    public void handle(Selector selector) throws IOException {
        while (true) {
            selector.select();
            processSelectorActions();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(this::handleKey);
            keys.clear();
        }
    }

    public void processSelectorActions() {
        Runnable action;
        while ((action = selectorActions.poll()) != null) {
            action.run();
        }
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
