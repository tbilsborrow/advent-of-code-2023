import kotlin.math.pow

val lines = java.io.File("../resources/input-04.txt").readLines()
// val lines = java.io.File("../resources/test.txt").readLines()

data class Card(
    val winners: Set<Int>,
    val have: List<Int>,
    var count: Int = 1,
)

val cards = lines.map { line ->
    val data = line.split(":")[1].trim().split("|")
    Card(
        winners = data[0].trim().split("\\s+".toRegex()).map { it.toInt() }.toSet(),
        have = data[1].trim().split("\\s+".toRegex()).map { it.toInt() },
    )
}

fun Card.numMatching() = this.have.filter { this.winners.contains(it) }.size
fun Card.value() = 2.toDouble().pow(this.numMatching() - 1).toInt()

val part1 = cards.sumOf { it.value() }
println("Part 1: $part1")

// part 2: update all card counts
cards.forEachIndexed { idx, card ->
    val n = card.numMatching()
    for (i in 1..n) cards[idx + i].count += card.count
}

val part2 = cards.sumOf { it.count }
println("Part 2: $part2")
