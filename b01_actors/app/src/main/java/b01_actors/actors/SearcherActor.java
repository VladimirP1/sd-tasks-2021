package b01_actors.actors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;

import org.slf4j.LoggerFactory;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import b01_actors.messages.SearcherCommand;
import b01_actors.messages.SearcherResultMessage;
import b01_actors.pojo.Result;
import b01_actors.messages.SearcherMessage;

public class SearcherActor extends AbstractBehavior<SearcherMessage> {
    private final Gson gson = new Gson();

    public SearcherActor(ActorContext<SearcherMessage> context) {
        super(context);
    }

    public static Behavior<SearcherMessage> create() {
        return Behaviors.setup(SearcherActor::new);
    }

    @Override
    public Receive<SearcherMessage> createReceive() {
        return newReceiveBuilder().onMessage(SearcherCommand.class, this::onSearchQuery).build();
    }

    public Behavior<SearcherMessage> onSearchQuery(SearcherCommand q) throws MalformedURLException, IOException {

        URL url = new URL("http://127.0.0.1:8081/search?q="
                + URLEncoder.encode(q.query, StandardCharsets.UTF_8) + "&engine=" + URLEncoder.encode(q.engine.name, StandardCharsets.UTF_8));

        try (InputStream is = url.openStream()) {
            Result[] results = gson.fromJson(new InputStreamReader(is), Result[].class);
            q.reply_to.tell(new SearcherResultMessage(q.engine.name, Optional.of(results)));
            LoggerFactory.getLogger(SearcherActor.class).info("Engine " + q.engine.name + " success");
        } catch (Exception e) {
            q.reply_to.tell(new SearcherResultMessage(q.engine.name, Optional.empty()));
            LoggerFactory.getLogger(SearcherActor.class).error(e.getMessage());
        }
        return Behaviors.stopped();
    }
}
