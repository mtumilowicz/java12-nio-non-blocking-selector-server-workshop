package selector.server.answer;

import selector.handler.answer.SelectorKeysHandlerAnswer;
import server.XNBServerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SingleThreadedSelectorNonBlockingServerAnswer extends XNBServerAnswer {

    public SingleThreadedSelectorNonBlockingServerAnswer(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new SelectorKeysHandlerAnswer().handle(selector);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedSelectorNonBlockingServerAnswer(81).start();
    }

    private void log(String message) {
        System.out.println(message);
    }
}