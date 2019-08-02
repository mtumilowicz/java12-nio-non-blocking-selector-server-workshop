package polling;

import server.XServer;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedNonBlockingPollingEchoServerWorkshop extends XServer {

    public SingleThreadedNonBlockingPollingEchoServerWorkshop(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedNonBlockingPollingEchoServerWorkshop(81).start();
    }

    @Override
    protected void processSockets(ServerSocketChannel ssc) throws IOException {
        var clients = new SocketChannelsAnswer();
        // spinning loop, hint: while(true)
        // accept connection and add to clients, hint: acceptConnection, ifPresent
        // handle connected, hint: client.handleConnected()
        // remove not connected, hint: clients.removeNotConnected()
    }

    private Optional<SocketChannel> acceptConnection(ServerSocketChannel ssc) throws IOException {
        SocketChannel newSocket = ssc.accept();
        if (nonNull(newSocket)) {
            log("Connected to " + newSocket);
            newSocket.configureBlocking(false);
        }

        return Optional.ofNullable(newSocket);
    }

    private void log(String message) {
        System.out.println(message);
    }
}