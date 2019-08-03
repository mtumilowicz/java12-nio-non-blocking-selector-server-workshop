package polling.workshop;

import server.NonBlockingServerWorkshop;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedNonBlockingPollingEchoServerWorkshop extends NonBlockingServerWorkshop {

    private SingleThreadedNonBlockingPollingEchoServerWorkshop(int port) {
        super(port);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedNonBlockingPollingEchoServerWorkshop(81).start();
    }

    @Override
    protected void processSockets(ServerSocketChannel ssc) throws IOException {
        var clients = new SocketChannelsWorkshop();
        // spinning loop, hint: while(true)
        // accept connection and add to clients, hint: acceptConnection, ifPresent
        // handle connected, hint: client.handleConnected()
        // remove not connected, hint: clients.removeNotConnected()
    }

    private Optional<SocketChannel> acceptConnection(ServerSocketChannel ssc) throws IOException {
        // accept connection, hint: ssc.accept()
        // observation: never blocking, nearly always null
        // if not null - configure to be non blocking, hint: configureBlocking(false)
        // if not null - log("Connected to " + newSocket);
        // return socket channel wrapped in optional

        return Optional.empty();
    }

    private void log(String message) {
        System.out.println(message);
    }
}