package selector.handler;

import selector.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TransmogrifyHandler implements Handler<Socket, IOException> {
  public void handle(Socket s) throws IOException {
    try (
        InputStream in = s.getInputStream();
        OutputStream out = s.getOutputStream()
    ) {
      int data;
      while ((data = in.read()) != -1) {
        out.write(Util.transmogrify(data));
      }
    }
  }
}
