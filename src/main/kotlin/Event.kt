import kotlinx.serialization.Serializable

@Serializable
data class Event(val where: String, val who: MutableList<String>, val date: String, val what: String, val outcome: String)
