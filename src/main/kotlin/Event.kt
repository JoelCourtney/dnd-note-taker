import kotlinx.serialization.Serializable

@Serializable
data class Event(var what: String, var where: String, var date: String = "", var outcome: String = "", val notes: ArrayList<String> = arrayListOf()): Summarizable {
    override fun summary(): String {
        return "$what${
        if (where.isNotEmpty())
            "\n$where"
        else
            ""
        }"
    }

    fun dump(): String {
        var res = ""
        res += "What happened: $what"
        res += "\nWhere: $where"
        if (date.isNotEmpty())
            res += "\nWhen: $date"
        if (outcome.isNotEmpty())
            res += "\nOutcome: $outcome"
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
            val command = DnD.getCommand(what)
            if (command.size == 1) {
                when (command[0].toLowerCase()) {
                    "sum", "summary" -> println(summary())
                    "dump" -> println(dump())
                    "what", "plot" -> println(what)
                    "where", "loc", "location" -> println(where)
                    "when", "date" -> println(date)
                    "outcome", "result" -> println(outcome)
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
                    "what", "plot" -> what = command.subList(1,command.size).joinToString(" ")
                    "where", "loc", "location" -> where = command.subList(1,command.size).joinToString(" ")
                    "when", "date" -> date = command.subList(1,command.size).joinToString(" ")
                    "outcome", "result" -> outcome = command.subList(1,command.size).joinToString(" ")
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
        fun search(what: String, events: ArrayList<Event>) {
            val event: Event?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ", "")
            } else {
                "what"
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ", "")
            } else {
                what.toLowerCase()
            }
            event = when (search) {
                "what", "plot" -> {
                    DnD.search(events) { it.what.replace(" ", "").contains(with, true) }
                }
                "where", "loc", "location" -> {
                    DnD.search(events) { it.where.replace(" ", "").contains(with, true) }
                }
                "when", "date" -> {
                    DnD.search(events) { it.date.replace(" ", "").contains(with, true) }
                }
                "outcome", "result" -> {
                    DnD.search(events) { it.outcome.replace(" ", "").contains(with, true) }
                }
                "note", "notes" -> {
                    DnD.search(events) { it.notes.joinToString("").contains(with, true) }
                }
                else -> null
            }
            event?.view()
        }
    }
}
