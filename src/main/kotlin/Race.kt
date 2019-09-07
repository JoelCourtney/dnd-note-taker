enum class Race(val type: String) {
    DRAGONBORN("Dragonborn"),
    DWARF("Dwarf"),
    ELF("Elf"),
    GNOME("Gnome"),
    HALFELF("Half-Elf"),
    HALFLING("Halfling"),
    HALFORC("Half-Orc"),
    HUMAN("Human"),
    TIEFLING("Tiefling"),
    AARAKOCRA("Aarakocra"),
    GENASI("Genasi"),
    GOLIATH("Goliath"),
    AASIMAR("Aasimar"),
    BUGBEAR("Bugbear"),
    FIRBOLG("Firbolg"),
    GOBLIN("Goblin"),
    KENKU("Kenku"),
    KOBOLD("Kobold"),
    LIZARDFOLK("Lizardfolk"),
    ORC("Orc"),
    TABAXI("Tabaxi"),
    TRITON("Triton"),
    YUANTIPUREBLOOD("Yuan-ti Pureblood"),
    TORTLE("Tortle"),
    GITH("Gith"),
    CHANGELING("Changeling"),
    KALASHTAR("Kalashtar"),
    SHIFTER("Shifter"),
    WARFORGED("Warforged"),
    CENTAUR("Centaur"),
    LOXODON("Loxodon"),
    MINOTAUR("Minotaur"),
    SIMICHYBRID("Simic Hybrid"),
    VEDALKEN("Vedalken"),
    VERDAN("Verdan"),
    DAMPEAR("Dampear"),
    DROW("Drow"),
    UNKNOWN("Unknown");

    companion object {
        fun from(type: String): Race {
            return if (type.isNotBlank())
                Race.valueOf(type.toUpperCase().replace("-","").replace(" ",""))
            else
                Race.UNKNOWN
        }
    }

    override fun toString(): String {
        return type
    }
}