import kotlinx.serialization.Serializable

@Serializable
class Character : Summarizable {
    var name: String = ""
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

    fun dump(): String {
        var res = "Name: $name"
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
        fun view(what: String, npcs: ArrayList<Character>) {
            val npc: Character?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ","")
            } else {
                "name"
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ","")
            } else {
                what.toLowerCase()
            }
            when (search) {
                "name" -> {
                    npc = DnD.search(npcs) {it.name.replace(" ", "").contains(with, true)}
                }
                else -> npc = null
            }
            if (npc != null) {
                loop@ while (true) {
                    val command = DnD.getCommand(npc.name)
                    if (command.size == 1) {
                        when (command[0].toLowerCase()) {
                            "sum", "summary" -> println(npc.summary())
                            "dump" -> println(npc.dump())
                            "name" -> println(npc.name)
                            "race" -> println(npc.race.toString())
                            "class" -> println(npc.dClass.toString())
                            "level" -> println(npc.level)
                            "align", "alignment" -> println(npc.alignment.toString())
                            "stat", "status" -> println(npc.status)
                            "rel", "relation", "relationship" -> println(npc.relationship)
                            "inv", "inventory" -> {
                                var i = 1
                                for (item in npc.inventory) {
                                    println("${i++}: $item")
                                }
                            }
                            "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                var i = 1
                                for (trait in npc.traits) {
                                    println("${i++}: $trait")
                                }
                            }
                            "", "done", "save", "exit" -> break@loop
                            else -> println("Unrecognized command: $command")
                        }
                    } else {
                        DnD.edit()
                        when (command[0].toLowerCase()) {
                            "name", "rename" -> npc.name = command.subList(1,command.size).joinToString(" ")
                            "race" -> npc.race = Race.from(command.subList(1,command.size).joinToString())
                            "class" -> npc.dClass = DClass.from(command.subList(1,command.size).joinToString())
                            "level" -> npc.level = command.subList(1,command.size).joinToString().toInt()
                            "align", "alignment" -> npc.alignment = Alignment.from(command.subList(1,command.size).joinToString(" "))
                            "stat", "status" -> npc.status = command.subList(1,command.size).joinToString(" ")
                            "rel", "relation", "relationship" -> npc.relationship = command.subList(1,command.size).joinToString(" ")
                            "add", "new", "insert", "append", "get", "create" -> {
                                val list: ArrayList<String>?
                                val prompt: String
                                when (command[1]) {
                                    "inv", "inventory", "item", "items", "weapon", "weapons" -> {
                                        list = npc.inventory
                                        prompt = "Item"
                                    }
                                    "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                        list = npc.traits
                                        prompt = "Trait"
                                    }
                                    "note", "notes" -> {
                                        list = npc.notes
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
                                        npc.inventory
                                    }
                                    "trait", "traits", "desc", "description", "describe", "appearance", "app", "personality", "person" -> {
                                        npc.traits
                                    }
                                    "note", "notes" -> {
                                        npc.notes
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