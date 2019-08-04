package selector.handler.answer;

import java.nio.channels.SelectionKey;

class SingleThreadedReadHandlerAnswer extends ReadHandlerAnswer {

    SingleThreadedReadHandlerAnswer(PendingMessagesAnswer pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    void handleIncomingMessage(Runnable prepareForSending, Runnable switchToWrite) {
        prepareForSending.run();
        switchToWrite.run();
    }

}
