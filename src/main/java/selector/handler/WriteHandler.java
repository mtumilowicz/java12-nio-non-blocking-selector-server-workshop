package selector.handler;


import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class WriteHandler {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    public void handle(SelectionKey key) throws IOException {
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
