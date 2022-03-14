package b01_actors.messages;

import b01_actors.aux.EngineConfig;

import java.util.List;

import akka.actor.typed.ActorRef;

public class AggregatorCommand implements AggregatorMessage {
    public final String query;
    public final List<EngineConfig> engines;
    public final ActorRef<AggregatorResponse> reply_to;

    public AggregatorCommand(String query, List<EngineConfig> engines, ActorRef<AggregatorResponse> reply_to) {
        this.query = query;
        this.engines = engines;
        this.reply_to = reply_to;
    }
}
