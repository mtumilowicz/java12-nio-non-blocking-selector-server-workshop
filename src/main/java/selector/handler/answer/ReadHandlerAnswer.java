package selector.handler.answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

abstract class ReadHandlerAnswer {
    private final PendingMessagesAnswer pendingMessages;

    ReadHandlerAnswer(PendingMessagesAnswer pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = client.read(buf);
            if (bytesRead > 0) {
                handleIncomingMessage(
                        () -> pendingMessages.prepareForSendingTo(client, buf),
                        () -> switchToWrite(key));
            }
            if (bytesRead == -1) {
                pendingMessages.closeClientIfEnd(client);
            }
        }
    }

    abstract void switchToWrite(SelectionKey key);

    abstract void handleIncomingMessage(Runnable prepareForSending, Runnable switchToWrite);

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
