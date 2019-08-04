package handler.answer;

import java.nio.channels.SelectionKey;

class SingleThreadedIncomingMessageAnswer extends IncomingMessageAnswer {

    SingleThreadedIncomingMessageAnswer(PendingMessagesAnswer pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
