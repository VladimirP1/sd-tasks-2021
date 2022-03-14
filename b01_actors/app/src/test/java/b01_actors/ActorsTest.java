package b01_actors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import akka.actor.testkit.typed.Effect;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import b01_actors.actors.AggregatorActor;
import b01_actors.actors.SearcherActor;
import b01_actors.aux.EngineConfig;
import b01_actors.messages.AggregatorCommand;
import b01_actors.messages.AggregatorMessage;
import b01_actors.messages.AggregatorResponse;
import b01_actors.messages.SearcherCommand;
import b01_actors.messages.SearcherMessage;
import b01_actors.messages.SearcherResultMessage;
import b01_actors.pojo.Result;
import b01_actors.server.StubServer;

class ActorsTest {
    @Test
    void testAggregator() {
        TestInbox<AggregatorResponse> inbox = TestInbox.create();
        BehaviorTestKit<AggregatorMessage> test = BehaviorTestKit.create(AggregatorActor.create());
        test.run(new AggregatorCommand("apple", List.of(new EngineConfig("google")), inbox.getRef()));
        test.expectEffectClass(Effect.ReceiveTimeoutSet.class);
        Effect.Spawned<SearcherMessage> spawned = test.expectEffectClass(Effect.Spawned.class);
        assertEquals("google", spawned.childName());
        test.childInbox(spawned.ref())
                .expectMessage(new SearcherCommand("apple", new EngineConfig("google"), test.getRef()));
    }

    private static final Result[] expected_results = {
            new Result("Akka: build concurrent, distributed, and resilient message ... - google", "https://akka.io/"),
            new Result("Akka. О чем все эти Акторы и зачем вам Akka? - Medium - google",
                    "https://medium.com/@maximsidorov/akka-%D0%BE-%D1%87%D0%B5%D0%BC-%D0%B2%D1%81%D0%B5-%D1%8D%D1%82%D0%B8-%D0%B0%D0%BA%D1%82%D0%BE%D1%80%D1%8B-%D0%B8-%D0%B7%D0%B0%D1%87%D0%B5%D0%BC-%D0%B2%D0%B0%D0%BC-akka-cad22a30747") };

    @Test
    void testSearcher() throws Exception {
        StubServer server = new StubServer();
        server.withStubServer(() -> {
            TestInbox<AggregatorMessage> inbox = TestInbox.create();
            BehaviorTestKit<SearcherMessage> test = BehaviorTestKit.create(SearcherActor.create());
            test.run(new SearcherCommand("apple", new EngineConfig("google"), inbox.getRef()));
            inbox.expectMessage(new SearcherResultMessage("google", Optional.of(expected_results)));
        });
    }
};