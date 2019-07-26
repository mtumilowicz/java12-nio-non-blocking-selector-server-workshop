package selector.server;

import selector.handler.ExecutorServiceHandler;
import selector.handler.Handler;
import selector.handler.PrintingHandler;
import selector.handler.TransmogrifyHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceBlockingServer {
  public static void main(String... args) throws IOException {
    ServerSocket ss = new ServerSocket(8080);
    ExecutorService pool = new ThreadPoolExecutor(
        10, 100,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(1000));
    Handler<Socket, IOException> handler =
        new ExecutorServiceHandler<Socket, IOException>(
            pool,
            new PrintingHandler<>(
                new TransmogrifyHandler()
            ));
    while (true) {
      Socket s = ss.accept(); // never null - blocks
      handler.handle(s);
    }
  }
}