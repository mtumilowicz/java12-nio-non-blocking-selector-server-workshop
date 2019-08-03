package selector.handler.answer;

import transformer.BufferTransformer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.function.UnaryOperator;

class ReadHandlerAnswer extends XReadHandlerAnswer {

    ReadHandlerAnswer(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        super(pendingData);
    }

    @Override
    void switchToWrite(SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    void xxx(SelectionKey key, ByteBuffer buf) {
        writeToBuffer(key, buf);
    }
}
