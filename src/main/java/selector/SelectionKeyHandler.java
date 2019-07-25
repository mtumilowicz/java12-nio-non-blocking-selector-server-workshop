package selector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by mtumilowicz on 2019-07-25.
 */
public class SelectionKeyHandler {
    public static void handle(SelectionKey key) {
        Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
        AcceptHandler acceptHandler = new AcceptHandler(pendingData);
        ReadHandler readHandler = new ReadHandler(pendingData);
        WriteHandler writeHandler = new WriteHandler(pendingData);

        try {
            if (key.isAcceptable()) {
                acceptHandler.handle(key);
            } else if (key.isReadable()) {
                readHandler.handle(key);
            } else if (key.isWritable()) {
                writeHandler.handle(key);
            }
        } catch (IOException e) {
            // workshops
        }
    }
}
