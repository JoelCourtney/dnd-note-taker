import kotlinx.serialization.Serializable

@Serializable
data class Quest(val givenBy: String, val what: String, val status: String = "incomplete") {
}