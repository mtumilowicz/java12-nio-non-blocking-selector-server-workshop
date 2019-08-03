package selector.handler.answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

class QueuedBuffers {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    QueuedBuffers(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void sendTo(SocketChannel client) throws IOException {
        var buffersToWrite = pendingData.get(client);
        while (!buffersToWrite.isEmpty()) {
            ByteBuffer buf = buffersToWrite.poll();
            int bytesWritten = client.write(buf);
            if (bytesWritten == -1) {
                closeClientIfEnd(client);
            }
        }
    }

    private void closeClientIfEnd(SocketChannel client) throws IOException {
        pendingData.remove(client);
        client.close();
    }
}
