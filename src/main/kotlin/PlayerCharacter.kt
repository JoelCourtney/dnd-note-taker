class PlayerCharacter(name: String, race: Race, dClass: DClass, var player: String) : Character(name, race, dClass) {
    override fun summary(): String {
        return "$player\n${super.summary()}"
    }
    override fun dump(): String {
        return "Player: $player\n${super.dump()}"
    }
}