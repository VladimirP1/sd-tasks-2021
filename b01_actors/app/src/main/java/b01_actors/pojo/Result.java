package b01_actors.pojo;

public class Result {
    public String title;
    public String url;

    public Result(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Result) {
            Result other = (Result) obj;
            return title.equals(other.title) && url.equals(other.url);
        } else {
            return false;
        }
    }
}
