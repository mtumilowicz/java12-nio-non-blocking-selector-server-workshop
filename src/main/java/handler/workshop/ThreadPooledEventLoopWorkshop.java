package handler.workshop;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mtumilowicz on 2019-07-30.
 */
public class ThreadPooledEventLoopWorkshop {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final PendingMessagesWorkshop pendingMessages = new PendingMessagesWorkshop();
    private final Queue<Runnable> switchKeysToWriteActions = new ConcurrentLinkedQueue<>();
    private final ClientConnectionWorkshop clientConnection = new ClientConnectionWorkshop(pendingMessages);
    private final ThreadPooledIncomingMessageWorkshop incomingMessage = new ThreadPooledIncomingMessageWorkshop(pool, pendingMessages, switchKeysToWriteActions);
    private final OutgoingMessageWorkshop outgoingMessage = new OutgoingMessageWorkshop(pendingMessages);

    public void runOver(Selector selector) throws IOException {
        while (true) {
            // select keys whose corresponding channels are ready for I/O operations, hint: selector
            // get keys, hint: selector.selectedKeys()
            // switch keys and clear actions, hint: switchKeysToWriteAndClear
            // for each key run its operation, hint: runOperationOf
            // clear keys
        }
    }

    private void switchKeysToWriteAndClear() {
        // hint: switchKeysToWriteActions, Runnable::run
        // clear
    }

    private void runOperationOf(SelectionKey key) {
        try {
            // accept client connection, hint: clientConnection, tryAccept
            // receive message from client, hint: incomingMessage, tryReceive
            // send message to client, hint: outgoingMessage, trySend
        } catch (Exception ex) {
            // workshops
        }
    }
}
