package selector.handler.workshop;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

class WriteHandlerWorkshop {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    WriteHandlerWorkshop(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (key.isValid() && key.isWritable()) {
            SocketChannel client = (SocketChannel) key.channel();
            Queue<ByteBuffer> buffersToWrite = pendingData.get(client);
            while (!buffersToWrite.isEmpty()) {
                ByteBuffer buf = buffersToWrite.peek();
                int bytesWritten = client.write(buf);
                if (bytesWritten == -1) {
                    closeClientIfEnd(client);
                    return;
                }
                buffersToWrite.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void closeClientIfEnd(SocketChannel client) throws IOException {
            pendingData.remove(client);
            client.close();
    }
}
