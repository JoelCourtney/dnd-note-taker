import kotlinx.serialization.Serializable

@Serializable
data class Quote(var text: String, var who: String, var notes: ArrayList<String> = arrayListOf()) : Summarizable {

    override fun summary(): String {
        return "$text\n\t-$who"
    }

    fun dump(): String {
        var res = "Text: $text\nWho: $who"
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
            val command = DnD.getCommand(if (text.length > 15) text.substring(0, 15) else text)
            if (command.size == 1) {
                when (command[0].toLowerCase()) {
                    "sum", "summary" -> println(summary())
                    "dump" -> println(dump())
                    "text", "what" -> println(text)
                    "who", "speaker" -> println(who)
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
                    "text", "what" -> text = command.subList(1,command.size).joinToString(" ")
                    "who", "speaker" -> who = command.subList(1,command.size).joinToString(" ")
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
        fun search(what: String, quotes: ArrayList<Quote>) {
            val quote: Quote?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ", "")
            } else {
                "text"
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ", "")
            } else {
                what.toLowerCase()
            }
            quote = when (search) {
                "text", "what" -> {
                    DnD.search(quotes) { it.text.replace(" ", "").contains(with, true) }
                }
                "who", "speaker" -> {
                    DnD.search(quotes) { it.who.replace(" ", "").contains(with, true) }
                }
                "note", "notes" -> {
                    DnD.search(quotes) { it.notes.joinToString("").contains(with, true) }
                }
                else -> null
            }
            quote?.view()
        }
    }
}