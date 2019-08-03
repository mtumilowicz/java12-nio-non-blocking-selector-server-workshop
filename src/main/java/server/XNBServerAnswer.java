package server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by mtumilowicz on 2019-07-31.
 */
public abstract class XNBServerAnswer extends NonBlockingServerAnswer {
    public XNBServerAnswer(int port) {
        super(port);
    }

    @Override
    protected void processSockets(ServerSocketChannel ssc) throws IOException {
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        handleConnections(selector);
    }
    
    protected abstract void handleConnections(Selector selector) throws IOException;
}
