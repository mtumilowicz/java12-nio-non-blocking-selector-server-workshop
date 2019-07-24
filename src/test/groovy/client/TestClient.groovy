package client

import java.nio.channels.Channels
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets

/**
 * Created by mtumilowicz on 2019-07-08.
 */
class TestClient {
    private final int port

    TestClient(int port) {
        this.port = port
    }

    public static void main(String[] args) {
        new TestClient(81).run()
    }

    def run() throws IOException {
        SocketChannel.open(new InetSocketAddress('localhost', port)).withCloseable { client ->
            def writer = new PrintWriter(Channels.newWriter(client, StandardCharsets.UTF_8.name()), true)
            def reader = new BufferedReader(Channels.newReader(client, StandardCharsets.UTF_8.name()))

            def list = []

            def sendMessage = 'xxx'
            writer.println(sendMessage)
            list.add('send: ' + sendMessage)

            def receivedMessage = reader.readLine()
            list.add("received: " + receivedMessage)
            
            return list
        }
    }
}
