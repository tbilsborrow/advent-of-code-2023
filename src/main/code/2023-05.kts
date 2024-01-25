import kotlin.math.min

val lines = java.io.File("../resources/input-05.txt").readLines()
// val lines = java.io.File("../resources/test.txt").readLines()

data class Mapping(
    val start: Long,
    val length: Long,
    val offset: Long,
)

val maps = mutableListOf<List<Mapping>>()
var currMaps = mutableListOf<Mapping>()

// --------------------------------------------------------
// input parsing

var i = 3
while (i < lines.size) {
    if (lines[i].isEmpty()) {
        maps.add(currMaps)
        currMaps = mutableListOf()
        i++
    } else {
        val data = lines[i].split(" ").map { it.toLong() }
        currMaps.add(
            Mapping(
                data[1],
                data[2],
                data[0] - data[1],
            ),
        )
    }
    i++
}
if (currMaps.size > 0) maps.add(currMaps)

// --------------------------------------------------------
// logic

// turn source into 1 or more result mappings, depending on if/how
// source overlaps with this
fun Mapping.remap(source: Mapping): List<Mapping> {
    val result = mutableListOf<Mapping>()
    var left = source.start
    var len = source.length

    // any of source that's to the left of this
    if (left < this.start) {
        result.add(Mapping(left, min(len, this.start - left), 0))
        len -= this.start - left
        left = this.start
    }
    if (len <= 0) return result

    // any of source that's contained in this (now tagged with this offset)
    if (left < this.start + this.length) {
        result.add(Mapping(left, min(len, this.start + this.length - left), this.offset))
        len -= this.start + this.length - left
        left = this.start + this.length
        if (len <= 0) return result
    }

    result.add(Mapping(left, len, 0))

    return result
}

// "purify" mapping with an offset into mapping with no offset (with its start updated per the offset)
fun Mapping.realign() = Mapping(this.start + this.offset, this.length, 0)

// for all source, apply remapping logic per each mapping in 'this'
fun List<Mapping>.remap(source: List<Mapping>): List<Mapping> {
    var result = source.toMutableList()
    this.forEach { m ->
        val temp = mutableListOf<Mapping>()
        result.forEach { s ->
            if (s.offset == 0L) {
                // only remap ranges that haven't already been mapped
                temp.addAll(m.remap(s))
            } else {
                // otherwise it's already got an offset and hence was affected
                // by one of 'this' mappings, so leave it alone
                temp.add(s)
            }
        }
        result = temp
    }

    // note: this can result in overlapping results, could optimize them away
    // example if 'this' is [5-9] with offset 3, and 'source' is from [7-11],
    // result is two Mappings:
    //   [7-9] with offset 3 (this part was within 'this')
    //   [10-11] with offset 0 (this part was to the right of 'this')
    // after realigning #1 with offset 3, we get [10-12] and [10-11]
    // so in this case the second one could be removed

    return result.map { it.realign() }
}

// remap source per each group of 'this', sending results to each successive group
fun List<List<Mapping>>.doMapping(source: List<Mapping>): List<Mapping> =
    this.fold(source) { acc, list -> list.remap(acc) }

// --------------------------------------------------------

// Part I: operating on individual numbers (aka Mappings of length 1)
val seeds1 = lines[0].substring(7).split(" ")
    .map { Mapping(it.toLong(), 1, 0) }
val part1 = maps.doMapping(seeds1).minOf { it.start }
println("Part 1: $part1")

// Part II: operating on ranges
val seeds2 = lines[0].substring(7).split(" ")
    .windowed(2, 2)
    .map { Mapping(it[0].toLong(), it[1].toLong(), 0) }
val part2 = maps.doMapping(seeds2).minOf { it.start }
println("Part 2: $part2")
