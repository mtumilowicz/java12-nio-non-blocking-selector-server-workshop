package handler.answer;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class OutgoingMessageAnswer {
    private final PendingMessagesAnswer pendingMessages;

    OutgoingMessageAnswer(PendingMessagesAnswer pendingMessages) {
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
