import java.io.File
import kotlin.math.abs

sealed class Instruction
class Noop() : Instruction()
class Add(val register: Char, val param: Int): Instruction()


fun parseInstruction(line: String): Instruction {
    val trimmed = line.trim()
    return if (line == "noop") {
        Noop()
    } else {
        val amt = Integer.parseInt(trimmed.split(" ")[1])
        Add('x', amt)
    }
}

class CPU(val screenWidth: Int, val screenHeight: Int) {
    private var registerX = 1
    private var nextCycle = 1
    private var strength = 0

    private var xPos = 0

    val signalStrength: Int get() = strength

    fun execute(instruction: Instruction) {
        when (instruction) {
            is Noop -> noOp()
            is Add -> add(instruction.register, instruction.param)
        }
    }

    private fun draw() {
        if (xPos == 0) {
            println()
        }
        val showingSprite = abs(xPos - registerX) <= 1
        print(if (showingSprite) '#' else '.')
    }

    private fun tick() {
        // This portion executes "during" the cycle
        draw()

        if ((nextCycle - 20) %40 == 0) {
            val currentStrength = nextCycle * registerX
            strength += currentStrength
        }

        // Increment the PC
        // Side effects of the instruction can occur now
        nextCycle++
        xPos = (xPos + 1) % screenWidth
    }

    private fun noOp() = tick()

    private fun add(register: Char, param: Int) {
        tick()
        tick()
        when (register) {
            'x' -> registerX += param
            else -> throw Exception("Unknown register: $register")
        }
    }
}

fun main() {
    val input = File("input.txt").readLines()
    val cpu = CPU(40, 6)
    input.map { parseInstruction(it) }.forEach { cpu.execute(it) }
    println("\n\nSolution1: ${cpu.signalStrength}")
}