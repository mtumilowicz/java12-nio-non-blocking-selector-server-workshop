package server.workshop

import client.TestClient
import spock.lang.Specification
/**
 * Created by mtumilowicz on 2019-07-23.
 */
class Step10_SingleThreadedServerWorkshopTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedServerWorkshop"() {
        given:
        def port = 3

        expect:
        expectedClientOutput == extractClientOutputFor(port, new Step10_SingleThreadedServerWorkshop(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
