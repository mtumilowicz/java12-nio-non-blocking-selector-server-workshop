package transformer;

import java.nio.ByteBuffer;
import java.util.function.UnaryOperator;

/**
 * Created by mtumilowicz on 2019-07-31.
 */
public class BufferTransformerWorkshop {
    public static void transformBytes(ByteBuffer buf, UnaryOperator<Byte> transformation) {
        // transform all bytes
        // hint: buf.limit(), buf.put
    }
}
