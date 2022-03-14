package b01_actors.messages;

import java.util.Arrays;
import java.util.Optional;

import b01_actors.pojo.Result;

public class SearcherResultMessage implements AggregatorMessage {
    public final String engine_name;
    public final Optional<Result[]> results;

    public SearcherResultMessage(String engine_name, Optional<Result[]> results) {
        this.engine_name = engine_name;
        this.results = results;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearcherResultMessage) {
            SearcherResultMessage other = (SearcherResultMessage) obj;
            return engine_name.equals(other.engine_name) && Arrays.equals(results.get(), other.results.get());
        } else {
            return false;
        }
    }
}
