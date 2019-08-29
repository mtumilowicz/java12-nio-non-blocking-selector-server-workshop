package server.answer

import client.TestClient
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class Step10_SingleThreadedServerAnswerTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedServerAnswerTest"() {
        given:
        def port = 3

        expect:
        expectedClientOutput == extractClientOutputFor(port, new Step10_SingleThreadedServerAnswer(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
