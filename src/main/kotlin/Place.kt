import kotlinx.serialization.Serializable

@Serializable
data class Place(val name: String, val description: MutableList<String>)