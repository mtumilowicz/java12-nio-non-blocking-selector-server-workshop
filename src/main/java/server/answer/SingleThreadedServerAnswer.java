package server.answer;

import handler.answer.SingleThreadedEventLoopAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SingleThreadedServerAnswer extends ServerAnswer {

    public SingleThreadedServerAnswer(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new SingleThreadedEventLoopAnswer().handle(selector);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedServerAnswer(81).start();
    }
}