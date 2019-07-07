import kotlinx.serialization.Serializable

@Serializable
data class Quote(val text: String, val who: String) {

}