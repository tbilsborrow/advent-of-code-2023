import kotlin.math.max

val lines = java.io.File("../resources/input-02.txt").readLines()

data class Set(
    var red: Int = 0,
    var green: Int = 0,
    var blue: Int = 0,
)

data class Game(
    val id: Int,
    val sets: List<Set>
)

val games = lines.map { line ->
    val x = line.split(":")
    val id = x[0].split(" ")[1].toInt()
    val sets = x[1].split(";")
    Game(
        id,
        sets.map { set ->
            val cubes = set.split(",")
            val s = Set()
            cubes.forEach { cube ->
                val z = cube.trim().split(" ")
                if (z[1] == "red") s.red = z[0].toInt()
                if (z[1] == "green") s.green = z[0].toInt()
                if (z[1] == "blue") s.blue = z[0].toInt()
            }
            s
        },
    )
}

// --------------------------------------------------------

val part1 = games.filter { game ->
    game.sets.forEach { set ->
        if (set.red > 12) return@filter false
        if (set.green > 13) return@filter false
        if (set.blue > 14) return@filter false
    }
    true
}.sumOf { it.id }

println("Part 1: $part1")

val part2 = games.sumOf { game ->
    var red = 0
    var green = 0
    var blue = 0
    game.sets.forEach { set ->
        red = max(set.red, red)
        green = max(set.green, green)
        blue = max(set.blue, blue)
    }
    red * green * blue
}

println("Part 2: $part2")
