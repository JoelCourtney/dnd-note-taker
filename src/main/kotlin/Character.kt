import kotlinx.serialization.*

@Serializable
class Character : Summarizable {
    var playedBy: String = "NPC"
        set(value) {
            field = if(value.isNotEmpty())
                value
            else
                "NPC"
        }
    var name: String = "Unknown"
        set(value) {
            field = if (value.isNotEmpty())
                value
            else
                "Unknown"
        }
    var race: Race = Race.UNKNOWN
    var dClass: DClass = DClass.UNKNOWN
    var level: Int? = null
    var alignment: Alignment = Alignment(Alignment.Disposition.UNKNOWN, Alignment.Morality.UNKNOWN)
    var inventory: ArrayList<String> = arrayListOf()
    var traits: ArrayList<String> = arrayListOf()
    var notes: ArrayList<String> = arrayListOf()
    var status: String = ""
    var relationship: String = ""

    override fun summary(): String {
        return "${
            if (!playedBy.contentEquals("NPC"))
                "Played by $playedBy\n"
            else
                ""
        }$name\n$race ${
            if (dClass != DClass.UNKNOWN)
                dClass.toString()
            else
                ""
        }"
    }

    fun dump(): String {
        var res = ""
        if (playedBy != "NPC")
            res += "Player: $playedBy\n"
        res += "Name: $name"
        res += "\nRace: $race"
        res += "\nClass: $dClass"
        res += "\nLevel: ${level?:"Unknown"}"
        res += "\nAlignment: $alignment"
        res += "\nRelationship: $relationship"
        res += "\nStatus: $status"
        if (!inventory.isEmpty()) {
            res += "\n\nInventory:"
            for (item in inventory) {
                res += "\n$item"
            }
        }
        if (!traits.isEmpty()) {
            res += "\n\nTraits:"
            for (trait in traits) {
                res += "\n$trait"
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

    fun view() {
        loop@ while (true) {
            val command = DnD.getCommand(name)
            if (command.size == 1) {
                when (command[0].toLowerCase()) {
                    "sum", "summary" -> println(summary())
                    "dump" -> println(dump())
                    "playedby", "player" -> println(playedBy)
                    "name" -> println(name)
                    "race" -> println(race.toString())
                    "class" -> println(dClass.toString())
                    "lvl", "lev", "level" -> println(level)
                    "align", "alignment" -> println(alignment.toString())
                    "stat", "status" -> println(status)
                    "rel", "relation", "relationship" -> println(relationship)
                    "inv", "inventory", "items" -> {
                        var i = 1
                        for (item in inventory) {
                            println("${i++}: $item")
                        }
                    }
                    "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                        var i = 1
                        for (trait in traits) {
                            println("${i++}: $trait")
                        }
                    }
                    "note", "notes" -> {
                        var i = 1
                        for (note in notes) {
                            println("${i++}: $note")
                        }
                    }
                    "", "done", "save", "exit" -> break@loop
                    else -> println("Unrecognized command: $command")
                }
            } else {
                DnD.edit()
                when (command[0].toLowerCase()) {
                    "playedby", "player" -> playedBy = command.subList(1,command.size).joinToString(" ")
                    "name", "rename" -> name = command.subList(1,command.size).joinToString(" ")
                    "race" -> race = Race.from(command.subList(1,command.size).joinToString())
                    "class" -> dClass = DClass.from(command.subList(1,command.size).joinToString())
                    "lvl", "lev", "level" -> level = command.subList(1,command.size).joinToString().toInt()
                    "align", "alignment" -> alignment = Alignment.from(command.subList(1,command.size).joinToString(" "))
                    "stat", "status" -> status = command.subList(1,command.size).joinToString(" ")
                    "rel", "relation", "relationship" -> relationship = command.subList(1,command.size).joinToString(" ")
                    "add", "new", "insert", "append", "get", "create" -> {
                        val list: ArrayList<String>?
                        val prompt: String
                        when (command[1]) {
                            "inv", "inventory", "item", "items", "weapon", "weapons" -> {
                                list = inventory
                                prompt = "Item"
                            }
                            "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                list = traits
                                prompt = "Trait"
                            }
                            "note", "notes" -> {
                                list = notes
                                prompt = "Note"
                            }
                            else -> {
                                println("List not found: ${command[1]}")
                                list = null
                                prompt = ""
                            }
                        }
                        list?.add(DnD.getResponse(prompt))
                    }
                    "remove", "del", "delete", "lose", "drop", "destroy" -> {
                        val list = when (command[1]) {
                            "inv", "inventory", "item", "items", "weapon", "weapons" -> {
                                inventory
                            }
                            "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                traits
                            }
                            "note", "notes" -> {
                                notes
                            }
                            else -> {
                                println("List not found: ${command[1]}")
                                null
                            }
                        }
                        if (list != null) {
                            val thing = DnD.getResponse("Keyword or Number")
                            val num = thing.toIntOrNull()
                            if (num != null) {
                                if (list.size >= num) {
                                    list.removeAt(num - 1)
                                } else {
                                    println("Index too large")
                                }
                            } else {
                                val index = DnD.searchIndex(list) {it.contains(thing)}
                                if (index != null) {
                                    list.removeAt(index)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun search(what: String, chars: ArrayList<Character>, key: String) {
            val char: Character?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ", "")
            } else {
                when (key) {
                    "pc" -> "playedby"
                    else -> "name"
                }
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ", "")
            } else {
                what.toLowerCase()
            }
            char = when (search) {
                "name" -> {
                    DnD.search(chars) { it.name.replace(" ", "").contains(with, true) }
                }
                "player", "playedby" -> {
                    DnD.search(chars) { it.playedBy.replace(" ", "").contains(with, true) }
                }
                "race" -> {
                    DnD.search(chars) { it.race.type.contains(with, true) }
                }
                "class" -> {
                    DnD.search(chars) { it.dClass.type.contains(with, true) }
                }
                "inv", "inventory", "item", "items" -> {
                    DnD.search(chars) { it.inventory.joinToString("").contains(with, true) }
                }
                "trait", "traits", "desc", "description", "app", "appearance", "describe", "personality", "person" -> {
                    DnD.search(chars) { it.traits.joinToString("").contains(with, true) }
                }
                "note", "notes" -> {
                    DnD.search(chars) { it.notes.joinToString("").contains(with, true) }
                }
                "level", "lvl", "lev" -> {
                    DnD.search(chars) { it.level == with.toInt() }
                }
                "stat", "status" -> {
                    DnD.search(chars) { it.status.contains(with, true) }
                }
                "rel", "relation", "relationship" -> {
                    DnD.search(chars) { it.relationship.contains(with, true) }
                }
                else -> null
            }
            char?.view()
        }
    }
}