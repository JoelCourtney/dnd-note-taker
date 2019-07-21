import kotlinx.serialization.Serializable

@Serializable
data class Place(var name: String, var location: String = "", val notes: ArrayList<String> = arrayListOf()): Summarizable {
    override fun summary(): String {
        return "$name${
            if (location.isNotEmpty())
                "\n$location"
            else
                ""
        }"
    }
    fun dump(): String {
        var res: String = ""
        res += "Name: $name"
        res += "\nLocation: $location"
        if (notes.isNotEmpty()) {
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
                    "name" -> println(name)
                    "where", "loc", "location" -> println(location)
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
                    "name", "rename" -> name = command.subList(1,command.size).joinToString(" ")
                    "add", "new", "insert", "append", "create" -> {
                        val list: ArrayList<String>?
                        val prompt: String
                        when (command[1]) {
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
                    "remove", "del", "delete", "drop" -> {
                        val list = when (command[1]) {
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
        fun search(what: String, places: ArrayList<Place>) {
            val place: Place?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ", "")
            } else {
                "name"
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ", "")
            } else {
                what.toLowerCase()
            }
            place = when (search) {
                "name" -> {
                    DnD.search(places) { it.name.replace(" ", "").contains(with, true) }
                }
                "where", "loc", "location" -> {
                    DnD.search(places) { it.location.replace(" ", "").contains(with, true) }
                }
                "note", "notes" -> {
                    DnD.search(places) { it.notes.joinToString("").contains(with, true) }
                }
                else -> null
            }
            place?.view()
        }
    }
}
