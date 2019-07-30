package selector.server;

import selector.handler.AcceptHandler;
import selector.handler.PooledReadHandler;
import selector.handler.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectorNonBlockingServerWithWorkerPool {
    public static void main(String... args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(81));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        
        Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();

        while (true) {
            selector.select();
            processSelectorActions(selectorActions);
            Set<SelectionKey> keys = selector.selectedKeys();
            new SelectorNonBlockingServerWithWorkerPool().handle(selector, selectorActions);
        }
    }

    final void handle(Selector selector, Queue<Runnable> selectorActions) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
        AcceptHandler acceptHandler = new AcceptHandler(pendingData);
        PooledReadHandler readHandler = new PooledReadHandler(pool, pendingData, selectorActions);
        WriteHandler writeHandler = new WriteHandler(pendingData);
        
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(key -> handleKey(key, acceptHandler, readHandler, writeHandler));
            keys.clear();
        }
    }

    private void handleKey(SelectionKey key, 
                           AcceptHandler acceptHandler,
                           PooledReadHandler readHandler,
                           WriteHandler writeHandler) {
        try {
            acceptHandler.handle(key);
            readHandler.handle(key);
            writeHandler.handle(key);
        } catch (
                Exception ex) {
            // workshops
        }
    }

    private static void processSelectorActions(Queue<Runnable> selectorActions) {
        selectorActions.forEach(Runnable::run);
        selectorActions.clear();
    }
}