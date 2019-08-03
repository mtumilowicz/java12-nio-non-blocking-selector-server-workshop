package selector.handler.answer;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

class SingleThreadedReadHandlerAnswer extends ReadHandlerAnswer {

    SingleThreadedReadHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        super(pendingData);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    void prepareForSendToClient2(SelectionKey key, ByteBuffer buf) {
        prepareForSendToClient(key, buf);
    }
}
