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
) {
    fun dump(): String {
        var res: String = ""
        res += "Name: $name"
        res += "\nDM: $dm"
        if (!chars.isEmpty()) {
            res += "\n\n***CHARACTERS***"
            for (char in chars) {
                res += "\n${char.dump()}"
            }
        }
        if (!quests.isEmpty()) {
            res += "\n\n***QUESTS***"
            for (quest in quests) {
                res += "\n$quest"
            }
        }
        if (!events.isEmpty()) {
            res += "\n\n***EVENTS***"
            for (event in events) {
                res += "\n$event"
            }
        }
        if (!places.isEmpty()) {
            res += "\n\n***PLACES***"
            for (place in places) {
                res += "\n${place.dump()}"
            }
        }
        return res
    }
}