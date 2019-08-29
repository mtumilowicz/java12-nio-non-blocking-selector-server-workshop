package server.workshop;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by mtumilowicz on 2019-07-31.
 */
public abstract class Step9_ServerWorkshop {
    protected final int port;

    public Step9_ServerWorkshop(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        log("Creating server socket on port " + port);
        // open server socket channel, hint: ServerSocketChannel.open();
        ServerSocketChannel ssc = null;
        // bind to the localhost:port, hint: bind, new InetSocketAddress("localhost", port)
        // configure non blocking, hint: configureBlocking(false)
        log("Created server socket on port " + port);

        // open selector, hint: Selector.open()
        // register selector for listening on new connections, hint: ssc.register(...), OP_ACCEPT
        Selector selector = null;
        handleConnections(selector);
    }

    protected abstract void handleConnections(Selector selector) throws IOException;

    private void log(String message) {
        System.out.println(message);
    }
}
