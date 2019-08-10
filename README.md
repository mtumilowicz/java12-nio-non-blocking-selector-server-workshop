# java12-nio-non-blocking-client-server-workshop

_Reference_: https://www.udemy.com/java-non-blocking-io-with-javanio-and-design-patterns/  
_Reference_: https://github.com/kabutz/Transmogrifier  
_Reference_: http://www.java2s.com/Tutorials/Java/Socket/How_to_use_Java_SocketChannel_create_a_HTTP_client.htm  
_Reference_: https://www.youtube.com/watch?v=3m9RN4aDh08

# introduction
* current JVMs run bytecode at speeds approaching that of natively compiled code or even better (dynamic 
runtime optimizations), so
    * applications are no longer CPU bound (spending most of their time executing code) 
    * they are I/O bound (waiting for data transfers)
* how to adjust number of threads
    * CPU intense tasks - adding more threads than cores will have harmful effect on performance,
    suppose that processor works at full power and we force it to do context switches
    * I/O waiting - adding more threads could be beneficial - context switches are not so harmful
    if thread is only waiting
    * `Nthreads = NCPU * UCPU * (1 + W/C)`
        * NCPU is the number of cores, available through 
        `Runtime.getRuntime().availableProcessors()`
        * UCPU is the target CPU utilization (between 0 and 1), and
        * W/C is the ratio of wait time to compute time
* operating system vs Java stream-based I/O model
    * operating system wants to move data in large chunks (buffers)
    * I/O classes of the JVM operates on small pieces — single bytes, or lines of text
    * operating system delivers buffers full of data -> stream classes of
    `java.io` breaks it down into little pieces
    * NIO makes it easier to back the big loaded buffer right up to where you can make direct use of the data 
    (a `ByteBuffer` object)
    * `RandomAccessFile` with array-based `read( )` and `write( )` methods are pretty close to the underlying 
    operating-system calls (even those methods entail at least one buffer copy)

# Buffer Handling
* buffers, and how buffers are handled, are the basis of all I/O
* "input/output" means nothing more than moving data in and out of buffers
* processes perform I/O by requesting of the operating system that data be drained from a buffer (write) 
or that a buffer be filled with data (read)
* steps:
    1. the process requests that its buffer be filled by making the `read()` system call
    1. kernel issuing a command to the disk controller hardware to fetch the data from disk
    1. disk controller writes the data directly into a kernel memory buffer by DMA without further assistance from
    the main CPU
    1. kernel copies the data from the temporary buffer in kernel space to the buffer specified by the process when it
    requested the `read()` operation
* User space is a nonprivileged area: code executing there cannot directly access hardware devices
* kernel space is where the operating system lives
    * communication with device controllers
    * all I/O flows through kernel space
* why disk controller not send directly to the buffer in user space?
    * block-oriented hardware devices such as disk controllers operate on fixed-size data blocks 
    * user process may be requesting an oddly sized chunk of data 
    * kernel plays the role of intermediary, breaking down and reassembling data as it moves between 
    user space and storage devices
* virtual memory means that artificial, or virtual, addresses are used in place of physical 
(hardware RAM) memory addresses (simulates RAM)
    * more than one virtual address can refer to the same physical memory location
    * virtual memory space can be larger than the actual hardware memory available
    * eliminates copies between kernel and user space by mapping a kernel space address to the same 
    physical address as a virtual address in user space, the DMA hardware (which can access only physical 
    memory addresses) can fill a buffer that is simultaneously visible to both the kernel and a user space process

## NIO
# Channels
* is a conduit to an I/O service (a hardware device, a file or socket) and provides methods for 
interacting with that service
* socket channel objects are bidirectional
* if a partial transfer was performed, the buffer can be resubmitted to the channel to continue
transferring data where it left off and repeated until the buffer's `hasRemaining( )` method returns
false
* cannot be reused - represents a specific connection to a specific I/O service and encapsulates 
the state of that connection 
* when a channel is closed - connection is lost

