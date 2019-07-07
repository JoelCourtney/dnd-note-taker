import kotlinx.serialization.*

@Serializable
data class Campaign (
    var name: String,
    var dm: String,
    val chars: ArrayList<Character> = arrayListOf(),
    val quests: ArrayList<Quest> = arrayListOf(),
    val events: ArrayList<Event> = arrayListOf(),
    val places: ArrayList<Place> = arrayListOf(),
    val quotes: ArrayList<Quote> = arrayListOf()
)