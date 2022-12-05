import java.io.File

data class Move(val count: Int, val from: Int, val to: Int);

fun parseBoxStacks(stackCount: Int, input: Iterable<String>): List<ArrayDeque<Char>> {
    val stacks = (0 until stackCount).map { ArrayDeque<Char>() }
    input.forEach {line ->
        (0 until stackCount).map {stackIndex ->
            val charIndex = stackIndex * 4 + 1
            val boxChar = line[charIndex]
            if (boxChar != ' ') {
                stacks[stackIndex].addLast(boxChar)
            }
        }
    }
    return stacks
}

fun parseMove(line: String): Move {
    val regex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
    val (count, from, to) = regex.find(line)!!.destructured;
    return Move(
        count.toInt(),
        from.toInt() - 1,  // Convert to zero-indexed
        to.toInt() - 1
    )
}

fun executeMove(boxStacks: List<ArrayDeque<Char>>, move: Move) {
    repeat(move.count) {
        boxStacks[move.to].addFirst(
            boxStacks[move.from].removeFirst()
        )
    }
}

fun executeMove2(boxStacks: List<ArrayDeque<Char>>, move: Move) {
    boxStacks[move.from].slice(0 until move.count).reversed().forEach {
        boxStacks[move.to].addFirst(it)
    }

    repeat(move.count) {
        boxStacks[move.from].removeFirst()
    }
}

fun printStacks(boxStacks: List<ArrayDeque<Char>>) {
    boxStacks.forEachIndexed { i, stack ->
        println("$i: ${stack.joinToString()}")
    }
}

fun main() {
    val input = File("input.txt").readLines()
    val count = input
        .find { it.startsWith(" 1") }!!
        .split("  ").maxOfOrNull { it.trim().toInt() }!!

    val moves = input.dropWhile { !it.startsWith("move") }.map {
        parseMove(it)
    }

    // Part 1
    val boxStacks1 = parseBoxStacks(count, input.takeWhile { !it.startsWith(" 1") })
    moves.forEach { executeMove(boxStacks1, it) }
    println("Solution 1: ${boxStacks1.map { it.first() }.joinToString("")}")

    // Part 2
    val boxStacks2 = parseBoxStacks(count, input.takeWhile { !it.startsWith(" 1") })
    moves.forEach { executeMove2(boxStacks2, it) }
    println("Solution 2: ${boxStacks2.map { it.first() }.joinToString("")}")
}