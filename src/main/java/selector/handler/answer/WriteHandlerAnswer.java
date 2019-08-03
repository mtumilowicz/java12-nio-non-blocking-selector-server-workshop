package selector.handler.answer;


import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

class WriteHandlerAnswer {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    WriteHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    void handle(SelectionKey key) throws IOException {
        if (canBeWritten(key)) {
            SocketChannel client = (SocketChannel) key.channel();
            Queue<ByteBuffer> buffersToWrite = pendingData.get(client);
            processBuffers(client, buffersToWrite);
            switchToRead(key);
        }
    }

    private void processBuffers(SocketChannel client, Queue<ByteBuffer> buffersToWrite) throws IOException {
        while (!buffersToWrite.isEmpty()) {
            ByteBuffer buf = buffersToWrite.poll();
            int bytesWritten = client.write(buf);
            if (bytesWritten == -1) {
                closeClientIfEnd(client);
            }
        }
    }

    void switchToRead(SelectionKey key) {
        key.interestOps(SelectionKey.OP_READ);
    }

    private void closeClientIfEnd(SocketChannel client) throws IOException {
        pendingData.remove(client);
        client.close();
    }

    private boolean canBeWritten(SelectionKey key) {
        return key.isValid() && key.isWritable();
    }

}
