package selector.handler;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

class AcceptHandlerAnswer {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    AcceptHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
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