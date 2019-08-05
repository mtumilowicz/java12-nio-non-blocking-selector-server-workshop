# java12-nio-non-blocking-client-server-workshop

_Reference_: https://www.udemy.com/java-non-blocking-io-with-javanio-and-design-patterns/  
_Reference_: https://github.com/kabutz/Transmogrifier  
_Reference_: http://www.java2s.com/Tutorials/Java/Socket/How_to_use_Java_SocketChannel_create_a_HTTP_client.htm  
_Reference_: https://www.youtube.com/watch?v=3m9RN4aDh08

* I/O is often the limiting factor in application performance, not processing speed
    * CPU intense tasks vs I/O waiting - when adding threads will be OK
* Current JVMs run bytecode
  at speeds approaching that of natively compiled code, sometimes doing even better because of
  dynamic runtime optimizations
  * most Java applications are no longer CPU
    bound (spending most of their time executing code) and are more frequently I/O bound
    (waiting for data transfers)
* There's an impedance mismatch between the operating system
  and the Java stream-based I/O model
  * The operating system wants to move data in large
    chunks (buffers)
  * The
    I/O classes of the JVM like to operate on small pieces — single bytes, or lines of text
  * This
    means that the operating system delivers buffers full of data that the stream classes of
    java.io spend a lot of time breaking down into little pieces, often copying each piece
    between several layers of objects
  * NIO makes it easier to
    back the truck right up to where you can make direct use of the data (a ByteBuffer object)
  * This is not to say that it was impossible to move large amounts of data with the traditional I/O
    model — it certainly was (and still is). The RandomAccessFile class in particular can be quite
    efficient if you stick to the array-based read( ) and write( ) methods. Even those methods
    entail at least one buffer copy, but are pretty close to the underlying operating-system calls.
* Buffer Handling
    * Buffers, and how buffers are handled, are the basis of all I/O
    * "input/output"
      means nothing more than moving data in and out of buffers
    * Processes perform I/O by requesting of the operating system that data be drained from
      a buffer (write) or that a buffer be filled with data (read)
    * The process requests that
      its buffer be filled by making the read( ) system call. This results in the kernel issuing
      a command to the disk controller hardware to fetch the data from disk. The disk controller
      writes the data directly into a kernel memory buffer by DMA without further assistance from
      the main CPU. Once the disk controller finishes filling the buffer, the kernel copies the data
      from the temporary buffer in kernel space to the buffer specified by the process when it
      requested the read( ) operation
    * User space is
      a nonprivileged area: code executing there cannot directly access hardware devices
      * Kernel space is where the operating system lives. Kernel code has special privileges:
        it can communicate with device controllers, manipulate the state of processes in user space,
        etc. Most importantly, all I/O flows through kernel space
    * Why not tell the disk controller to send it directly to
      the buffer in user space?
      * block-oriented hardware devices such as disk
        controllers operate on fixed-size data blocks. The user process may be requesting an oddly
        sized or misaligned chunk of data. The kernel plays the role of intermediary, breaking down
        and reassembling data as it moves between user space and storage devices
    * All modern operating systems make use of virtual memory. Virtual memory means that
      artificial, or virtual, addresses are used in place of physical (hardware RAM) memory
      addresses (simulates RAM)
      1. More than one virtual address can refer to the same physical memory location.
      2. A virtual memory space can be larger than the actual hardware memory available.
    * it eliminates copies between kernel and user space
        * By mapping a kernel space address
          to the same physical address as a virtual address in user space, the DMA hardware (which can
          access only physical memory addresses) can fill a buffer that is simultaneously visible to both
          the kernel and a user space process
* buffers
    * Channels are portals through which I/O transfers
      take place, and buffers are the sources or targets of those data transfers. For outgoing
      transfers, data you want to send is placed in a buffer, which is passed to a channel. For
      inbound transfers, a channel deposits data in a buffer you provide.
    * 0 <= mark <= position <= limit <= capacity
    * position attribute does this. It indicates where the next data element should be inserted
      when calling put( ) or from where the next element should be retrieved when get( ) is invoked
    * buffer.put((byte)'H').put((byte)'e').put((byte)'l').put((byte)'l')
      .put((byte)'o');
      * mark: X
      * position: 5
      * limit: 10
      * capacity: 10
    * We've filled the buffer, now we must prepare it for draining. We want to pass this buffer to a
      channel so the content can be written out. But if the channel performs a get( ) on the buffer
      now, it will fetch undefined data from beyond the good data we just inserted. If we set the
      position back to 0, the channel will start fetching at the right place, but how will it know when
      it has reached the end of the data we inserted? This is where the limit attribute comes in. The
      limit indicates the end of the active buffer content
    * We need to set the limit to the current
      position, then reset the position to 0
    * buffer.limit(buffer.position( )).position(0);
    * The flip( ) method flips a buffer from a fill state, where data elements can be appended, to a
      drain state ready for elements to be read out
    * Following a flip - limit: 6
    * What if you flip a buffer twice? It effectively becomes zero-sized.
    * Buffers are not thread-safe