# Socket Channels
* models network sockets
* can operate in nonblocking mode and are selectable
* it's no longer necessary to dedicate a thread to each socket connection 
* it's possible to perform readiness selection of socket channels using a `Selector` object
* `SocketChannel`, `ServerSocketChannel` create a peer socket object when they are instantiated
    * classes from `java.net`: `Socket`, `ServerSocket`, which have been updated to be aware of channels
* Socket channels delegate protocol operations to the peer socket object
    * `ServerSocketChannel` doesn't have a `bind()` method, it's necessary to fetch the peer
      socket and use it to bind to a port to begin listening for connections
* readiness selection is a mechanism by which a channel can be queried to determine if it's
ready to perform an operation of interest, such as reading or writing.
* sockets are stream-oriented, not packet-oriented
    * bytes sent will arrive in the same order, but
    * sender may write 20 bytes to a socket, and the receiver gets only 3 when invoking `read()` 
    - remaining part may still be in transit
    
# Selectors 
* provide the ability to do readiness selection, which enables multiplexed I/O
* controls the selection process for the channels registered with it
* Imagine a bank with three drive-through lanes. In the traditional (nonselector)
  scenario, imagine that each drive-through lane has a pneumatic tube that runs to its own teller
  station inside the bank, and each station is walled off from the others. This means that each
  tube (channel) requires a dedicated teller (worker thread). This approach doesn't scale well
  and is wasteful. For each new tube (channel) added, a new teller is required, along with
  associated overhead such as tables, chairs, paper clips (memory, CPU cycles, context
  switching), etc. And when things are slow, these resources (which have associated costs) tend
  to sit idle.
* Now imagine a different scenario in which each pneumatic tube (channel) is connected to
  a single teller station inside the bank. The station has three slots where the carriers (data
  buffers) arrive, each with an indicator (selection key) that lights up when the carrier is in
  the slot. Also imagine that the teller (worker thread) has a sick cat and spends as much time as
  possible reading Do It Yourself Taxidermy. 1 At the end of each paragraph, the teller glances
  up at the indicator lights (invokes select( )) to determine if any of the channels are ready
  (readiness selection). The teller (worker thread) can perform another task while
  the drive-through lanes (channels) are idle yet still respond to them in a timely manner when
  they require attention.
* You register one or
  more previously created selectable channels with a selector object. A key that represents the
  relationship between one channel and one selector is returned. Selection keys remember what
  you are interested in for each channel. They also track the operations of interest that their
  channel is currently ready to perform. When you invoke select( ) on a selector object, the
  associated keys are updated by checking all the channels registered with that selector. You
  can obtain a set of the keys whose channels were found to be ready at that point. By iterating
  over these keys, you can service each channel that has become ready since the last time you
  invoked select( ).
* selectors provide the capability to ask a channel if it's ready to
  perform an I/O operation of interest to you
  * for example - check if ServerSocketChannel has any incoming connections ready to accept
* large number of channels can be checked for readiness simultaneously
    * True readiness selection must be done by the operating system. 
    * One of the most important functions performed by an operating system is to handle 
    I/O requests and notify processes when their data is ready. 
    * provides the abstraction by which Java code can request readiness selection service from the 
    underlying operating system
* manages information about a set of registered channels and their
  readiness states. 
  * Channels are registered with selectors, and a selector can be asked to
  update the readiness states of the channels currently registered with it
* SelectionKey encapsulates the registration relationship between a specific channel
  and a specific selector
  * SelectionKey objects contain two bit sets (encoded as integers) indicating which
  channel operations the registrant has an interest in and which operations the channel is
  ready to perform.
* given channel can be registered with more than one selector and has no idea which
  Selector objects it's currently registered with.
* Selectors are the managing objects, not the selectable channel objects.
  The Selector object performs readiness selection of channels registered
  with it and manages selection keys.
