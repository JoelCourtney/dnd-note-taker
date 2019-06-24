class Campaign(val name: String) {
    var dm = ""
    var pcs: MutableList<PlayerCharacter> = mutableListOf()
    var npcs: MutableList<Character> = mutableListOf()

    fun summary(): String {
        var res = "$name"
        if (dm.isNotBlank()) {
            res += "\nDM: $dm"
        }
        if (!pcs.isEmpty()) {
            res += "\n\nPlayers:"
            for (pc in pcs) {
                res += "\n${pc.summary()}"
            }
        }
        return res
    }
}