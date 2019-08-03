package selector.handler.answer;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

class SingleThreadedReadHandlerAnswer extends ReadHandlerAnswer {

    SingleThreadedReadHandlerAnswer(PendingMessages pendingMessages) {
        super(pendingMessages);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    void prepareConnectionForWriting(SelectionKey key, ByteBuffer buf) {
        prepareForSendingToClient(key, buf);
    }
}
