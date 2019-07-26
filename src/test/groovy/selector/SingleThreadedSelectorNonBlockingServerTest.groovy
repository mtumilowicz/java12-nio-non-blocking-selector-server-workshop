package selector

import client.TestClient
import selector.server.SingleThreadedSelectorNonBlockingServer
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class SingleThreadedSelectorNonBlockingServerTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedPollingNonBlockingServerAnswer"() {
        given:
        def port = 8080

        expect:
        expectedClientOutput == extractClientOutputFor(port, new SingleThreadedSelectorNonBlockingServer())
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
