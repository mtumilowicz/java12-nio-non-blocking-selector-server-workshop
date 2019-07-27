package selector.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class ReadHandler {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public ReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    public void handle(SelectionKey key) throws IOException {
        if (key.isValid() && key.isReadable()) {
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            SocketChannel client = (SocketChannel) key.channel();
            int bytesRead = read(client, buf);
            if (bytesRead > 0) {
                switchToWrite(bytesRead, key);
            }
            closeClientIfEnd(bytesRead, client);
        }
    }

    private void switchToWrite(int bytesRead, SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private int read(SocketChannel client, ByteBuffer buf) throws IOException {
        int read = client.read(buf);
        if (read > 0) {
            transform(buf, UnaryOperator.identity());
            pendingData.get(client).add(buf);
        }

        return read;
    }

    private void closeClientIfEnd(int read, SocketChannel client) throws IOException {
        if (read == -1) {
            pendingData.remove(client);
            client.close();
        }
    }

    private static void transform(ByteBuffer buf, UnaryOperator<Byte> transformation) {
        buf.flip();
        IntStream.range(0, buf.limit()).forEach(i -> buf.put(i, transformation.apply(buf.get(i))));
    }
}
