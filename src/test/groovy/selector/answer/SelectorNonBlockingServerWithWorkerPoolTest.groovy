package selector.answer

import client.TestClient
import selector.server.answer.SelectorNonBlockingServerWithWorkerPoolAnswer
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class SelectorNonBlockingServerWithWorkerPoolTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedPollingNonBlockingServerAnswer"() {
        given:
        def port = 2

        expect:
        expectedClientOutput == extractClientOutputFor(port, new SelectorNonBlockingServerWithWorkerPoolAnswer(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
