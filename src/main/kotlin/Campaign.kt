import kotlinx.serialization.*

@Serializable
data class Campaign (
    var name: String,
    var dm: String,
    val chars: ArrayList<Character> = arrayListOf(),
    val quests: ArrayList<Quest> = arrayListOf(),
    val events: ArrayList<Event> = arrayListOf(),
    val places: ArrayList<Place> = arrayListOf(),
    val quotes: ArrayList<Quote> = arrayListOf()
) {
    private fun dump(): String {
        var res: String = ""
        res += "Name: $name"
        res += "\nDM: $dm"
        if (chars.isNotEmpty()) {
            res += "\n\n\n***CHARACTERS***\n"
            for (char in chars) {
                res += "\n\n${char.dump()}"
            }
        }
        if (quests.isNotEmpty()) {
            res += "\n\n\n***QUESTS***\n"
            for (quest in quests) {
                res += "\n\n${quest.dump()}"
            }
        }
        if (events.isNotEmpty()) {
            res += "\n\n\n***EVENTS***\n"
            for (event in events) {
                res += "\n\n$event"
            }
        }
        if (places.isNotEmpty()) {
            res += "\n\n\n***PLACES***\n"
            for (place in places) {
                res += "\n\n${place.dump()}"
            }
        }
        if (quotes.isNotEmpty()) {
            res += "\n\n\n***QUOTES***\n"
            for (quote in quotes) {
                res += "\n\n${quote.dump()}"
            }
        }
        return res
    }

    fun view() {
        while (true) {
            val line = DnD.getCommand()
            when (line[0].toLowerCase()) {
                "" -> println("No command")
                "new" -> new(line.subList(1, line.size))
                "char", "character", "person", "npc", "pc" -> Character.search(line.subList(1, line.size).joinToString(""), chars, line[0].toLowerCase())
                "place", "loc", "location" -> Place.search(line.subList(1,line.size).joinToString(""), places)
                "quest", "mission", "objective" -> Quest.search(line.subList(1,line.size).joinToString(""), quests)
                "event", "plot" -> Event.search(line.subList(1, line.size).joinToString(""), events)
                "quote" -> Quote.search(line.subList(1, line.size).joinToString(""), quotes)
                "dump" -> println(dump())
                "xyzzy" -> {
                    val list = dump().toMutableList()
                    list.shuffle()
                    println(list.joinToString(""))
                }
                "chars", "characters", "people", "npcs", "pcs" -> {
                    for (char in chars.sortedBy{it.name})
                        println(char.name)
                }
                "places", "locs", "locations" -> {
                    for (place in places.sortedBy{it.name})
                        println(place.name)
                }
                "quests", "missions", "objectives" -> {
                    for (quest in quests.sortedBy{it.status})
                        println(quest.goal)
                }
                "events" -> {
                    for (event in events)
                        println(event.what)
                }
                "quotes" -> {
                    for (quote in quotes)
                        println(quote.text)
                }
                else -> println("Unrecognized: " + line[0])
            }
        }
    }

    private fun new(what: List<String>) {
        if (what.size == 1) {
            when (what[0]) {
                "char", "character", "person", "npc" -> {
                    val char = Character()
                    char.name = DnD.getResponse("Name")
                    char.race = Race.from(DnD.getResponse("Race"))
                    char.dClass = DClass.from(DnD.getResponse("Class"))
                    chars.add(char)
                    DnD.edit()
                    char.view()
                }
                "pc" -> {
                    val char = Character()
                    char.playedBy = DnD.getResponse("Player")
                    char.name = DnD.getResponse("Name")
                    char.race = Race.from(DnD.getResponse("Race"))
                    char.dClass = DClass.from(DnD.getResponse("Class"))
                    chars.add(char)
                    DnD.edit()
                    char.view()
                }
                "loc", "location", "place" -> {
                    val place = Place(DnD.getResponse("Name"),DnD.getResponse("Location"))
                    places.add(place)
                    DnD.edit()
                    place.view()
                }
                "quest", "mission", "objective" -> {
                    val quest = Quest(DnD.getResponse("Goal"),DnD.getResponse("Given by"))
                    quests.add(quest)
                    DnD.edit()
                    quest.view()
                }
                "event" -> {
                    val event = Event(DnD.getResponse("What happened"), DnD.getResponse("Where"))
                    events.add(event)
                    DnD.edit()
                    event.view()
                }
                "quote" -> {
                    val quote = Quote(DnD.getResponse("Text"), DnD.getResponse("Who's so fucking wise"))
                    quotes.add(quote)
                    DnD.edit()
                    quote.view()
                }
                else -> println("Unrecognized: " + what[0])
            }
        }
    }
}