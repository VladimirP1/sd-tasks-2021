package b01_actors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import b01_actors.actors.AggregatorActor;
import b01_actors.aux.EngineConfig;
import b01_actors.messages.AggregatorCommand;
import b01_actors.messages.AggregatorMessage;
import b01_actors.messages.AggregatorResponse;
import b01_actors.server.StubServer;

public class IntegrationTest {
    static final ActorTestKit testKit = ActorTestKit.create();

    @Test
    void testSlowSearcher() throws Exception {
        StubServer server = new StubServer();
        server.withStubServer(() -> {
            ActorRef<AggregatorMessage> ref = testKit.spawn(AggregatorActor.create());
            TestProbe<AggregatorResponse> inbox = testKit.createTestProbe();

            ref.tell(new AggregatorCommand("apple", List.of(new EngineConfig("slow"), new EngineConfig("google")),
                    inbox.ref()));
            AggregatorResponse msg = inbox.receiveMessage();
            assertEquals(1, msg.results.keySet().size());
            assertEquals(true, msg.results.keySet().contains("google"));


        });
    }

    @AfterAll
    public static void cleanup() {
        testKit.shutdownTestKit();
    }
}