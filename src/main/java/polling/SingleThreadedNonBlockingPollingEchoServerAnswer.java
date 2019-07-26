package polling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedNonBlockingPollingEchoServerAnswer {
    
    private final int port;

    public SingleThreadedNonBlockingPollingEchoServerAnswer(int port) {
        this.port = port;
    }

    public SingleThreadedNonBlockingPollingEchoServerAnswer() {
        this.port = 81;
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedNonBlockingPollingEchoServerAnswer().start();
    }

    public void start() throws IOException {
        log("Creating server socket on port " + port);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);
        log("Created server socket on port " + port);

        List<SocketChannel> sockets = new ArrayList<>();
        while (true) {
            acceptConnection(ssc).ifPresent(sockets::add);
            handleConnected(sockets);
            sockets = removeNotConnected(sockets);
        }
    }
    
    private Optional<SocketChannel> acceptConnection(ServerSocketChannel ssc) throws IOException {
        SocketChannel newSocket = ssc.accept();
        if (nonNull(newSocket)) {
            System.out.println("Connected to " + newSocket);
            newSocket.configureBlocking(false);
        }
        
        return Optional.ofNullable(newSocket);
    }
    
    private void handleConnected(List<SocketChannel> sockets) {
        sockets.stream().filter(SocketChannel::isConnected).forEach(sc -> handle(new ClientConnectionAnswer(sc)));
    }
    
    private List<SocketChannel> removeNotConnected(List<SocketChannel> sockets) {
        return sockets.stream().filter(SocketChannel::isConnected).collect(Collectors.toList());
    }

    private void handle(Runnable clientConnection) {
        clientConnection.run();
    }

    private void log(String message) {
        System.out.println(message);
    }
}