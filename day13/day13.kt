import java.io.File

sealed class Packet {
    abstract operator fun compareTo(packet: Packet): Int
}

class NumberPacket(val value: Int): Packet() {
    fun toListPacket(): ListPacket = ListPacket().also { it.addPacket(this) }

    operator fun compareTo(otherNumberPacket: NumberPacket) = this.value - otherNumberPacket.value
    operator fun compareTo(listPacket: ListPacket) = this.toListPacket().compareTo(listPacket)

    // Need this to convince the compiler that I've implemented the abstract compareTo method
    override fun compareTo(packet: Packet): Int = when(packet) {
        is NumberPacket -> compareTo(packet)
        is ListPacket -> compareTo(packet)
    }
}

class ListPacket(): Packet() {
    private val packetList = mutableListOf<Packet>()
    private val packets get() = packetList.toList()

    fun addPacket(packet: Packet) = packetList.add(packet)

    operator fun compareTo(otherList: ListPacket): Int {
        packets.zip(otherList.packets).forEach { (myPacket, otherPacket) ->
            val compared = myPacket.compareTo(otherPacket)
            if (compared != 0) {
                return compared
            }
        }
        return packetList.size - otherList.packets.size
    }

    operator fun compareTo(numberPacket: NumberPacket): Int = compareTo(numberPacket.toListPacket())

    // Need this to convince the compiler that I've implemented the abstract compareTo method
    override fun compareTo(packet: Packet): Int = when(packet) {
        is NumberPacket -> compareTo(packet)
        is ListPacket -> compareTo(packet)
    }
}

fun parseListPacket(stringGenerator: Sequence<String>): ListPacket {
    val packetStack = mutableListOf<ListPacket>()

    stringGenerator.forEach {
        when (it) {
            "[" -> packetStack.add(0, ListPacket())
            "]" -> {
                val closedPacket = packetStack.removeFirst()
                if (packetStack.size == 0) {
                    // Closed the topmost list packet - return it as the final result
                    return closedPacket
                } else {
                    // Closed an inner list packet. Append it to the topmost list packet in the stack
                    packetStack.first().addPacket(closedPacket)
                }
            }
            else -> packetStack.first().addPacket(NumberPacket(it.toInt()))
        }
    }

    throw Exception("Failed to parse packets")
}

fun inputSequence(line: String): Sequence<String> = sequence {
    var buf = ""
    line.forEach {
        when (it) {
            ',' -> if (buf != "") {
                yield(buf)
                buf = ""
            }
            '[' -> {
                if (buf != "") {
                    yield(buf)
                    buf = ""
                }
                yield("[")
            }
            ']' -> {
                if (buf != "") {
                    yield(buf)
                    buf = ""
                }
                yield("]")
            }
            else -> buf += it
        }
    }
}

fun main() {
    val input = File("input.txt").readLines()
    val packets = input.filter { it.isNotEmpty() }.map { inputSequence(it) }.map { parseListPacket(it) }

    val solution1 = packets
        .chunked(2)
        .mapIndexed { i, p -> Pair(i + 1, p) }
        .filter { (i, p) -> p[0] < p[1] }
        .sumOf { it.first }

    println("Solution 1: $solution1")

    val divider1 = ListPacket().also { it.addPacket(NumberPacket(2).toListPacket()) }
    val divider2 = ListPacket().also { it.addPacket(NumberPacket(6).toListPacket()) }
    val dividerPackets = listOf(divider1, divider2)

    val sortedPackets = (packets + dividerPackets).sortedWith { first, second -> first.compareTo(second) }
    val dividerIndex1 = sortedPackets.indexOf(divider1) + 1
    val dividerIndex2 = sortedPackets.indexOf(divider2) + 1
    val solution2 = dividerIndex1 * dividerIndex2
    println("Solution2: $solution2")
}
