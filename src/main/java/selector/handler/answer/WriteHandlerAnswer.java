package selector.handler.answer;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class WriteHandlerAnswer {
    private final PendingMessagesAnswer pendingMessages;

    WriteHandlerAnswer(PendingMessagesAnswer pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeWritten(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            pendingMessages.sendTo(client);
            switchToRead(key);
        }
    }

    private boolean canBeWritten(SelectionKey key) {
        return key.isValid() && key.isWritable();
    }

    private void switchToRead(SelectionKey key) {
        key.interestOps(SelectionKey.OP_READ);
    }
}
