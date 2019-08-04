package server.answer;

import handler.answer.SelectorKeysHandlerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SIngleThreadedServerAnswer extends ServerAnswer {

    public SIngleThreadedServerAnswer(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new SelectorKeysHandlerAnswer().handle(selector);
    }

    public static void main(String[] args) throws IOException {
        new SIngleThreadedServerAnswer(81).start();
    }
}