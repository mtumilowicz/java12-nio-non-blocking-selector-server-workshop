package selector.server.answer;

import selector.handler.answer.PooledSelectorKeysHandlerAnswer;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorNonBlockingServerWithWorkerPoolAnswer extends NonBlockingServerAnswer {

    public SelectorNonBlockingServerWithWorkerPoolAnswer(int port) {
        super(port);
    }

    @Override
    protected void handleConnections(Selector selector) throws IOException {
        new PooledSelectorKeysHandlerAnswer().handle(selector);

    }

    public static void main(String[] args) throws IOException {
        new SelectorNonBlockingServerWithWorkerPoolAnswer(81).start();
    }
}