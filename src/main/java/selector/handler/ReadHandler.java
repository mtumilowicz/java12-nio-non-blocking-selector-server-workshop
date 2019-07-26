package selector.handler;

import selector.util.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class ReadHandler {
  private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

  public ReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
    this.pendingData = pendingData;
  }

  public void handle(SelectionKey key) throws IOException {
    SocketChannel sc = (SocketChannel) key.channel();
    ByteBuffer buf = ByteBuffer.allocateDirect(80);
    int read = sc.read(buf);
    if (read == -1) {
      pendingData.remove(sc);
      return;
    }
    if (read > 0) {
      Util.transmogrify(buf);
      pendingData.get(sc).add(buf);
      key.interestOps(SelectionKey.OP_WRITE);
    }
  }
}
