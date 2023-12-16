val lines = java.io.File("../resources/input-01.txt").readLines().asSequence()

val digits = listOf(
    Pair("0", "zero"),
    Pair("1", "one"),
    Pair("2", "two"),
    Pair("3", "three"),
    Pair("4", "four"),
    Pair("5", "five"),
    Pair("6", "six"),
    Pair("7", "seven"),
    Pair("8", "eight"),
    Pair("9", "nine"),
)
val stigid = digits.map { Pair(it.first, it.second.reversed()) }

fun matchPart1(line: String, pair: Pair<String, String>): String? =
    if (line.startsWith(pair.first)) pair.first else null

fun matchPart2(line: String, pair: Pair<String, String>): String? =
    if (line.startsWith(pair.first) || line.startsWith(pair.second)) pair.first else null

fun matchLine(line: String, digits: List<Pair<String, String>>, matchFunc: (String, Pair<String, String>) -> String?): String {
    for (i in line.indices) {
        for (pair in digits) {
            val d = matchFunc(line.substring(i), pair)
            if (d != null) return d
        }
    }
    return "0"
}

fun go(lines: Sequence<String>, matchFunc: (String, Pair<String, String>) -> String?) =
    lines.map { line ->
        val d1 = matchLine(line, digits, matchFunc)
        val d2 = matchLine(line.reversed(), stigid, matchFunc)
        (d1 + d2).toInt()
    }.sum()

// part 1
println("Part 1: ${go(lines, ::matchPart1)}")

// part 2
println("Part 2: ${go(lines, ::matchPart2)}")
