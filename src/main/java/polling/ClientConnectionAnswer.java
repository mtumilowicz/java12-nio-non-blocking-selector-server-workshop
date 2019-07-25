package polling;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class ClientConnectionAnswer implements Runnable {
    private final SocketChannel client;
    private final ByteBuffer buf;

    ClientConnectionAnswer(SocketChannel client) {
        this(client, ByteBuffer.allocateDirect(80));
    }

    ClientConnectionAnswer(SocketChannel client, ByteBuffer buf) {
        this.client = client;
        this.buf = buf;
    }

    @Override
    public void run() {
        try {
            int read = client.read(buf);
            if (read > 0) {
                writeBufferToClient();
            }
            closeClientIfEnd(read);
        } catch (IOException exception) {
            // workshops
        }
    }

    private void closeClientIfEnd(int read) throws IOException {
        if (read == -1) {
            client.close();
        }
    }

    private void writeBufferToClient() throws IOException {
        buf.flip();
        transformBytesInBuffer(UnaryOperator.identity(), buf);
        while (buf.hasRemaining()) {
            client.write(buf);
        }
    }

    private void transformBytesInBuffer(UnaryOperator<Byte> transformation, ByteBuffer buf) {
        IntStream.range(0, buf.limit()).forEach(i -> buf.put(i, transformation.apply(buf.get(i))));
    }
}