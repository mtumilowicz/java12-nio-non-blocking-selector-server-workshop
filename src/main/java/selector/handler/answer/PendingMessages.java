package selector.handler.answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

class PendingMessages {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingMessagesByClient;

    PendingMessages(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingMessagesByClient = pendingData;
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

    private void closeClientIfEnd(SocketChannel client) throws IOException {
        pendingMessagesByClient.remove(client);
        client.close();
    }
}
