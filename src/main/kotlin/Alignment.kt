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
}