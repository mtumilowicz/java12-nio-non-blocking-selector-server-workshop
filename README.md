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
* provide direct connections to I/O services
* conduit that transports data between byte buffers and the entity on the other end of the channel 
(a hardware device, a file or socket)
* socket channel objects are bidirectional
* `read()` and `write()` returns the number of bytes transferred, which can be less than the number 
of bytes in the buffer, or even zero
    * the position of the buffer will have been advanced by the same amount
    * if a partial transfer was performed, the buffer can be resubmitted to the channel to continue
    transferring data where it left off and repeated until the buffer's `hasRemaining( )` method returns
    false
* cannot be reused - represents a specific connection to a specific I/O service and encapsulates 
the state of that connection 
* when a channel is closed, that connection is lost, and the channel is no longer connected to anything

# Socket Channels
* models network sockets
* can operate in nonblocking mode and are selectable
* it's no longer necessary to dedicate a thread to each socket connection 
(and suffer the context-switching overhead of managing large numbers of threads)
* Using the new NIO classes, one or a few threads can manage hundreds or even thousands of active socket 
connections with little or no performance loss
* it's possible to perform readiness selection of socket channels using a `Selector` object
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
    
# Selectors provide the ability to do readiness selection, which enables multiplexed I/O
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
* it illustrates the paradigm of quickly checking to see if
  attention is required by any of a set of resources, without being forced to wait if something
  isn't ready to go. This ability to check and continue is key to scalability. A single thread can
  monitor large numbers of channels with readiness selection. The Selector and related classes
  provide the APIs to do readiness selection on channels
*  You register one or
  more previously created selectable channels with a selector object. A key that represents the
  relationship between one channel and one selector is returned. Selection keys remember what
  you are interested in for each channel. They also track the operations of interest that their
  channel is currently ready to perform. When you invoke select( ) on a selector object, the
  associated keys are updated by checking all the channels registered with that selector. You
  can obtain a set of the keys whose channels were found to be ready at that point. By iterating
  over these keys, you can service each channel that has become ready since the last time you
  invoked select( ).
* At the most fundamental level, selectors provide the capability to ask a channel if it's ready to
  perform an I/O operation of interest to you. For example, a SocketChannel object could be
  asked if it has any bytes ready to read, or we may want to know if a ServerSocketChannel has
  any incoming connections ready to accept.
* Selectors provide this service when used in conjunction with SelectableChannel objects, but
  there's more to the story than that. The real power of readiness selection is that a potentially
  large number of channels can be checked for readiness simultaneously. The caller can easily
  determine which of several channels are ready to go
* traditional Java solution to monitoring multiple sockets has been to create a
  thread for each and allow the thread to block in a read( ) until data is available. This
  effectively makes each blocked thread a socket monitor and the JVM's thread scheduler
  becomes the notification mechanism
* True readiness selection must be done by the operating system. One of the most important
  functions performed by an operating system is to handle I/O requests and notify processes
  when their data is ready. So it only makes sense to delegate this function down to the
  operating system. The Selector class provides the abstraction by which Java code can request
  readiness selection service from the underlying operating system in a portable way.
* Selector
  The Selector class manages information about a set of registered channels and their
  readiness states. Channels are registered with selectors, and a selector can be asked to
  update the readiness states of the channels currently registered with it. When doing so,
  the invoking thread can optionally indicate that it would prefer to be suspended until
  one of the registered channels is ready.
* SelectionKey
  A SelectionKey encapsulates the registration relationship between a specific channel
  and a specific selector. A SelectionKey object is returned from
  SelectableChannel.register( ) and serves as a token representing the registration.
  SelectionKey objects contain two bit sets (encoded as integers) indicating which
  channel operations the registrant has an interest in and which operations the channel is
  ready to perform.
* Although the register( ) method is defined on the SelectableChannel class, channels are
  registered with selectors, not the other way around. A selector maintains a set of channels to
  monitor. A given channel can be registered with more than one selector and has no idea which
  Selector objects it's currently registered with. The choice to put the register( ) method in
  SelectableChannel rather than in Selector was somewhat arbitrary. It returns a SelectionKey
  object that encapsulates a relationship between the two objects. The important thing is to
  remember that the Selector object controls the selection process for the channels registered
  with it.
