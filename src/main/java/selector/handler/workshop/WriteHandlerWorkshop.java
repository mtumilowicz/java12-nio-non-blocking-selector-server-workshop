package selector.handler.workshop;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class WriteHandlerWorkshop {
    private final PendingMessagesWorkshop pendingMessages;

    WriteHandlerWorkshop(PendingMessagesWorkshop pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void handle(SelectionKey key) throws IOException {
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
