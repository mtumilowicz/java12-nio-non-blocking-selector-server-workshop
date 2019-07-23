import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientConnectionAnswer implements Runnable {
    private final SocketChannel client;
    private final PrintWriter writer;
    private final InputStream reader;

    ClientConnectionAnswer(SocketChannel client) {
        this(
                client,
                new PrintWriter(Channels.newWriter(client, StandardCharsets.UTF_8.name()), true),
                Channels.newInputStream(client));
    }

    public ClientConnectionAnswer(SocketChannel client, PrintWriter writer, InputStream reader) {
        this.client = client;
        this.writer = writer;
        this.reader = reader;
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
            if (read > 0) {
                buf.flip();
                for (int i = 0; i < buf.limit(); i++) {
                    buf.put(i, buf.get(i));
                }
                while(buf.hasRemaining()) {
                    client.write(buf);
                }
            }
        } catch (IOException exception) {
            // workshops
        }
    }

//    private void sendLine(String message) {
//        if (read > 0) {
//            while (buf.hasRemaining()) {
//                client.write(buf);
//                buf.clear();
//            }
//        }
//    }
//
//    private void readLine() throws IOException {
//        int read = client.read(buf);
//        if (read == -1) {
//            client.close();
//            return null;
//        }
//    }

    private void log(String message) {
        System.out.println(message);
    }
}