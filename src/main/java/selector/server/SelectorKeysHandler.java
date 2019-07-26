package selector.server;

import selector.handler.AcceptHandler;
import selector.handler.ReadHandler;
import selector.handler.WriteHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by mtumilowicz on 2019-07-26.
 */
public class SelectorKeysHandler {
    Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
    AcceptHandler acceptHandler = new AcceptHandler(pendingData);
    ReadHandler readHandler = new ReadHandler(pendingData);
    WriteHandler writeHandler = new WriteHandler(pendingData);
    
    public final void handle(Selector selector) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext(); ) {
                SelectionKey key = it.next();
                it.remove();
                handleKey(key);
            }
        }
    }
    
    public final void handleKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                acceptHandler.handle(key);
            } else if (key.isReadable()) {
                readHandler.handle(key);
            } else if (key.isWritable()) {
                writeHandler.handle(key);
            }
        }
    }
}
