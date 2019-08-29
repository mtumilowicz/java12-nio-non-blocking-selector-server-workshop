package handler.workshop;

import java.nio.channels.SelectionKey;

class Step5_SingleThreadedIncomingMessageWorkshop extends Step4_IncomingMessageWorkshop {

    Step5_SingleThreadedIncomingMessageWorkshop(Step1_PendingMessagesWorkshop pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        // hint: interestOps, SelectionKey.OP_WRITE
    }
}