* Channels
    * provide direct connections to I/O
      services. A Channel is a conduit that transports data efficiently between byte buffers and the
      entity on the other end of the channel (usually a file or socket)
    * socket
      channel objects are bidirectional
    * The read( ) and write( ) methods of ByteChannel take ByteBuffer objects as arguments. Each
      returns the number of bytes transferred, which can be less than the number of bytes in the
      buffer, or even zero. The position of the buffer will have been advanced by the same amount.
      If a partial transfer was performed, the buffer can be resubmitted to the channel to continue
      transferring data where it left off. Repeat until the buffer's hasRemaining( ) method returns
      false
    * Unlike buffers, channels cannot be reused. An open channel represents a specific connection
      to a specific I/O service and encapsulates the state of that connection. When a channel is
      closed, that connection is lost, and the channel is no longer connected to anything.
    * Scatter/gather is a simple yet powerful concept (see Section 1.4.1.1).
      It refers to performing a single I/O operation across multiple buffers. For a write operation,
      data is gathered (drained) from several buffers in turn and sent along the channel. The buffers
      do not need to have the same capcity (and they usually don't). The effect is the same as if the
      content of all the buffers was concatenated into one large buffer before being sent. For reads,
      the data read from the channel is scattered to multiple buffers in sequence, filling each to its
      limit, until the data from the channel or the total buffer space is exhausted
      * ByteBuffer header = ByteBuffer.allocateDirect (10);
        ByteBuffer body = ByteBuffer.allocateDirect (80);
        ByteBuffer [] buffers = { header, body };
        Java NIO
        64
        int bytesRead = channel.read (buffers);
        Upon returning from read( ), bytesRead holds the value 48 , the header buffer contains the
        first 10 bytes read from the channel, and body holds the following 38 bytes. The channel
        automatically scattered the data into the two buffers.
        *  It allows you to
          delegate to the operating system the grunt work of separating out the data you read into
          multiple buckets, or assembling disparate chunks of data into a whole. This can be a huge win
          because the operating system is highly optimized for this sort of thing
* Socket Channels
    * model network sockets
    * can operate in nonblocking mode and are selectable
    * it's no longer necessary to
      dedicate a thread to each socket connection (and suffer the context-switching overhead of
      managing large numbers of threads). Using the new NIO classes, one or a few threads can
      manage hundreds or even thousands of active socket connections with little or no performance
      loss.
    *  it's possible to perform
      readiness selection of socket channels using a Selector object
    * you should understand the
      relationship between sockets and socket channels. As described earlier, a channel is a conduit
      to an I/O service and provides methods for interacting with that service. In the case of sockets,
      the decision was made not to reimplement the socket protocol APIs in the corresponding
      channel classes. The preexisting socket channels in java.net are reused for most protocol
      operations.
    * All the socket channels (SocketChannel, ServerSocketChannel, and DatagramChannel) create
      a peer socket object when they are instantiated
    *  These are the familiar classes from java.net
      (Socket, ServerSocket, and DatagramSocket), which have been updated to be aware of
      channels
    * The peer socket can be obtained from a channel by invoking its socket( ) method.
      Additionally, each of the java.net classes now has a getChannel( ) method
    * Socket channels delegate protocol operations to the peer socket object
    * Nonblocking Mode
        * Readiness selection is a mechanism by which a channel can be queried to determine if it's
          ready to perform an operation of interest, such as reading or writing.
    * The ServerSocketChannel class is a channel-based socket listener. It performs the same basic
          task as the familiar java.net.ServerSocket but adds channel semantics, including the ability to
          operate in nonblocking mode.
        * ServerSocketChannel doesn't have a bind( ) method, it's necessary to fetch the peer
      socket and use it to bind to a port to begin listening for connections
        * If invoked in nonblocking mode, ServerSocketChannel.accept( ) will immediately return null
          if no incoming connections are currently pending. This ability to check for connections
          without getting stuck is what enables scalability and reduces complexity. Selectability also
          comes into play. A ServerSocketChannel object can be registered with a Selector instance to
          enable notification when new connections arrive
* The Socket and SocketChannel classes encapsulate point-to-point, ordered network
  connections similar to those provided by the familiar TCP/IP connections we all know and
  love. A SocketChannel acts as the client, initiating a connection to a listening server. It cannot
  receive until connected and then only from the address to which the connection was made
  * Keep in mind that sockets are stream-oriented, not packet-oriented. They
    guarantee that the bytes sent will arrive in the same order but make no promises about
    maintaining groupings. A sender may write 20 bytes to a socket, and the receiver gets only 3
    of those bytes when invoking read( ). The remaining 17 bytes may still be in transit.
  