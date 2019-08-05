package handler.workshop;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class SingleThreadedEventLoopWorkshop {
    private final PendingMessagesWorkshop pendingMessages = new PendingMessagesWorkshop();
    private final ClientConnectionWorkshop clientConnection = new ClientConnectionWorkshop(pendingMessages);
    private final SingleThreadedIncomingMessageWorkshop incomingMessage = new SingleThreadedIncomingMessageWorkshop(pendingMessages);
    private final OutgoingMessageWorkshop outgoingMessage = new OutgoingMessageWorkshop(pendingMessages);

    public final void runOver(Selector selector) throws IOException {
        while (true) {
            // select keys whose corresponding channels are ready for I/O operations, hint: selector
            // get keys, hint: selector.selectedKeys()
            // for each key run its operation, hint: runOperationOf
            // clear keys
        }
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
