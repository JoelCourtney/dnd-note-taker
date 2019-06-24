enum class DClass(val type: String) {
    BARBARIAN("Barbarian"),
    BARD("Bard"),
    CLERIC("Cleric"),
    DRUID("Druid"),
    FIGHTER("Fighter"),
    MONK("Monk"),
    PALADIN("Paladin"),
    RANGER("Ranger"),
    ROGUE("Rogue"),
    SORCERER("Sorcerer"),
    WARLOCK("Warlock"),
    WIZARD("Wizard"),
    ARTIFICER("Artificer"),
    BLOODHUNTER("Blood Hunter"),
    UNKNOWN("Unknown");

    companion object {
        fun from(type: String): DClass {
            return if (type.isNotBlank())
                DClass.valueOf(type.toUpperCase().replace(" ",""))
            else
                DClass.UNKNOWN
        }
    }

    override fun toString(): String {
        return type
    }
}