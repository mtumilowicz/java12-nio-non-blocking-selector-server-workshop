package handler.answers;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Step3_OutgoingMessageAnswer {
    private final Step1_PendingMessagesAnswer pendingMessages;

    Step3_OutgoingMessageAnswer(Step1_PendingMessagesAnswer pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void trySend(SelectionKey key) throws IOException {
        if (canBeWritten(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            pendingMessages.sendTo(client);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private boolean canBeWritten(SelectionKey key) {
        return key.isValid() && key.isWritable();
    }

}
