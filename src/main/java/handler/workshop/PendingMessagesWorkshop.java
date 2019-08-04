package handler.workshop;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

class PendingMessagesWorkshop {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingMessagesByClient = new ConcurrentHashMap<>();

    void initFor(SocketChannel client) {
        // entry for that client with thread-safe collection, hint: ConcurrentLinkedQueue
    }

    void sendTo(SocketChannel client) throws IOException {
        // get queue for the given client
        // process all buffers (until queue is not empty), hint: poll()
        // write to the client, hint: client.write(buf)
    }

    void closeClientIfEnd(SocketChannel client) throws IOException {
        // remove from collection
        // close client, hint: client.close()
    }

    void prepareForSendingTo(SocketChannel client, ByteBuffer buffer) {
        prepareBuffer(buffer);
        // add to the queue for the given client
    }

    private void prepareBuffer(ByteBuffer buf) {
        // reverse the buffer, hint: buf.flip()
        BufferTransformer.transformBytes(buf, UnaryOperator.identity());
    }
}
