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
            int bytesRead = read(key, buf);
            closeIfEnd(bytesRead, key);
        }
    }

    private void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private int read(SelectionKey key, ByteBuffer buf) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        int read = client.read(buf);
        if (read > 0) {
            buf.flip();
            BufferTransformer.transformBytes(buf, UnaryOperator.identity());
            pendingData.get(client).add(buf);
            switchToWrite(key);
        }

        return read;
    }

    private void closeIfEnd(int read, SelectionKey key) throws IOException {
        if (read == -1) {
            SocketChannel client = (SocketChannel) key.channel();
            pendingData.remove(client);
            client.close();
        }
    }

    private boolean canBeRead(SelectionKey key) {
        return key.isValid() && key.isReadable();
    }
}
