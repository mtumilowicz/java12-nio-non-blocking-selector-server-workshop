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
import java.util.Iterator;
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
    ssc.bind(new InetSocketAddress(8080));
    ssc.configureBlocking(false);
    Selector selector = Selector.open();
    ssc.register(selector, SelectionKey.OP_ACCEPT);

    ExecutorService pool = Executors.newFixedThreadPool(10);
    Queue<Runnable> selectorActions = new ConcurrentLinkedQueue<>();

    Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
    AcceptHandler acceptHandler = new AcceptHandler(pendingData);
    PooledReadHandler readHandler = new PooledReadHandler(pool, pendingData, selectorActions);
    WriteHandler writeHandler = new WriteHandler(pendingData);

    while (true) {
      selector.select();
      processSelectorActions(selectorActions);
      Set<SelectionKey> keys = selector.selectedKeys();
      for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
        SelectionKey key = it.next();
        it.remove();
        if (key.isValid()) {
          if (key.isAcceptable()) {
            acceptHandler.handle(key);
          } else if (key.isReadable()) {
            readHandler.handle(key);
          } else if (key.isWritable()) {
            writeHandler.handle(key);
          }
        }
      }
    }
  }

  private static void processSelectorActions(Queue<Runnable> selectorActions) {
    Runnable action;
    while((action = selectorActions.poll()) != null) {
      action.run();
    }
  }
}