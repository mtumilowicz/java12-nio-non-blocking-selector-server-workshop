package handler.workshop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

abstract class IncomingMessageWorkshop {
    private final PendingMessagesWorkshop pendingMessages;

    IncomingMessageWorkshop(PendingMessagesWorkshop pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void tryReceive(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            // get the client (SocketChannel) from key, hint: key.channel()
            // allocate buffer for incoming message, hint: ByteBuffer.allocateDirect(...)
            // read from client into buffer, hint: client.read(buffer)
            // if message is received (bytesRead > 0) invoke handleIncomingMessage(...)
            // if channel has reached end of stream (bytesRead == -1) close client
            // use pendingMessages.closeClientIfEnd(client);
        }
    }

    abstract void switchToWrite(SelectionKey key);

    void handleIncomingMessage(SocketChannel client, ByteBuffer buffer, SelectionKey key) {
        // prepare buffer for sending back to the client, hint: pendingMessages.prepareForSendingTo(client, buffer)
        // switch key to listen for write, hint: switchToWrite(key)
    }

    private boolean canBeRead(SelectionKey key) {
        // key must be valid and must be ready for accept
        // hint: isValid(), isReadable()
        return false;
    }
}
