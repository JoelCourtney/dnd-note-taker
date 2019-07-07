import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import kotlin.system.exitProcess

fun main() {
    DnD.run()
}

class DnD {
    companion object {
        private var activeCampaign: Campaign? = null
        private var edited: Boolean = false

        private val json = Json(JsonConfiguration(prettyPrint = true))

        fun edit() {
            edited = true
        }

        fun getCommand(prompt: String = ""): List<String> {
            if (edited && activeCampaign != null) {
                val jsonData = json.stringify(Campaign.serializer(), activeCampaign!!)
                File("${activeCampaign!!.name.toLowerCase().replace(" ", "")}.json").writeText(jsonData)
                edited = false
            }
            if (prompt.isNotEmpty())
                print("\n$prompt >> ")
            else
                print("\n>> ")
            val input = readLine()
            if (input != null) return input.trim(' ').split(" ")
            else exitProcess(0)
        }

        fun getResponse(prompt: String = ""): String {
            if (prompt.isNotEmpty())
                print("$prompt: ")
            else
                print(": ")
            val input = readLine()
            if (input != null) return input.trim(' ')
            else exitProcess(0)
        }

        fun run() {
            while (true) {
                val line = getCommand()
                when (line[0].toLowerCase()) {
                    "" -> println("No command")
                    "help" -> help(line.subList(1, line.size))
                    "play" -> play(line.subList(1, line.size).joinToString("").toLowerCase())
                    "new" -> new(line.subList(1, line.size))
                    "npc" -> Character.view(line.subList(1, line.size).joinToString(""), activeCampaign!!.npcs)
                    else -> println("Unrecognized: " + line[0])
                }
            }
        }

        fun help(what: List<String>) {
            if (what.isEmpty()) {
                println("helpy help")
            }
        }

        fun play(what: String) {
            activeCampaign = json.parse(Campaign.serializer(), File("${what.replace(" ", "")}.json").readText())
        }

        fun new(what: List<String>) {
            if (what.size == 1) {
                when (what[0]) {
                    "campaign" -> {
                        val name = getResponse("Name")
                        if (name.isEmpty()) {
                            println("enter a flippin name, son.")
                            new(what)
                        }
                        activeCampaign = Campaign()
                        activeCampaign!!.name = name
                        activeCampaign!!.dm = getResponse("DM")
                    }
                    "npc" -> {
                        if (activeCampaign != null) {
                            val npc = Character()
                            npc.name = getResponse("Name")
                            npc.race = Race.from(getResponse("Race"))
                            npc.dClass = DClass.from(getResponse("Class"))
                            activeCampaign!!.npcs.add(npc)
                        } else {
                            println("Please select a campaign first.")
                        }
                    }
                    "pc" -> {
                    }
                    "place" -> {
                    }
                    else -> println("Unrecognized: " + what[0])
                }
            }
        }

        fun <T : Summarizable> search(list: List<T>, with: (T) -> Boolean): T? {
            val possible = list.filter(with)
            return when (possible.size) {
                0 -> {
                    println("None found")
                    null
                }
                1 -> {
                    possible[0]
                }
                else -> {
                    var i = 1
                    for (p in possible) {
                        println("\n${i++}: ${p.summary()}")
                    }
                    val which = (getResponse("\nSelect one")).toInt()
                    if (possible.size >= which)
                        possible[which - 1]
                    else {
                        println("Index out of bounds")
                        null
                    }
                }
            }
        }

        fun <T : Summarizable> searchIndex(list: List<T>, with: (T) -> Boolean): Int? {
            val possible = list.filter(with)
            return when (possible.size) {
                0 -> {
                    println("None found")
                    null
                }
                1 -> {
                    list.indexOf(possible[0])
                }
                else -> {
                    var i = 1
                    for (p in possible) {
                        println("\n${i++}: ${p.summary()}")
                    }
                    list.indexOf(possible[getResponse("\nSelect one").toInt() - 1])
                }
            }
        }

        @JvmName("searchIndexString")
        fun searchIndex(list: List<String>, with: (String) -> Boolean): Int? {
            val possible = list.filter(with)
            return when (possible.size) {
                0 -> {
                    println("None found")
                    null
                }
                1 -> {
                    list.indexOf(possible[0])
                }
                else -> {
                    var i = 1
                    for (p in possible) {
                        println("\n${i++}: $p")
                    }
                    list.indexOf(possible[getResponse("\nSelect one").toInt() - 1])
                }
            }
        }
    }
}