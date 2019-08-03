package selector.handler.answer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class SelectorKeysHandlerAnswer {
    private final PendingMessages pendingMessages = PendingMessages.singleThreaded();
    private final AcceptHandlerAnswer acceptHandler = new AcceptHandlerAnswer(pendingMessages);
    private final SingleThreadedReadHandlerAnswer readHandler = new SingleThreadedReadHandlerAnswer(pendingMessages);
    private final WriteHandlerAnswer writeHandler = new WriteHandlerAnswer(pendingMessages);

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
        } catch (Exception ex) {
            // workshops
        }
    }
}
