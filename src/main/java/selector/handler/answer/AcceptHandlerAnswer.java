package selector.handler.answer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class AcceptHandlerAnswer {
    private final PendingMessages pendingMessages;

    AcceptHandlerAnswer(PendingMessages pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeAccepted(key)) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel client = channel.accept(); // never null, nonblocking
            log("Client connected: " + client);
            client.configureBlocking(false);
            pendingMessages.initFor(client);
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