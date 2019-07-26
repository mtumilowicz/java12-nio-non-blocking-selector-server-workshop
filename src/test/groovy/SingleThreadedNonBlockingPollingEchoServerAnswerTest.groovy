import client.TestClient
import polling.SingleThreadedNonBlockingPollingEchoServerAnswer
import spock.lang.Specification

/**
 * Created by mtumilowicz on 2019-07-23.
 */
class SingleThreadedNonBlockingPollingEchoServerAnswerTest extends Specification {

    def expectedClientOutput = ["send: xxx", "received: xxx"]

    def "SingleThreadedPollingNonBlockingServerAnswer"() {
        given:
        def port = 1

        expect:
        expectedClientOutput == extractClientOutputFor(port, new SingleThreadedNonBlockingPollingEchoServerAnswer(port))
    }
    
    def extractClientOutputFor(port, server) {
        new Thread({ server.start() }).start()
        Thread.sleep(10)
        new TestClient(port).run()
    }
}
