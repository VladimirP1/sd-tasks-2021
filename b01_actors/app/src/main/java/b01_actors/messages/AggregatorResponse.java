package b01_actors.messages;

import java.util.Map;

import b01_actors.pojo.Result;

public class AggregatorResponse {
    public final Map<String, Result[]> results;

    public AggregatorResponse(Map<String, Result[]> results) {
        this.results = results;
    }
}
