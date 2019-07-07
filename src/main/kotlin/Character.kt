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
        var res: String = ""
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

    companion object {
        fun view(what: String, chars: ArrayList<Character>, key: String) {
            val char: Character?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ","")
            } else {
                when (key) {
                    "pc" -> "playedby"
                    else -> "name"
                }
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ","")
            } else {
                what.toLowerCase()
            }
            char = when (search) {
                "name" -> {
                    DnD.search(chars) {it.name.replace(" ", "").contains(with, true)}
                }
                "player", "playedby" -> {
                    DnD.search(chars) {it.playedBy.replace(" ", "").contains(with, true)}
                }
                "race" -> {
                    DnD.search(chars) {it.race == Race.from(with)}
                }
                "class" -> {
                    DnD.search(chars) {it.dClass == DClass.from(with)}
                }
                else -> null
            }
            if (char != null) {
                loop@ while (true) {
                    val command = DnD.getCommand(char.name)
                    if (command.size == 1) {
                        when (command[0].toLowerCase()) {
                            "sum", "summary" -> println(char.summary())
                            "dump" -> println(char.dump())
                            "playedby", "player" -> println(char.playedBy)
                            "name" -> println(char.name)
                            "race" -> println(char.race.toString())
                            "class" -> println(char.dClass.toString())
                            "level" -> println(char.level)
                            "align", "alignment" -> println(char.alignment.toString())
                            "stat", "status" -> println(char.status)
                            "rel", "relation", "relationship" -> println(char.relationship)
                            "inv", "inventory" -> {
                                var i = 1
                                for (item in char.inventory) {
                                    println("${i++}: $item")
                                }
                            }
                            "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                var i = 1
                                for (trait in char.traits) {
                                    println("${i++}: $trait")
                                }
                            }
                            "", "done", "save", "exit" -> break@loop
                            else -> println("Unrecognized command: $command")
                        }
                    } else {
                        DnD.edit()
                        when (command[0].toLowerCase()) {
                            "playedby", "player" -> char.playedBy = command.subList(1,command.size).joinToString(" ")
                            "name", "rename" -> char.name = command.subList(1,command.size).joinToString(" ")
                            "race" -> char.race = Race.from(command.subList(1,command.size).joinToString())
                            "class" -> char.dClass = DClass.from(command.subList(1,command.size).joinToString())
                            "level" -> char.level = command.subList(1,command.size).joinToString().toInt()
                            "align", "alignment" -> char.alignment = Alignment.from(command.subList(1,command.size).joinToString(" "))
                            "stat", "status" -> char.status = command.subList(1,command.size).joinToString(" ")
                            "rel", "relation", "relationship" -> char.relationship = command.subList(1,command.size).joinToString(" ")
                            "add", "new", "insert", "append", "get", "create" -> {
                                val list: ArrayList<String>?
                                val prompt: String
                                when (command[1]) {
                                    "inv", "inventory", "item", "items", "weapon", "weapons" -> {
                                        list = char.inventory
                                        prompt = "Item"
                                    }
                                    "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                        list = char.traits
                                        prompt = "Trait"
                                    }
                                    "note", "notes" -> {
                                        list = char.notes
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
                                        char.inventory
                                    }
                                    "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                        char.traits
                                    }
                                    "note", "notes" -> {
                                        char.notes
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
        }
    }
}