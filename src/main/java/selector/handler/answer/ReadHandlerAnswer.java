package selector.handler.answer;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.UnaryOperator;

abstract class ReadHandlerAnswer {
    private final PendingMessages pendingMessages;

    ReadHandlerAnswer(PendingMessages pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = client.read(buf);
            if (bytesRead > 0) {
                prepareConnectionForWriting(key, buf);
            }
            if (bytesRead == -1) {
                pendingMessages.closeClientIfEnd(client);
            }
        }
    }

    abstract void switchToWrite(SelectionKey key);

    abstract void prepareConnectionForWriting(SelectionKey key, ByteBuffer buf);

    void prepareForSendingToClient(SelectionKey key, ByteBuffer buf) {
        prepareBuffer(buf);
        SocketChannel client = (SocketChannel) key.channel();
        pendingMessages.addFor(client, buf);
        switchToWrite(key);
    }

    private void prepareBuffer(ByteBuffer buf) {
        buf.flip();
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
