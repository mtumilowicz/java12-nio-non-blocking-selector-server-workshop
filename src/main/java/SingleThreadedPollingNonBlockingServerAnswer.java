import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedPollingNonBlockingServerAnswer {
    
    private final int port;

    public SingleThreadedPollingNonBlockingServerAnswer(int port) {
        this.port = port;
    }

    public SingleThreadedPollingNonBlockingServerAnswer() {
        this.port = 81;
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedPollingNonBlockingServerAnswer().start();
    }

    public void start() throws IOException {
        log("Creating server socket on port " + port);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);
        log("Created server socket on port " + port);

        List<SocketChannel> sockets = new ArrayList<>();
        while (true) {
            SocketChannel newSocket = ssc.accept();
            if (nonNull(newSocket)) {
                sockets.add(newSocket);
                System.out.println("Connected to " + newSocket);
                newSocket.configureBlocking(false);
            }

            sockets.stream().filter(SocketChannel::isConnected).forEach(sc -> handle(new ClientConnectionAnswer(sc)));

            sockets.removeIf(not(SocketChannel::isConnected));
        }
    }

    private void handle(Runnable clientConnection) {
        clientConnection.run();
    }

    private void log(String message) {
        System.out.println(message);
    }
}