* Selectors are the managing objects, not the selectable channel objects.
  The Selector object performs readiness selection of channels registered
  with it and manages selection keys.
* Selectors are not
  primary I/O objects like channels or streams: data never passes through them
* ops - This is a bit
        mask that represents the I/O operations that the selector should test for when checking the
        readiness of that channel
        
# SelectionKey
* a key represents the registration of a particular channel object with a
  particular selector object. You can see that relationship reflected in the first two methods
  above. The channel( ) method returns the SelectableChannel object associated with the key,
  and selector( ) returns the associated Selector object
* A SelectionKey object contains two sets encoded as integer bit masks: one for those
  operations of interest to the channel/selector combination (the interest set) and one
  representing operations the channel is currently ready to perform (the ready set).
*  there are currently four channel operations that can be tested for readiness.
    * isReadable( ),
      isWritable( ), isConnectable( ), and isAcceptable( )
* The ready set contained by a SelectionKey object is as of the time the
  selector last checked the states of the registered channels. The readiness
  of individual channels could have changed in the meantime.
* Each Selector object maintains
  three sets of keys:
  Registered key set
  The set of currently registered keys associated with the selector. Not every registered
  key is necessarily still valid. This set is returned by the keys( ) method and may be
  empty. The registered key set is not directly modifiable; attempting to do so yields a
  java.lang.UnsupportedOperationException.
  Selected key set
  A subset of the registered key set. Each member of this set is a key whose associated
  channel was determined by the selector (during a prior selection operation) to be ready
  for at least one of the operations in the key's interest set. This set is returned by the
  selectedKeys( ) method (and may be empty).
  Don't confuse the selected key set with the ready set. This is a set of keys, each with an
  associated channel that is ready for at least one operation. Each key has an embedded ready
  set that indicates the set of operations the associated channel is ready to perform.
  Keys can be directly removed from this set, but not added. Attempting to add to the selected
  key set throws java.lang.UnsupportedOperationException.
  Cancelled key set
  A subset of the registered key set, this set contains keys whose cancel( ) methods have
  been called (the key has been invalidated), but they have not been deregistered. This
  set is private to the selector object and cannot be accessed directly.
* Essentially, selectors are a wrapper for a native call to
  select( ), poll( ), or a similar operating system-specific system call
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
* int n = selector.selectNow( );
  The selectNow( ) method performs the readiness selection process but will never block. If no
  channels are currently ready, it immediately returns 0 
* wakeup( ), provides the capability to gracefully break
  out a thread from a blocked select( ) invocation
* The important part is what happens when a key is not already in the selected set. When at
  least one operation of interest becomes ready on the channel, the ready set of the key is
  cleared, and the currently ready operations are added to the ready set. The key is then added to
  the selected key set
* The way to clear the ready set of a SelectionKey is to remove the key itself from the set of
  selected keys. The ready set of a selection key is modified only by the Selector object during a
  selection operation. The idea is that only keys in the selected set are considered to have
  legitimate readiness information. That information persists in the key until the key is removed
  from the selected key set, which indicates to the selector that you have seen and dealt with it.
  The next time something of interest happens on the channel, the key will be set to reflect the
  state of the channel at that point and once again be added to the selected key set.
* The conventional approach is to perform a select( )
  call on the selector (which updates the selected key set) then iterate over the set of keys
  returned by selectedKeys( ). As each key is examined in turn, the associated channel is dealt
  with according to the key's ready set. The keys are then removed from the selected key set
* Selector objects are thread-safe, but the key sets they contain are not. The key sets returned by
  the keys( ) and selectedKeys( ) methods are direct references to private Set objects inside the
  Selector object. These sets can change at any time. The registered key set is read-only.
* A better approach is to use one selector for all selectable channels and delegate the servicing
  of ready channels to other threads. You have a single point to monitor channel readiness and a
  decoupled pool of worker threads to handle the incoming data.