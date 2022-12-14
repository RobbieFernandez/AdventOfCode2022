import java.io.File

val Pair<Int, Int>.x get() = first
val Pair<Int, Int>.y get() = second

fun parseInput(input: List<String>): HashSet<Pair<Int, Int>> = input.map { line ->
        val cells = line.split(" -> ").map {
            val (x, y) = it.split(',').map{ it.toInt() }
            Pair(x, y)
        }
        cells.windowed(2).map { walk(it[0], it[1]).toList() }.flatten()
    }.flatten().toHashSet()


fun walk(start: Pair<Int, Int>, end: Pair<Int, Int>): Sequence<Pair<Int, Int>>  = sequence {
    val (xStart, xEnd) = listOf(start.x, end.x).sorted()
    val (yStart, yEnd) = listOf(start.y, end.y).sorted()

    (xStart..xEnd).forEach { x ->
        (yStart..yEnd).forEach { y ->
            yield(Pair(x, y))
        }
    }
}

fun simulate(
    rocks: HashSet<Pair<Int, Int>>,
    startingPos: Pair<Int, Int>,
    floorLevel: Int,
    stopCondition: (Pair<Int, Int>) -> Boolean
): HashSet<Pair<Int, Int>> {
    val sandPositions = hashSetOf<Pair<Int, Int>>()

    fun spaceFree(space: Pair<Int, Int>) =
        !rocks.contains(space) &&
        !sandPositions.contains(space)  &&
        space.y < floorLevel

    while (true) {
        var sand = startingPos

        while(true) {
            val updated = listOf(
                Pair(sand.x, sand.y + 1),
                Pair(sand.x - 1, sand.y + 1),
                Pair(sand.x + 1, sand.y + 1),
            ).firstOrNull { spaceFree(it) }
            updated?.let { sand = it } ?: break
        }

        if (stopCondition(sand)) {
            return sandPositions
        }

        sandPositions.add(sand)
    }
}


fun main() {
    val input = File("input.txt").readLines()
    val rocks = parseInput(input)
    val floorLevel = rocks.maxOf { it.y }
    val sand = simulate(rocks, Pair(500, 0), floorLevel + 1) { it.y == floorLevel }

    println("Solution 1: ${sand.size}")

    val sand2 = simulate(rocks, Pair(500, 0), floorLevel + 2) { it.x == 500 && it.y == 0 }
    println("Solution 2: ${sand2.size + 1}")
}