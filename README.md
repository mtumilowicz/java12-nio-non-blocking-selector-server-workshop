[![Build Status](https://travis-ci.com/mtumilowicz/java12-nio-non-blocking-selector-server-workshop.svg?branch=master)](https://travis-ci.com/mtumilowicz/java12-nio-non-blocking-selector-server-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# java12-nio-non-blocking-selector-server-workshop

_Reference_: https://www.udemy.com/java-non-blocking-io-with-javanio-and-design-patterns/  
_Reference_: https://github.com/kabutz/Transmogrifier  
_Reference_: http://www.java2s.com/Tutorials/Java/Socket/How_to_use_Java_SocketChannel_create_a_HTTP_client.htm  
_Reference_: https://www.youtube.com/watch?v=3m9RN4aDh08

# project description
* the main goal of this project is to show how to implement single-threaded and multi-threaded 
multiplexed non-blocking server using `java.nio`
    * please refer firstly: https://github.com/mtumilowicz/java12-nio-non-blocking-polling-server-workshop
* in the workshop we will try to fix failing tests from `test/*/workshop` package by following steps and hints in
`java/*/workshop` package
* answers: `java/*/answers` package

# theory in a nutshell
# SelectionKey
* a key represents the registration of a particular channel object with a
  particular selector object - moreover, we have methods: `channel()`, `selector()`
* `SelectionKey` object contains two sets
    * the interest set - operations we are interested in
    * the ready set - operations the channel is ready to perform (time the selector last checked the states 
    of the registered channels)
* operations:
    * `isReadable()`,
    * `isWritable()`, 
    * `isConnectable()`,
    * `isAcceptable()`
* good practice: we should use one selector for all selectable channels and delegate the servicing of ready 
channels to other threads
  * therefore we have a single point to monitor channel readiness and a decoupled pool of worker threads to handle 
  the incoming data
      
# Selectors
* provide the ability to do readiness selection, which enables multiplexed I/O
    * I/O multiplexing is the capability to tell the kernel that we want to be notified if one or more I/O conditions 
    are ready, like input is ready to be read
* provide the capability to ask a channel if it's ready to perform an I/O operation of interest to you
  * for example - check if `ServerSocketChannel` has any incoming connections ready to be accepted
* manages information about a set of registered channels and their readiness states
* channels are registered with selectors, and a selector can be asked to update the readiness states of the 
  channels currently registered with it
* simple analogy
    * each pneumatic tube (channel) is connected to a single teller station inside the bank
    * station has three slots where the carriers (data buffers) arrive, each with an indicator (selection key) that 
    lights up when the carrier is in the slot
    * teller (worker thread) once for a couple of minutes glances up at the indicator lights (invokes `select()`) 
    to determine if any of the channels are ready (readiness selection) 
    * teller (worker thread) can perform another task while the drive-through lanes (channels) are idle yet 
    still respond to them in a timely manner when they require attention
* invoking `select()` on a selector object causes that the associated keys are updated by checking all the channels 
registered with that selector
* by iterating over these keys, we can service each channel that has become ready since the last time we invoked 
`select()`
* large number of channels can be checked for readiness simultaneously
    * true readiness selection is performed by operating system
    * one of the most important functions performed by an operating system is to handle 
    I/O requests and notify processes when their data is ready 
    * abstractions by which Java code can request readiness selection service from the 
    underlying operating system
* given channel can be registered with more than one selector and has no idea which
  `Selector` objects it's currently registered with
* data never passes through selectors
* maintains three sets of keys:
    * Registered key set
        * currently registered keys associated with the selector
        * not every registered key is necessarily still valid
        * returned by the `keys()` method 
    * Selected key set
        * `Selected key set c Registered key set`
        * key whose associated channel was determined by the selector to be ready for at least one of the 
        operations in the key's interest set
        * returned by the `selectedKeys()`
        * selected key set vs the key's ready set 
            * each key has an embedded ready set, and each key can be in selected key set
    * Cancelled key set
        * `Cancelled key set c Registered key set`
        * contains keys whose `cancel()` methods have been called (the key has been invalidated), 
        but they have not been deregistered     
* `selector.select()`
    * blocks indefinitely if no channels are ready
    * this method returns a nonzero value since it blocks until a channel is ready
    * it can return 0 if the `wakeup()` method of the selector is invoked
  * `select()` - return value is not a count of ready channels, but the number of channels
      that became ready since the last invocation of `select()`
* `wakeup()` provides the capability to gracefully break out a thread from a blocked `select()` invocation
