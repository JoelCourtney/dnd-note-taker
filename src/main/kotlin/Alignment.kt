import kotlinx.serialization.Serializable

@Serializable
class Alignment(val disposition: Disposition, val morality: Morality) {
    enum class Disposition(val type: String) {
        LAWFUL("Lawful"),
        NEUTRAL("Neutral"),
        CHAOTIC("Chaotic"),
        UNKNOWN("Unknown")
    }
    enum class Morality(val type: String) {
        GOOD("Good"),
        NEUTRAL("Neutral"),
        EVIL("Evil"),
        UNKNOWN("Unknown")
    }

    fun isKnown(): Boolean {
        return (morality != Morality.UNKNOWN) || (disposition != Disposition.UNKNOWN)
    }

    override fun toString(): String {
        return if (morality == Morality.NEUTRAL && disposition == Disposition.NEUTRAL) {
            "True Neutral"
        } else if (!isKnown()) {
            "Unknown"
        } else {
            "${disposition.type} ${morality.type}"
        }
    }

    companion object {
        fun from(what: String): Alignment {
            return when (what) {
                "unknown" -> Alignment(Disposition.UNKNOWN, Morality.UNKNOWN)
                "neutral", "true neutral" -> Alignment(Disposition.NEUTRAL, Morality.NEUTRAL)
                else -> {
                    val list = what.split(" ")
                    if (list.size == 2) {
                        val disp = Disposition.valueOf(list[0].toUpperCase())
                        val mor = Morality.valueOf(list[1].toUpperCase())
                        Alignment(disp, mor)
                    } else {
                        Alignment(Disposition.UNKNOWN, Morality.UNKNOWN)
                    }
                }
            }
        }
    }
}