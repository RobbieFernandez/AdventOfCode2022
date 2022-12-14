import java.io.File

val Pair<Int, Int>.row get() = first
val Pair<Int, Int>.col get() = second
operator fun List<List<Int>>.get(p: Pair<Int, Int>) = this[p.first][p.second]

class Node(val pos: Pair<Int, Int>, val height: Int, val parent: Node? = null) {
    fun getChildren(heightMap: List<List<Int>>): Sequence<Node> {
        val parent = this
        return sequence{
            fun canMove(nextHeight: Int): Boolean {
                return nextHeight <= height + 1
            }

            // Up
            if (pos.row > 0) {
                val up = Pair(pos.row - 1, pos.col)
                val upHeight = heightMap[up]
                if (canMove(upHeight)) {
                    yield(
                        Node(up, upHeight, parent)
                    )
                }
            }

            // Down
            if (pos.row < heightMap.size - 1) {
                val down = Pair(pos.row + 1, pos.col)
                val downHeight = heightMap[down]
                if (canMove(downHeight)) {
                    yield(
                        Node(down, downHeight, parent)
                    )
                }
            }

            // Left
            if (pos.col > 0) {
                val left = Pair(pos.row, pos.col - 1)
                val leftHeight = heightMap[left]
                if (canMove(leftHeight)) {
                    yield(
                        Node(left, leftHeight, parent)
                    )
                }
            }

            // Right
            if (pos.col < heightMap[0].size - 1) {
                val right = Pair(pos.row, pos.col + 1)
                val rightHeight = heightMap[right]
                if (canMove(rightHeight)) {
                    yield(
                        Node(right, rightHeight, parent)
                    )
                }
            }
        }
    }
}

fun parseChar(character: Char): Int = when(character) {
    'S' -> parseChar('a')
    'E' -> parseChar('z')
    else -> character.code - 'a'.code
}

fun parseHeightMap(input: List<String>) = input.map { line->
    line.map { parseChar(it) }
}

fun findChar(input: List<String>, target: Char): Pair<Int, Int> {
    input.forEachIndexed { row, line ->
        line.forEachIndexed { col, value ->
            if (value == target) {
                return Pair(row, col)
            }
        }
    }
    throw Exception("Did not find starting point")
}

fun findPath(heightMap: List<List<Int>>, start: Pair<Int, Int>, end: Pair<Int, Int>): List<Node> {
    val root = Node(start, heightMap[start.row][start.col])
    val toVisit = root.getChildren(heightMap).toMutableList()
    val seen = hashSetOf(root.pos)

    while(toVisit.size > 0) {
        val node = toVisit.removeAt(0)
        if (node.pos == end) {
            return unwindPath(node).toList().reversed()
        }
        node.getChildren(heightMap).filterNot{ seen.contains(it.pos) }.forEach {
            toVisit.add(it)
            seen.add(it.pos)
        }
    }
    throw Exception("Did not find path")
}

fun unwindPath(node: Node): Sequence<Node> = sequence {
    var current: Node? = node
    while (current != null) {
        yield(current)
        current = current.parent
    }
}

fun main() {
    val input = File("input.txt").readLines()
    val heightMap = parseHeightMap(input)
    val startingPoint = findChar(input, 'S')
    val finishingPoint = findChar(input, 'E')

    val path = findPath(heightMap, startingPoint, finishingPoint)
    println("Solution 1: ${path.size - 1}")

    val potentialStartingPoints = List(heightMap.size) { row -> List(heightMap[row].size) { col -> Pair(row, col)} }
        .flatten()
        .filter { heightMap[it] == parseChar('a') }

    val minPath = potentialStartingPoints.mapNotNull {
        try {
            findPath(heightMap, it, finishingPoint).size
        } catch (e: Exception) {
            null
        }
    }.min()

    println("Solution 2: ${minPath - 1}")
}