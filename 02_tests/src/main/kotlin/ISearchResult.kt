

interface ISearchResult {
    interface Post {
        val text : String
        val postType : String
        val timestamp : Long
    }

    val posts : List<Post>
}