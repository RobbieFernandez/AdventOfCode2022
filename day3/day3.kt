import java.io.File

class Rucksack (val compartments: Pair<Set<Char>, Set<Char>>);

fun parseLine(line: String): Rucksack {
    val chars = line.toCharArray();
    val compartmentSize = chars.size;
    val compartment1 = HashSet(chars.slice(0 until compartmentSize / 2))
    val compartment2 = HashSet(chars.slice(compartmentSize / 2 until chars.size))
    return Rucksack(Pair(compartment1, compartment2))
}

fun charPriority(character: Char): Int {
    if (character < 'a') {
        return character.code - 'A'.code + 27
    }
    return character.code - 'a'.code + 1
}

fun getRuckSuckErrorPriority(rucksack: Rucksack): Int {
    val overlap = rucksack.compartments.first.intersect(
        rucksack.compartments.second
    )
    if (overlap.size > 1) {
        throw Error("Found >1 overlapping element in the rucksack's compartments.")
    }
    val overlappingChar = overlap.first()
    val priority = charPriority(overlappingChar)
    println("Overlapping char: $overlappingChar. Priority: $priority")
    return priority
}

fun getBadgePriority(rucksacks: Triple<Rucksack, Rucksack, Rucksack>): Int {
    val badge = rucksacks.toList()
        .map { it.compartments.first.union(it.compartments.second)}
        .reduce { first, second -> first.intersect(second) }

    if (badge.size > 1) {
        throw Error("Found >1 badge for a group of rucksacks")
    }

    return charPriority(badge.first())
}

fun main() {
    val input = File("input.txt").readLines().map { it.trim() }

    // Part 1
    val solution1 = input.map { parseLine(it) }.sumOf { getRuckSuckErrorPriority(it) }
    println("Part 1 solution: $solution1")

    // Part 2
    val solution2 = input
            .map { parseLine(it) }
            .chunked(3)
            .map { Triple(it[0], it[1], it[2])}
            .sumOf { getBadgePriority(it) }

    println("Part 2 solution: $solution2");
}