package selector.handler.answer;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.UnaryOperator;

class ReadHandlerAnswer {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    ReadHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            SocketChannel client = (SocketChannel) key.channel();
            int bytesRead = read(client, buf);
            if (bytesRead > 0) {
                switchToWrite(bytesRead, key);
            }
            closeClientIfEnd(bytesRead, client);
        }
    }

    private void switchToWrite(int bytesRead, SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private int read(SocketChannel client, ByteBuffer buf) throws IOException {
        int read = client.read(buf);
        if (read > 0) {
            buf.flip();
            BufferTransformer.transformBytes(buf, UnaryOperator.identity());
            pendingData.get(client).add(buf);
        }

        return read;
    }

    private void closeClientIfEnd(int read, SocketChannel client) throws IOException {
        if (read == -1) {
            pendingData.remove(client);
            client.close();
        }
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
