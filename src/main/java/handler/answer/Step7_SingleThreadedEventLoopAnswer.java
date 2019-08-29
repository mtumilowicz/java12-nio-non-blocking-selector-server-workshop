package handler.answer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class Step7_SingleThreadedEventLoopAnswer {
    private final Step1_PendingMessagesAnswer pendingMessages = new Step1_PendingMessagesAnswer();
    private final Step2_ClientConnectionAnswer clientConnection = new Step2_ClientConnectionAnswer(pendingMessages);
    private final Step5_SingleThreadedIncomingMessageAnswer incomingMessage = new Step5_SingleThreadedIncomingMessageAnswer(pendingMessages);
    private final Step3_OutgoingMessageAnswer outgoingMessage = new Step3_OutgoingMessageAnswer(pendingMessages);

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
