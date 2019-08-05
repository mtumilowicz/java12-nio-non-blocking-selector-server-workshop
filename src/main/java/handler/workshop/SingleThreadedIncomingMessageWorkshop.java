package handler.workshop;

import java.nio.channels.SelectionKey;

class SingleThreadedIncomingMessageWorkshop extends IncomingMessageWorkshop {

    SingleThreadedIncomingMessageWorkshop(PendingMessagesWorkshop pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        // hint: interestOps, SelectionKey.OP_WRITE
    }
}
