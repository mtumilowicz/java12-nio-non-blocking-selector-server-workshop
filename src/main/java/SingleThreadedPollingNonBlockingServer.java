import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedPollingNonBlockingServer {
    public void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);

        List<SocketChannel> sockets = new ArrayList<>();
        while (true) {
            SocketChannel newSocket = ssc.accept(); // mostly null - never blocks
            if (newSocket != null) {
                sockets.add(newSocket);
                System.out.println("Connected to " + newSocket);
                newSocket.configureBlocking(false);
            }

            sockets.forEach(
                    sc -> {
                        if (sc.isConnected()) {
                            handle(new ClientConnectionAnswer(sc));
                        }
                    }
            );

            sockets.removeAll(sockets.stream().filter(SocketChannel::isConnected).collect(Collectors.toList()));
        }
    }

    private void handle(Runnable clientConnection) {
        clientConnection.run();
    }
}