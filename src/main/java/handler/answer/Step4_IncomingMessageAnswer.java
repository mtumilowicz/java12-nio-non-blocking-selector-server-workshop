package handler.answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

abstract class Step4_IncomingMessageAnswer {
    private final Step1_PendingMessagesAnswer pendingMessages;

    Step4_IncomingMessageAnswer(Step1_PendingMessagesAnswer pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void tryReceive(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = client.read(buf);
            if (bytesRead > 0) {
                handleIncomingMessage(client, buf, key);
            }
            if (bytesRead == -1) {
                pendingMessages.closeClientIfEnd(client);
            }
        }
    }

    abstract void switchToWrite(SelectionKey key);

    void handleIncomingMessage(SocketChannel client, ByteBuffer buffer, SelectionKey key) {
        pendingMessages.prepareForSendingTo(client, buffer);
        switchToWrite(key);
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
