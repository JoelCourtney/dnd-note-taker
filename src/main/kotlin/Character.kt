open class Character(var name: String, var race: Race, var dClass: DClass) {
    var level: Int? = null
    var alignment: Alignment = Alignment(Alignment.Disposition.UNKNOWN, Alignment.Morality.UNKNOWN)
    var inventory: MutableList<String> = mutableListOf()
    var appearance: MutableList<String> = mutableListOf()
    var traits: MutableList<String> = mutableListOf()
    var notes: MutableList<String> = mutableListOf()
    open fun summary(): String {
        return if (level != null && alignment.isKnown()) {
            "$name\n$race $dClass\nLevel $level\n$alignment"
        } else if (level != null) {
            "$name\n$race $dClass\nLevel $level"
        } else if (alignment.isKnown()) {
            "$name\n$race $dClass\n$alignment"
        } else {
            "$name\n$race $dClass"
        }
    }
    open fun dump(): String {
        var res = "Name: $name"
        res += "\nRace: $race"
        res += "\nClass: $dClass"
        res += "\nLevel: ${level?:"Unknown"}"
        res += "\nAlignment: $alignment"
        if (!appearance.isEmpty()) {
            res += "\n\nAppearance:"
            for (sight in appearance) {
                res += "\n$sight"
            }
        }
        if (!inventory.isEmpty()) {
            res += "\n\nInventory:"
            for (item in inventory) {
                res += "\n$item"
            }
        }
        if (!traits.isEmpty()) {
            res += "\n\nTraits:"
            for (trait in traits) {
                res += "\n$traits"
            }
        }
        if (!notes.isEmpty()) {
            res += "\n\nNotes:"
            for (note in notes) {
                res += "\n$note"
            }
        }
        return res
    }
}