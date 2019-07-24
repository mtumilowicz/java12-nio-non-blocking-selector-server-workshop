package polling;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class ClientConnectionAnswer implements Runnable {
    private final SocketChannel client;

    ClientConnectionAnswer(SocketChannel client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buf = ByteBuffer.allocateDirect(80);
            int read = client.read(buf);
            if (read == -1) {
                client.close();
                return;
            }
            write(read, buf);
        } catch (IOException exception) {
            // workshops
        }
    }
    
    void write(int read, ByteBuffer buf) throws IOException {
        if (read > 0) {
            buf.flip();
//            for (int i = 0; i < buf.limit(); i++) {
//                buf.put(i, buf.get(i));
//            }
            while(buf.hasRemaining()) {
                client.write(buf);
            }
        }
    }
}