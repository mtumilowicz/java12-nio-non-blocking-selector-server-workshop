package selector.handler.answer;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.UnaryOperator;

abstract class ReadHandlerAnswer {
    final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    ReadHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeRead(key)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int bytesRead = read(key, buf);
            if (bytesRead > 0) {
                prepareConnectionForWriting(key, buf);
            }
            closeClientIfEnd(bytesRead, key);
        }
    }

    abstract void switchToWrite(SelectionKey key);

    abstract void prepareConnectionForWriting(SelectionKey key, ByteBuffer buf);

    private int read(SelectionKey key, ByteBuffer buf) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        return client.read(buf);
    }

    void prepareForSendingToClient(SelectionKey key, ByteBuffer buf) {
        prepareBuffer(buf);
        queueBuffer(key, buf);
        prepareKey(key);
    }

    void prepareBuffer(ByteBuffer buf) {
        buf.flip();
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
    }

    void queueBuffer(SelectionKey key, ByteBuffer buf) {
        SocketChannel client = (SocketChannel) key.channel();
        pendingData.get(client).add(buf);
    }

    void prepareKey(SelectionKey key) {
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
