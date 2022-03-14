package b01_actors.actors;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import b01_actors.aux.EngineConfig;
import b01_actors.messages.AggregatorMessage;
import b01_actors.messages.AggregatorResponse;
import b01_actors.messages.AggregatorCommand;
import b01_actors.messages.SearcherCommand;
import b01_actors.messages.SearcherResultMessage;
import b01_actors.pojo.Result;

public class AggregatorActor extends AbstractBehavior<AggregatorMessage> {
    private Map<String, Result[]> results;
    private ActorRef<AggregatorResponse> reply_to;
    private int total_asked = 0;

    private enum ReceiveTimeout implements AggregatorMessage {
        INSTANCE
    }

    public AggregatorActor(ActorContext<AggregatorMessage> context) {
        super(context);
        results = new HashMap<>();
        getContext().setReceiveTimeout(Duration.ofMillis(500), ReceiveTimeout.INSTANCE);
    }

    public static Behavior<AggregatorMessage> create() {
        return Behaviors.setup(AggregatorActor::new);
    }

    @Override
    public Receive<AggregatorMessage> createReceive() {
        return newReceiveBuilder().onMessage(AggregatorCommand.class, this::onMultiSearchQuery)
                .onMessage(SearcherResultMessage.class, this::onSearchResults)
                .onMessage(ReceiveTimeout.class, it -> onReceiveTimeout()).build();
    }

    public Behavior<AggregatorMessage> onSearchResults(SearcherResultMessage msg) {
        total_asked -= 1;
        if (msg.results.isPresent()) {
            results.put(msg.engine_name, msg.results.get());
        }
        if (total_asked == 0) {
            respond();
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

    public Behavior<AggregatorMessage> onReceiveTimeout() {
        LoggerFactory.getLogger(AggregatorActor.class).error("timeout");
        respond();
        return Behaviors.stopped();
    }

    public Behavior<AggregatorMessage> onMultiSearchQuery(AggregatorCommand q) {
        total_asked = q.engines.size();
        for (EngineConfig engine : q.engines) {
            reply_to = q.reply_to;
            getContext().spawn(SearcherActor.create(), engine.name)
                    .tell(new SearcherCommand(q.query, engine, getContext().getSelf()));
        }
        return Behaviors.same();
    }

    private void respond() {
        LoggerFactory.getLogger(AggregatorActor.class).info("responding!");
        reply_to.tell(new AggregatorResponse(results));
    }
}
