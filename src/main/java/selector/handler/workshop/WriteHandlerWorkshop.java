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
        if (canBeWritten(key)) {
            // ger client (SocketChannel) from key, hint: key.channel() + casting
            SocketChannel client = null;
            // get all buffers to write, hint: pendingData.get(client)
            Queue<ByteBuffer> buffersToWrite = null;
            processBuffers(buffersToWrite, client);
            switchToRead(key);
        }
    }

    void switchToRead(SelectionKey key) {
        key.interestOps(SelectionKey.OP_READ);
    }

    private void processBuffers(Queue<ByteBuffer> buffersToWrite, SocketChannel client) throws IOException {
        // process until not empty, hint: while(!buffersToWrite.isEmpty())
        // take and remove first, hint: poll()
        // send to client, hint: client.write(...)
        // if end (bytesWritten == -1) closeClientIfEnd(client) and return
    }

    private void closeClientIfEnd(SocketChannel client) throws IOException {
        // remove buffers and client from pendingData, hint: pendingData.remove(...)
        // close client, hint: client.close()
    }

    private boolean canBeWritten(SelectionKey key) {
        // if is valid and is writable, hint: key.isValid(), key.isWritable()
        return false;
    }

}
