import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
class Test(val a : Int) {
    init {
        println(a)
    }
}

fun main() {
    val test = Json.parse(Test.serializer(),"{a:5}")
    println(test.a)
//    var me = PlayerCharacter("Leaf on the Wind", Race.from("Half  Elf"), DClass.from("RaNg  er"), "Joel Courtney")
//    me.level = 5
//    me.appearance.add("White fur")
//    me.appearance.add("Brown and black spots")
//
//    me.inventory.add("Javelin of Lightning")
////    println(me.summary())
//
//    var zemnianNights = Campaign("Zemnian Nights")
//    zemnianNights.dm = "Dan Walton"
//    zemnianNights.pcs.add(me)
////    println(zemnianNights.summary())
//    val dnd = DnD()
//    dnd.run()
}

class DnD {
    var activeCampaign: Campaign? = null
    var campaigns = mutableListOf<Campaign>()

    fun run() {
        while (true) {
            print(">> ")
            val line = readLine()?.split(" ")
            if (line != null) {
                when (line[0]) {
                    "" -> println("No command")
                    "help" -> help(line.subList(1, line.size))
                    "play" -> play(line.subList(1, line.size).joinToString(" "))
                    "new" -> new(line.subList(1, line.size))
                    "view" -> view(line.subList(1, line.size))
                    else -> println("Unrecognized: " + line[0])
                }
            } else {
                return
            }
        }
    }

    fun help(what: List<String>) {
        if (what.isEmpty()) {
            println("helpy help")
        }
    }

    fun play(what: String) {
        for (camp in campaigns) {
            if (camp.name == what) {
                activeCampaign = camp
                return
            }
        }
        println("Campaign not found")
    }

    fun new(what: List<String>) {
        if (what.size == 1) {
            when (what[0]) {
                "campaign" -> {
                    print("Name: ")
                    val name = readLine()
                    if (name != null) {
                        if (name.isEmpty()) {
                            println("enter a flippin name, son.")
                            new(what)
                        }
                        var camp = Campaign(name)
                        print("DM: ")
                        camp.dm = readLine()?:""
                        campaigns.add(camp)
                    }
                }
                "npc" -> {
                    if (activeCampaign != null) {
                        print("Name: ")
                        val name = readLine() ?: "Unknown"
                        print("Race: ")
                        val race = Race.from(readLine() ?: "unknown")
                        print("Class: ")
                        val dClass = DClass.from(readLine() ?: "unknown")
                        var npc = Character(name, race, dClass)
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

    fun view(what: List<String>) {
        if (what.size == 1) {
            when (what[0]) {
                "npc" -> {
                    print("Search by: ")
                    val search = readLine()!!.toLowerCase().replace(" ","")
                    when (search) {
                        "name" -> {
                            print(": ")
                            val name = readLine()!!.replace(" ", "")
                            val possible = activeCampaign!!.npcs.filter{it.name.replace(" ","").contains(name, true)}
                            when (possible.size) {
                                0 -> println("None found")
                                1 -> println(possible[0].summary())
                                else -> {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}