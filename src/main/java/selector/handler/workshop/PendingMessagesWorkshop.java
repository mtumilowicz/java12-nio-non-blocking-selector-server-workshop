package selector.handler.workshop;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.UnaryOperator;

class PendingMessagesWorkshop {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingMessagesByClient;

    private PendingMessagesWorkshop(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingMessagesByClient = pendingData;
    }

    static PendingMessagesWorkshop multithreaded() {
        return new PendingMessagesWorkshop(new ConcurrentHashMap<>());
    }

    static PendingMessagesWorkshop singleThreaded() {
        return new PendingMessagesWorkshop(new HashMap<>());
    }

    void initFor(SocketChannel client) {
        pendingMessagesByClient.put(client, new ConcurrentLinkedQueue<>());
    }

    void sendTo(SocketChannel client) throws IOException {
        var buffersToWrite = pendingMessagesByClient.get(client);
        while (!buffersToWrite.isEmpty()) {
            ByteBuffer buf = buffersToWrite.poll();
            int bytesWritten = client.write(buf);
            if (bytesWritten == -1) {
                closeClientIfEnd(client);
            }
        }
    }

    void closeClientIfEnd(SocketChannel client) throws IOException {
        pendingMessagesByClient.remove(client);
        client.close();
    }

    void prepareForSendingTo(SocketChannel client, ByteBuffer buffer) {
        prepareBuffer(buffer);
        pendingMessagesByClient.get(client).add(buffer);
    }

    private void prepareBuffer(ByteBuffer buf) {
        buf.flip();
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
    }
}
