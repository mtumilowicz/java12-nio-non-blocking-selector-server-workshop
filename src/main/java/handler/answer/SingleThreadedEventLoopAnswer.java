package handler.answer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class SingleThreadedEventLoopAnswer {
    private final PendingMessagesAnswer pendingMessages = new PendingMessagesAnswer();
    private final ClientConnectionAnswer clientConnection = new ClientConnectionAnswer(pendingMessages);
    private final SingleThreadedIncomingMessageAnswer incomingMessage = new SingleThreadedIncomingMessageAnswer(pendingMessages);
    private final OutgoingMessageAnswer outgoingMessage = new OutgoingMessageAnswer(pendingMessages);

    public final void runOver(Selector selector) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(this::runOperationOf);
            keys.clear();
        }
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
