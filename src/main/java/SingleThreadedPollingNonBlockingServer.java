import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;

/**
 * Created by mtumilowicz on 2019-07-21.
 */
public class SingleThreadedPollingNonBlockingServer {

    public static void main(String[] args) throws IOException {
        new SingleThreadedPollingNonBlockingServer().start();
    }
    
    public void start() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(81));
        ssc.configureBlocking(false);

        List<SocketChannel> sockets = new ArrayList<>();
        while (true) {
            SocketChannel newSocket = ssc.accept();
            if (newSocket != null) {
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
}