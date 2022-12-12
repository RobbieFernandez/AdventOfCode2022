import java.io.File
import kotlin.math.abs

enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

class Knot(var x: Int, var y: Int)

class Rope(length: Int) {
    private val tailVisited = hashSetOf<Pair<Int, Int>>()
    private var knots: List<Knot>

    init {
        knots = List(length) { Knot(0, 0) }
    }

    val tailVisitCount get() = tailVisited.size

    fun move(dir: Direction) {
        val head = knots[0]
        when(dir) {
            Direction.UP -> head.y -= 1
            Direction.DOWN -> head.y += 1
            Direction.LEFT -> head.x -= 1
            Direction.RIGHT -> head.x += 1
        }

        knots.windowed(2).forEach {
            followKnot(it[0], it[1])
        }

        val tail = knots.last()
        tailVisited.add(Pair(tail.x, tail.y))
    }

    private fun followKnot(leader: Knot, follower: Knot) {
        val xDiff = abs(leader.x - follower.x)
        val yDiff = abs(leader.y - follower.y)
        if (xDiff < 2 && yDiff < 2) {
            return
        }

        if (follower.x == leader.x) {
            // Need to move up or down
            if (leader.y > follower.y + 1) {
                follower.y += 1
            } else if (leader.y < follower.y - 1) {
                follower.y -= 1
            }
        } else if (follower.y == leader.y) {
            // Need to move left or right
            if (leader.x > follower.x + 1) {
                follower.x += 1
            } else if (leader.x < follower.x - 1) {
                follower.x -= 1
            }
        } else {
            // Need to move diagonally
            if (leader.y > follower.y) {
                follower.y += 1
            } else {
                follower.y -= 1
            }

            if (leader.x > follower.x) {
                follower.x += 1
            } else {
                follower.x -= 1
            }
        }
    }
}

fun iterMoves(input: Iterable<String>) : Sequence<Direction> = sequence {
    input.forEach { line ->
        val splitLine = line.split(" ");
        val direction = when(splitLine[0]) {
            "U" -> Direction.UP
            "R" -> Direction.RIGHT
            "D" -> Direction.DOWN
            else -> Direction.LEFT
        }
        val count = Integer.parseInt(splitLine[1])
        repeat(count) {
            yield(direction)
        }
    }
}

fun main() {
    val input = File("input.txt").readLines()
    val rope = Rope(2)
    iterMoves(input).forEach { rope.move(it) }
    println("Solution 1: ${rope.tailVisitCount}")

    val rope2 = Rope(10)
    iterMoves(input).forEach { rope2.move(it) }
    println("Solution 2: ${rope2.tailVisitCount}")
}