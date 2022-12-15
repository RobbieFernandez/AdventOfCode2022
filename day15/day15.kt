import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

val Pair<Int, Int>.x get() = first
val Pair<Int, Int>.y get() = second

class Sensor(val pos: Pair<Int, Int>, val beaconPos: Pair<Int, Int>) {
    val range get() = manhattanDistance(pos, beaconPos)

    fun xRange(y: Int): Pair<Int, Int> {
        val yDiff = abs(pos.y - y)
        val xRange = max(0, range - yDiff)
        return Pair(pos.x - xRange, pos.x + xRange)
    }


    fun isSafe(otherPos: Pair<Int, Int>) =
        otherPos != beaconPos &&
        manhattanDistance(pos, otherPos) <= range
}

fun manhattanDistance(point1: Pair<Int, Int>, point2: Pair<Int, Int>) =
    abs(point1.x - point2.x) + abs(point1.y - point2.y)

fun parseLine(line: String): Sensor {
    val values = line
        .split(" ")
        .filter { it.startsWith("x=") || it.startsWith("y=") }
        .map { it.split("=")[1].trim(',').trim(':').toInt() }

    val sensorPos = Pair(values[0], values[1])
    val beaconPos = Pair(values[2], values[3])
    return Sensor(sensorPos, beaconPos)
}

fun findGaps(ranges: List<Pair<Int, Int>>): Sequence<Pair<Int, Int>> = sequence {
    ranges.reduce { range, nextRange ->
        if (nextRange.first < range.second) {
            // Second range contained within previous range.
            // Merge them together
            Pair(
                range.first,
                max(range.second, nextRange.second)
            )
        } else if (nextRange.first - range.second > 1) {
            // Gap!
            yield(
                Pair(range.second + 1, nextRange.first - 1)
            )
            nextRange
        } else {
            nextRange
        }
    }
}

fun part2(sensors: List<Sensor>, searchSize: Int): Pair<Int, Int> {
    (0..searchSize).forEach { y ->
        val xRanges = sensors
            .map { it.xRange(y) }
            .filter{ it.first < searchSize && it.second > 0 }
            .map { Pair(max(it.first, 0), min(it.second, searchSize)) }
            .sortedBy { it.first }

        val gaps = findGaps(xRanges).toList()

        if (gaps.size == 1) {
            val (gapStart, gapEnd) = gaps[0]
            if (gapEnd > gapStart) {
                throw Exception("More than 1 solution found")
            }
            return Pair(gapStart, y)
        } else if (gaps.size > 1) {
            throw Exception("More than 1 solution found")
        }
    }
    throw Exception("Uh oh")
}

fun main() {
    val sensors = File("input.txt").readLines().map { parseLine(it) }

    // Part 1
    val leftX = sensors.minOf { it.pos.x - it.range }
    val rightX = sensors.maxOf { it.pos.x + it.range }
    val solution1 = (leftX..rightX).filter { x -> sensors.any { it.isSafe(Pair(x, 2000000)) } }
    println("Solution1: ${solution1.size}")

    // Part 2
    val solution2 = part2(sensors, 4000000)
    val tuningFrequency = 4000000UL * solution2.x.toULong() + solution2.y.toULong()
    println("Solution2: $tuningFrequency")
}