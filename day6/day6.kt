import java.io.File

fun findStartPacket(stream: String, packetLength: Int): Int {
    val packetIndex = stream.windowed(packetLength, partialWindows = false).indexOfFirst { window ->
        window.toSet().size == window.length
    }
    return packetIndex + packetLength
}

fun main() {
    val input = File("input.txt").readText()

    // Day 1
    val solution1 = findStartPacket(input, 4)
    println("Solution 1: $solution1")

    // Day 2
    val solution2 = findStartPacket(input, 14)
    println("Solution 2: $solution2")
}