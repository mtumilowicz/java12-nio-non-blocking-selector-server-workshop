package handler.answer;

import java.nio.channels.SelectionKey;

class Step5_SingleThreadedIncomingMessageAnswer extends Step4_IncomingMessageAnswer {

    Step5_SingleThreadedIncomingMessageAnswer(Step1_PendingMessagesAnswer pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
