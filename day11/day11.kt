import java.io.File

class Item(val worry: ULong)

class InspectionResult(val item: Item, val passTo: Int)

fun getOperation(line: String): (Item) -> Item {
    val operationText =  line.trim().replace("Operation: new = ", "")
    val (lhs, operator, rhs) = operationText.split(' ')


    return {item ->
        val old = item.worry
        val leftVal = if (lhs == "old") old else lhs.toULong()
        val rightVal =  if (rhs == "old") old else rhs.toULong()

        when(operator) {
            "+" -> Item(leftVal + rightVal)
            "*" -> Item(leftVal * rightVal)
            else -> throw Exception("Unknown operator: $operator")
        }
    }
}

fun getTest(test: String, ifTrue: String, ifFalse: String): (Item) -> Int {
    val divisibleBy = test.trim().split(' ').last().toULong()
    val trueIndex = ifTrue.trim().split(' ').last().toInt()
    val falseIndex = ifFalse.trim().split(' ').last().toInt()

    return { item ->
        if (item.worry % divisibleBy == 0UL) {
            trueIndex
        } else {
            falseIndex
        }
    }
}

class Monkey(input: List<String>, val postInspection: (Item) -> Item) {
    private val items: MutableList<Item>
    private val operation: (Item) -> Item
    private val test: (Item) -> Int
    private var inspectionCount = 0

    val inspected get() = inspectionCount

    init {
        val cleanedInput = input.drop(1).takeWhile { it.isNotEmpty() }
        items = cleanedInput[0]
            .trim()
            .replace("Starting items: ", "")
            .split(", ")
            .map{ Item(it.toULong()) }
            .toMutableList()

        operation = getOperation(cleanedInput[1])
        test = getTest(cleanedInput[2], cleanedInput[3], cleanedInput[4])
    }

    fun pushItem(item: Item) {
        items.add(item)
    }

    fun hasItems() = items.size > 0

    fun inspectNext(): InspectionResult = items.removeFirst()
        .let { operation(it) }
        .let { postInspection(it) }
        .let {
            InspectionResult(
                it,
                test(it)
            )
        }.also { inspectionCount++}
}

fun calculateMonkeyBusiness(monkeys: List<Monkey>, numRounds: Int): Long {
    repeat(numRounds) {
        monkeys.forEach {
            while(it.hasItems()) {
                val res = it.inspectNext()
                monkeys[res.passTo].pushItem(res.item)
            }
        }
    }

    return monkeys
        .map { it.inspected.toLong() }
        .sorted()
        .reversed()
        .slice(0 until 2)
        .reduce{ acc, it -> acc * it }
}

fun findLowestCommonMultiple(input: List<String>): ULong {
    val numbers = input
        .asSequence()
        .map { it.trim() }
        .filter { it.startsWith("Test:") }
        .map { it.split(" ").last() }
        .map { it.toULong() }
        .toList()

    val multiplied = numbers.toMutableList()

    fun allEqual() = multiplied.toHashSet().size == 1
    
    while (!allEqual()) {
        val lowestIndex = multiplied
            .mapIndexed { i, v -> Pair(i, v) }
            .minBy { it.second }
            .first

        multiplied[lowestIndex] += numbers[lowestIndex]
    }

    if (!allEqual()) {
        throw Exception("damn")
    }
    return multiplied[0]
}


fun main() {
    val input = File("input.txt").readLines()
    val monkeys1 = input.chunked(7).map{
        Monkey(it) { inspectedItem -> Item(inspectedItem.worry / 3UL) }
    }
    val solution1 = calculateMonkeyBusiness(monkeys1, 20)
    println("Solution 1: $solution1")

    val lcm = findLowestCommonMultiple(input)

    val monkeys2 = input.chunked(7).map{
        Monkey(it) { inspectedItem -> Item(inspectedItem.worry % lcm) }
    }
    val solution2 = calculateMonkeyBusiness(monkeys2, 10000)
    println(lcm)
    println("Solution2: $solution2")
}