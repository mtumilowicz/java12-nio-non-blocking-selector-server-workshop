package selector.server.workshop;

import selector.handler.answer.SelectorKeysHandlerAnswer;
import server.XNBServerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SingleThreadedSelectorNonBlockingServerWorkshop extends XNBServerAnswer {

    public SingleThreadedSelectorNonBlockingServerWorkshop(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new SelectorKeysHandlerAnswer().handle(selector);
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadedSelectorNonBlockingServerWorkshop(81).start();
    }

    private void log(String message) {
        System.out.println(message);
    }
}