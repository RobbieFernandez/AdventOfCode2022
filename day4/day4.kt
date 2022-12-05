import java.io.File


fun readRange(range: String): Set<Int> {
    val splitLine = range.split('-')
    val lowerBound = splitLine[0].toInt()
    val upperBound = splitLine[1].toInt()
    return (lowerBound..upperBound).toSet();
}

fun parseLine(line: String): Pair<Set<Int>, Set<Int>> {
    val splitLine = line.split(",")
    return Pair(
        readRange(splitLine[0]),
        readRange(splitLine[1])
    )
}

fun overlap(range1: Set<Int>, range2: Set<Int>): Boolean {
    val intersection = range1 intersect range2
    return intersection == range1 || intersection == range2
}

fun main() {
    val input = File("input.txt").readLines().map { it.trim() }

    // Part 1
    val solution1 = input.map { parseLine(it) }.filter { overlap(it.first, it.second) }.size
    println("Part 1 solution: $solution1")

    // Part 2
    val solution2 = input.map { parseLine(it) }.filter { (it.first intersect it.second).isNotEmpty() }.size
    println("Part 2 solution: $solution2");
}