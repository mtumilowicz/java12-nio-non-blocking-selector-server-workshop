package selector.handler;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class AcceptHandler {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    public void handle(SelectionKey key) throws IOException {
        if (canBeAccepted(key)) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel client = channel.accept(); // never null, nonblocking
            System.out.println("Client connected: " + client);
            client.configureBlocking(false);
            pendingData.put(client, new ConcurrentLinkedQueue<>());
            client.register(key.selector(), SelectionKey.OP_READ);
        }
    }

    private boolean canBeAccepted(SelectionKey key) {
        return key.isValid() && key.isAcceptable();
    }
}