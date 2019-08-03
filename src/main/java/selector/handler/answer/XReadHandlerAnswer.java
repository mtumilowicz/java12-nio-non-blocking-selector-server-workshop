package selector.handler.answer;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

abstract class XReadHandlerAnswer {
    final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    XReadHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = read(key, buf);
            closeClientIfEnd(bytesRead, key);
        }
    }

    abstract void switchToWrite(SelectionKey key);

    abstract void xxx(SelectionKey key, ByteBuffer buf);

    private int read(SelectionKey key, ByteBuffer buf) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        int read = client.read(buf);
        if (read > 0) {
            xxx(key, buf);
        }

        return read;
    }

    void writeToBuffer(SelectionKey key, ByteBuffer buf) {
        SocketChannel client = (SocketChannel) key.channel();
        buf.flip();
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
        pendingData.get(client).add(buf);
        switchToWrite(key);
    }

    private void closeClientIfEnd(int read, SelectionKey key) throws IOException {
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
