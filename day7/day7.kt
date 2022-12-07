import java.io.File

sealed class Command
class CD (val dirName: String): Command()
class LS (val output: Iterable<String>): Command()

class FileNode (val name: String, val size: Int)

class DirectoryNode(val name: String, val parent: DirectoryNode? = null) {
    private val children: MutableList<DirectoryNode> = mutableListOf()
    private val files: MutableList<FileNode> = mutableListOf()

    fun addSubDirectory(name: String) = DirectoryNode(name, this).also { children.add(it) }
    fun addFile(file: FileNode) = files.add(file)

    val size: Int get() {
        val mySize = files.sumOf { it.size }
        return mySize + children.sumOf { it.size }
    }

    fun iterator(): Sequence<DirectoryNode> {
        val root = this
        return sequence {
            yield(root)
            for (child in children) {
                yieldAll(child.iterator())
            }
        }
    }
}

fun parseTerminalCommands(terminalLines: List<String>): Sequence<Command> = sequence {
    var pendingLs: MutableList<String>? = null

    for (line in terminalLines) {
        if (line.startsWith("$")) {
            pendingLs?.let {
                yield(LS(it))
                pendingLs = null
            }

            if (line.startsWith("$ cd")) {
                val dirName = line.split(" ")[2]
                yield(CD(dirName))
            }
        } else {
            pendingLs?.add(line) ?: run { pendingLs = mutableListOf(line) }
        }
    }

    pendingLs?.let { yield(LS(it)) }
}

fun buildFilesystem(commands: Iterable<Command>): DirectoryNode {
    val rootDir = DirectoryNode("/")
    var activeDir = rootDir

    for (command in commands) {
        when (command) {
            is CD -> {
                activeDir = when(command.dirName) {
                    "/" -> rootDir
                    ".." -> activeDir.parent!!
                    else -> activeDir.addSubDirectory(command.dirName)
                }
            }
            is LS -> {
                for (line in command.output) {
                    val splitLine = line.split(" ")
                    val size = splitLine[0]
                    if (size != "dir") {
                        activeDir.addFile(
                            FileNode(splitLine[1], size.toInt())
                        )
                    }
                }
            }
        }
    }

    return rootDir
}

fun main() {
    val input = File("input.txt").readLines()

    val commands = parseTerminalCommands(input).toList()
    val fileSystem = buildFilesystem(commands)

    // Part 1
    val maxSize = 100000
    val filteredDirectories = fileSystem.iterator().filter { it.size <= maxSize }
    val totalSize = filteredDirectories.sumOf { it.size }
    println("Solution 1: $totalSize")

    // Part 2
    val totalSpace = 70000000
    val freeSpace = totalSpace - fileSystem.size
    val neededSpace = 30000000
    val spaceToFree = neededSpace - freeSpace

    val potentialDirectories = fileSystem.iterator().filter { it.size >= spaceToFree }
    val targetSize = potentialDirectories.minOf { it.size }
    println("Solution 2: $targetSize")
}