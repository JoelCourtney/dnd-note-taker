import kotlinx.serialization.*

@Serializable
class Campaign {
    var name: String = ""
    var dm = ""
    val pcs: ArrayList<Player> = arrayListOf()
    val npcs: ArrayList<Character> = arrayListOf()
    val quests: ArrayList<Quest> = arrayListOf()
    val events: ArrayList<Event> = arrayListOf()
    val places: ArrayList<Place> = arrayListOf()
    val quotes: ArrayList<Quote> = arrayListOf()

//    fun summary(): String {
//        var res = "$name"
//        if (dm.isNotBlank()) {
//            res += "\nDM: $dm"
//        }
//        if (!pcs.isEmpty()) {
//            res += "\n\nPlayers:"
//            for (pc in pcs) {
//                res += "\n${pc.summary()}"
//            }
//        }
//        return res
//    }
}