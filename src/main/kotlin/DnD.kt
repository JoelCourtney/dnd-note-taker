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
            print("\n${
                if (activeCampaign != null)
                    "${activeCampaign!!.name} "
                else
                    ""
                }>${
                    if (prompt.isNotEmpty())
                        " $prompt "
                    else
                        ""
                }> "
            )
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
                    "char", "character", "person", "npc", "pc" -> Character.view(line.subList(1, line.size).joinToString(""), activeCampaign!!.chars, line[0].toLowerCase())
                    "place", "loc", "location" -> Place.view(line.subList(1,line.size).joinToString(""), activeCampaign!!.places)
                    "dump" -> println(activeCampaign?.dump())
                    "xyzzy" -> {
                        val list = activeCampaign!!.dump().toMutableList()
                        list.shuffle()
                        println(list.joinToString(""))
                    }
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
                        activeCampaign = Campaign(name, getResponse("DM"))
                    }
                    "char", "character", "person", "npc" -> {
                        if (activeCampaign != null) {
                            val char = Character()
                            char.name = getResponse("Name")
                            char.race = Race.from(getResponse("Race"))
                            char.dClass = DClass.from(getResponse("Class"))
                            activeCampaign!!.chars.add(char)
                            edit()
                        } else {
                            println("Please select a campaign first.")
                        }
                    }
                    "pc" -> {
                        if (activeCampaign != null) {
                            val char = Character()
                            char.playedBy = getResponse("Player")
                            char.name = getResponse("Name")
                            char.race = Race.from(getResponse("Race"))
                            char.dClass = DClass.from(getResponse("Class"))
                            activeCampaign!!.chars.add(char)
                            edit()
                        } else {
                            println("Please select a campaign first.")
                        }
                    }
                    "loc", "location", "place" -> {
                        if (activeCampaign != null) {
                            val place = Place(getResponse("Name"),getResponse("Location"))
                            activeCampaign!!.places.add(place)
                            edit()
                        } else {
                            println("Please select a campaign first")
                        }
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