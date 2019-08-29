package handler.workshop;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Step2_ClientConnectionWorkshop {
    private final Step1_PendingMessagesWorkshop pendingMessages;

    Step2_ClientConnectionWorkshop(Step1_PendingMessagesWorkshop pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    void tryAccept(SelectionKey key) throws IOException {
        if (canBeAccepted(key)) {
            // get ServerSocketChannel from key, hint: key.channel() + casting
            // accept connection, hint: channel.accept()
            // note that this operation is non blocking and the output is never null
            SocketChannel client = null;
            log("Client connected: " + client);
            // switch client to be non blocking, hint: configureBlocking(false)
            // init pending messages for that client, hint: pendingMessages.initFor(client)
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