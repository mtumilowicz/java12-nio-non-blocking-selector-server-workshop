package selector.handler.workshop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class SelectorKeysHandlerWorkshop {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
    private final AcceptHandlerWorkshop acceptHandler = new AcceptHandlerWorkshop(pendingData);
    private final ReadHandlerWorkshop readHandler = new ReadHandlerWorkshop(pendingData);
    private final WriteHandlerWorkshop writeHandler = new WriteHandlerWorkshop(pendingData);

    public final void handle(Selector selector) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            keys.forEach(this::handleKey);
            keys.clear();
        }
    }

    private void handleKey(SelectionKey key) {
        try {
            acceptHandler.handle(key);
            readHandler.handle(key);
            writeHandler.handle(key);
        } catch (
                Exception ex) {
            // workshops
        }
    }
}
