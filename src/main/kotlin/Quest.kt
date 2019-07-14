import kotlinx.serialization.Serializable

@Serializable
data class Quest (
    var goal: String,
    var givenBy: String,
    var status: String = "Incomplete",
    val notes: ArrayList<String> = arrayListOf()) : Summarizable {
    override fun summary(): String {
        return "$goal\n$status"
    }
    fun dump(): String {
        var res = "Goal: $goal\nStatus: $status\nGiven by: $givenBy"
        if (notes.isNotEmpty()) {
            res += "\n\nNotes:"
            for (note in notes) {
                res += "\n$note"
            }
        }
        return res
    }
    companion object {
        fun view(what: String, quests: ArrayList<Quest>) {
            val quest: Quest?
            val search: String = if (what.isEmpty()) {
                DnD.getResponse("Search by").toLowerCase().replace(" ","")
            } else {
                "goal"
            }
            val with: String = if (what.isEmpty()) {
                DnD.getResponse("Search for").toLowerCase().replace(" ","")
            } else {
                what.toLowerCase()
            }
            quest = when (search) {
                "goal", "mission", "quest", "objective" -> {
                    DnD.search(quests) {it.goal.replace(" ", "").contains(with, true)}
                }
                "status", "stat" -> {
                    DnD.search(quests) {it.status.replace(" ", "").contains(with, true)}
                }
                "who", "boss", "givenby" -> {
                    DnD.search(quests) {it.givenBy.replace(" ", "").contains(with, true)}
                }
                "note", "notes" -> {
                    DnD.search(quests) {it.notes.joinToString("").contains(with,true)}
                }
                else -> null
            }
            if (quest != null) {
                loop@ while (true) {
                    val command = DnD.getCommand(quest.goal)
                    if (command.size == 1) {
                        when (command[0].toLowerCase()) {
                            "sum", "summary" -> println(quest.summary())
                            "dump" -> println(quest.dump())
                            "goal", "mission", "quest", "objective" -> println(quest.goal)
                            "status", "stat" -> println(quest.status)
                            "who", "boss", "givenby" -> println(quest.givenBy)
                            "note", "notes" -> {
                                var i = 1
                                for (note in quest.notes) {
                                    println("${i++}: $note")
                                }
                            }
                            "", "done", "save", "exit" -> break@loop
                            else -> println("Unrecognized command: $command")
                        }
                    } else {
                        DnD.edit()
                        when (command[0].toLowerCase()) {
                            "goal", "mission", "quest" -> quest.goal = command.subList(1,command.size).joinToString(" ")
                            "status", "stat" -> quest.status = command.subList(1,command.size).joinToString(" ")
                            "who", "boss", "givenby" -> quest.givenBy = command.subList(1,command.size).joinToString(" ")
                            "add", "new", "insert", "append", "create" -> {
                                val list: ArrayList<String>?
                                val prompt: String
                                when (command[1]) {
                                    "note", "notes" -> {
                                        list = quest.notes
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
                                        quest.notes
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