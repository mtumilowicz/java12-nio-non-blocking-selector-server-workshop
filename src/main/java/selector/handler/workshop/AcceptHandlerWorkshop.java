package selector.handler.workshop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class AcceptHandlerWorkshop {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    AcceptHandlerWorkshop(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeAccepted(key)) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel client = channel.accept(); // never null, nonblocking
            log("Client connected: " + client);
            client.configureBlocking(false);
            pendingData.put(client, new ConcurrentLinkedQueue<>());
            client.register(key.selector(), SelectionKey.OP_READ);
        }
    }

    private boolean canBeAccepted(SelectionKey key) {
        return key.isValid() && key.isAcceptable();
    }

    private void log(String message) {
        System.out.println(message);
    }
}