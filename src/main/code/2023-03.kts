val lines = java.io.File("../resources/input-03.txt").readLines()
// val lines = java.io.File("../resources/test.txt").readLines()

// part number-centric: find numbers and check around them for symbols
fun part1(): Int {
    fun isPart(row: Int, col: Int): Pair<Int?, Int> {
        var end = col
        while (end < lines[row].length && lines[row][end].isDigit()) end++
        val success = Pair(lines[row].substring(col, end).toInt(), (end - col))
        val fail = Pair(null, (end - col))

        if (row > 0) {
            for (ic in col - 1..end) {
                if (ic >= 0 && ic < lines[row - 1].length) {
                    if (!lines[row - 1][ic].isDigit() && lines[row - 1][ic] != '.') return success
                }
            }
        }
        if ((col > 0) && (lines[row][col - 1] != '.')) return success
        if ((end < lines[row].length - 1) && (lines[row][end] != '.')) return success
        if (row < lines.size - 1) {
            for (ic in col - 1..end) {
                if (ic >= 0 && ic < lines[row + 1].length) {
                    if (!lines[row + 1][ic].isDigit() && lines[row + 1][ic] != '.') return success
                }
            }
        }

        return fail
    }

    var r = 0
    var c = 0
    var sum = 0

    while (r < lines.size) {
        while (c < lines[r].length) {
            if (lines[r][c].isDigit()) {
                val (value, len) = isPart(r, c)
                // println("[r] $r [c] $c [val] $value [len] $len")
                sum += value ?: 0
                c += len
            }
            c++
        }
        r++
        c = 0
    }

    return sum
}

// symbol-centric: find * and find numbers around them
fun part2(): Int {
    fun isDigit(row: Int, col: Int): Boolean =
        (row >= 0 && row < lines.size && col >= 0 && col < lines[row].length && lines[row][col].isDigit())

    fun partNumber(row: Int, col: Int): Int {
        var start = col
        var end = col
        while (isDigit(row, start)) start--
        while (isDigit(row, end)) end++
        return lines[row].substring(start + 1, end).toInt()
    }

    fun gearRatio(row: Int, col: Int): Int? {
        val parts = mutableListOf<Int>()
        if (isDigit(row - 1, col)) {
            parts.add(partNumber(row - 1, col))
        } else {
            if (isDigit(row - 1, col - 1)) {
                parts.add(partNumber(row - 1, col - 1))
            }
            if (isDigit(row - 1, col + 1)) {
                parts.add(partNumber(row - 1, col + 1))
            }
        }
        if (isDigit(row + 1, col)) {
            parts.add(partNumber(row + 1, col))
        } else {
            if (isDigit(row + 1, col - 1)) {
                parts.add(partNumber(row + 1, col - 1))
            }
            if (isDigit(row + 1, col + 1)) {
                parts.add(partNumber(row + 1, col + 1))
            }
        }
        if (isDigit(row, col - 1)) parts.add(partNumber(row, col - 1))
        if (isDigit(row, col + 1)) parts.add(partNumber(row, col + 1))

        if (parts.size == 2) {
            return parts[0] * parts[1]
        }
        return null
    }

    var sum = 0
    for (r in lines.indices) {
        for (c in lines[r].indices) {
            if (lines[r][c] == '*') sum += gearRatio(r, c) ?: 0
        }
    }
    return sum
}

println("Part 1: ${part1()}")
println("Part 2: ${part2()}")
