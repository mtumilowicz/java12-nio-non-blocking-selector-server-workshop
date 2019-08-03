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
            // get ServerSocketChannel from key, hint: key.channel() + casting
            // accept connection, hint: channel.accept()
            // note that this operation is non blocking and the output is never null
            SocketChannel client = null;
            log("Client connected: " + client);
            // switch client to be non blocking, hint: configureBlocking(false)
            // insert placeholder queue for that client into pendingData
            // hint: use ConcurrentLinkedQueue
            // register selector to listen for reads of that client
            // hint: client.register(..., SelectionKey.OP_READ), key.selector()
        }
    }

    private boolean canBeAccepted(SelectionKey key) {
        // key must be valid and must be ready for accept
        // hint: isValid(), isAcceptable()
        return false;
    }

    private void log(String message) {
        System.out.println(message);
    }
}