* data never passes through them
* maintains three sets of keys:
    * Registered key set
        * currently registered keys associated with the selector
        * not every registered key is necessarily still valid. 
        * returned by the keys( ) method 
    * Selected key set
        * `Selected key set c Registered key set`
        * key whose associated channel was determined by the selector to be ready
  for at least one of the operations in the key's interest set. 
        * returned by the selectedKeys( )
        * selected key set vd the ready set 
            * Each key has an embedded ready set that indicates the set of operations the 
            associated channel is ready to perform
    * Cancelled key set
        * `Cancelled key set c Registered key set`
        * contains keys whose cancel( ) methods have been called (the key has been invalidated), 
        but they have not been deregistered     
* following three steps are performed:
  1. The cancelled key set is checked. If it's nonempty, each key in the cancelled set is
  removed from the other two sets, and the channel associated with the cancelled key is
  deregistered. When this step is complete, the cancelled key set is empty.
  2. The operation interest sets of each key in the registered key set are examined. Changes
  made to the interest sets after they've been examined in this step will not be seen
  during the remainder of the selection operation.
  Java NIO
  130
  Once readiness criteria have been determined, the underlying operating system is
  queried to determine the actual readiness state of each channel for its operations of
  interest. Depending on the specific select( ) method called, the thread may block at
  this point if no channels are currently ready, possibly with a timeout value.
  Upon completion of the system calls, which may have caused the invoking thread to
  be put to sleep for a while, the current readiness status of each channel will have been
  determined. Nothing further happens to any channel not found to be currently ready.
  For each channel that the operating system indicates is ready for at least one of the
  operations in its interest set, one of the following two things happens:
  a. If the key for the channel is not already in the selected key set, the key's ready
  set is cleared, and the bits representing the operations determined to be
  currently ready on the channel are set.
  b. Otherwise, the key is already in the selected key set. The key's ready set is
  updated by setting bits representing the operations found to be currently ready.
  Any previously set bits representing operations that are no longer ready are not
  cleared. In fact, no bits are cleared. The ready set as determined by the
  operating system is bitwise-disjoined into the previous ready set. 2 Once a key
  has been placed in the selected key set of the selector, its ready set is
  cumulative. Bits are set but never cleared.
  3. Step 2 can potentially take a long time, especially if the invoking thread sleeps. Keys
  associated with this selector could have been cancelled in the meantime. When Step 2
  completes, the actions taken in Step 1 are repeated to complete deregistration of any
  channels whose keys were cancelled while the selection operation was in progress.
  4. The value returned by the select operation is the number of keys whose operation
  ready sets were modified in Step 2, not the total number of channels in the selection
  key set. The return value is not a count of ready channels, but the number of channels
  that became ready since the last invocation of select( ). A channel ready on a previous
  call and still ready on this call won't be counted, nor will a channel that was ready on a
  previous call but is no longer ready. These channels could still be in the selection key
  set but will not be counted in the return value. The return value could be 0 .
* selector.select( );
  This call blocks indefinitely if no channels are ready. As soon as at least one of the registered
  channels is ready, the selection key set of the selector is updated, and the ready sets for each
  ready channel will be updated. The return value will be the number of channels determined to
  be ready. Normally, this method returns a nonzero value since it blocks until a channel is
  ready. But it can return 0 if the wakeup( ) method of the selector is invoked by another thread.
* wakeup( ), provides the capability to gracefully break out a thread from a blocked select( ) invocation
# SelectionKey
* a key represents the registration of a particular channel object with a
  particular selector object. 
  * `channel()`
  * `selector()`
* A SelectionKey object contains two sets
    * the interest set - representing operations we are interested in
    * the ready set - representing operations the channel is currently ready to perform
        * is as of the time the selector last checked the states of the registered channels
* operations:
    * `isReadable()`,
    * `isWritable()`, 
    * `isConnectable()`,
    * `isAcceptable()`
* The important part is what happens when a key is not already in the selected set. When at
  least one operation of interest becomes ready on the channel, the ready set of the key is
  cleared, and the currently ready operations are added to the ready set. The key is then added to
  the selected key set
* A better approach is to use one selector for all selectable channels and delegate the servicing
  of ready channels to other threads. You have a single point to monitor channel readiness and a
  decoupled pool of worker threads to handle the incoming data.