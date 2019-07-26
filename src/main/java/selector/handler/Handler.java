package selector.handler;

public interface Handler<S, X extends Throwable> {
  public void handle(S s) throws X;
}
