package b01_actors.messages;

import akka.actor.typed.ActorRef;
import b01_actors.aux.EngineConfig;

public class SearcherCommand implements SearcherMessage {
    public final String query;
    public final EngineConfig engine;
    public final ActorRef<AggregatorMessage> reply_to;

    public SearcherCommand(String query, EngineConfig engine, ActorRef<AggregatorMessage> reply_to) {
        this.query = query;
        this.engine = engine;
        this.reply_to = reply_to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearcherCommand) {
            SearcherCommand other = (SearcherCommand) obj;
            return query.equals(other.query) && engine.name.equals(other.engine.name)
                    && reply_to.equals(other.reply_to);
        } else {
            return false;
        }
    }
}
