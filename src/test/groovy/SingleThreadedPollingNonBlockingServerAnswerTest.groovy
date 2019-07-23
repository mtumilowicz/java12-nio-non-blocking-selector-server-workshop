import client.TestClient
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class SingleThreadedPollingNonBlockingServerAnswerTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: XXX"]

    def "SingleThreadedPollingNonBlockingServerAnswer"() {
        given:
        def port = 1

        expect:
        expectedClientOutput == extractClientOutputFor(port, new SingleThreadedPollingNonBlockingServerAnswer(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
