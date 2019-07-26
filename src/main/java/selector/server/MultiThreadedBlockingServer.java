package selector.server;

import selector.handler.Handler;
import selector.handler.PrintingHandler;
import selector.handler.ThreadedHandler;
import selector.handler.TransmogrifyHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedBlockingServer {
  public static void main(String... args) throws IOException {
    ServerSocket ss = new ServerSocket(8080);
    Handler<Socket, IOException> handler =
        new ThreadedHandler<>(
            new PrintingHandler<>(
                new TransmogrifyHandler()
            ));
    while (true) {
      Socket s = ss.accept(); // never null - blocks
      handler.handle(s);
    }
  }
}