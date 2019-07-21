import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import kotlin.system.exitProcess

fun main() {
    DnD.run()
}

class DnD {
    companion object {
        private var campaign: Campaign = Campaign("fake", "news")
        private var edited: Boolean = false

        private val json = Json(JsonConfiguration(prettyPrint = true))

        fun edit() {
            edited = true
        }

        fun run() {
            play(getResponse("Select Campaign"))
            campaign.view()
        }

        fun getCommand(prompt: String = ""): List<String> {
            if (edited) {
                val jsonData = json.stringify(Campaign.serializer(), campaign)
                File("${campaign.name.toLowerCase().replace(" ", "")}.json").writeText(jsonData)
                edited = false
            }
            print("\n${
                if (prompt.isNotEmpty()) "$prompt " else ""
            }>> ")
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

        fun play(what: String) {
            if (what != "new")
                campaign = json.parse(Campaign.serializer(), File("${what.replace(" ", "").toLowerCase()}.json").readText())
            else {
                var name = getResponse("Name")
                while (name.isEmpty()) {
                    name = getResponse("Enter a flipping name, son")
                }
                campaign = Campaign(name, getResponse("DM"))
                edit()
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