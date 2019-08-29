package handler.workshop;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Step3_OutgoingMessageWorkshop {
    private final Step1_PendingMessagesWorkshop pendingMessages;

    Step3_OutgoingMessageWorkshop(Step1_PendingMessagesWorkshop pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void trySend(SelectionKey key) throws IOException {
        if (canBeWritten(key)) {
            // get client (SocketChannel) from key, hint: key.channel() + casting
            SocketChannel client = null;
            // send pending messages to that client, hint: pendingMessages.sentTo(client)
            // switch key to listen to read again, hint: key.interestOps(...), OP_READ
        }
    }

    private boolean canBeWritten(SelectionKey key) {
        // key must be valid and must be ready to write
        // hint: isValid(), isWritable()
        return false;
    }
}
