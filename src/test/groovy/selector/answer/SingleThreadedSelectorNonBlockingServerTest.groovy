package selector.answer

import client.TestClient
import selector.server.answer.SingleThreadedSelectorNonBlockingServerAnswer
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class SingleThreadedSelectorNonBlockingServerTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedPollingNonBlockingServerAnswer"() {
        given:
        def port = 3

        expect:
        expectedClientOutput == extractClientOutputFor(port, new SingleThreadedSelectorNonBlockingServerAnswer(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
