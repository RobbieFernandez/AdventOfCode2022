import java.io.File

class Grid(input: List<String>) {
    private val grid: List<List<Int>>

    init {
        grid = input.map { line ->
            line.map { Integer.parseInt(it.toString()) }
        }
    }

    val width: Int get() = grid[0].size
    val height: Int get() = grid.size

    class Tree(private val grid: Grid, private val col: Int, private val row: Int, val height: Int) {
        private fun left() = if (col > 0) grid.getTree(row, col - 1) else null
        private fun right() = if (col < (grid.width - 1)) grid.getTree(row, col + 1) else null
        private fun up() = if (row > 0) grid.getTree(row - 1, col) else null
        private fun down() = if (row < (grid.height - 1)) grid.getTree(row + 1, col) else null

        private fun walk(nextTree: (Tree) -> Tree?): Sequence<Tree> {
            val thisTree = this;
            return sequence<Tree> {
                var tree = nextTree(thisTree)
                while (tree !== null) {
                    yield(tree)
                    tree = nextTree(tree)
                }
            }
        }

        private fun firstBlockingTree(nextTree: (Tree) -> Tree?): Tree? =
            walk(nextTree).find { it.height >= height }

        private fun walkLeft() = walk { it.left() }
        private fun walkRight() = walk { it.right() }
        private fun walkUp() = walk { it.up() }
        private fun walkDown() = walk { it.down() }

        private fun visibilityLeft() = firstBlockingTree { it.left() }?.let{ col - it.col } ?: col
        private fun visibilityRight() = firstBlockingTree { it.right() }?.let { it.col - col } ?: (grid.width - col - 1)
        private fun visibilityUp() = firstBlockingTree { it.up() }?.let { row - it.row } ?: row
        private fun visibilityDown() = firstBlockingTree { it.down() }?.let { it.row - row } ?: (grid.height - row - 1)

        fun isVisible()= (
            walkLeft().all { it.height < height} ||
            walkRight().all { it.height < height} ||
            walkUp().all { it.height < height} ||
            walkDown().all { it.height < height}
        )

        fun scenicScore()= listOf(
            visibilityLeft(),
            visibilityRight(),
            visibilityUp(),
            visibilityDown()
        ).reduce { acc, it -> acc * it}
    }

    fun getTree(row: Int, col: Int) = Tree(this, col, row, grid[row][col])

    fun treeIterator() = sequence {
        for (row in (0 until height)) {
            for (col in (0 until width)) {
                yield(getTree(row, col))
            }
        }
    }
}


fun main() {
    val input = File("input.txt").readLines()
    val treeGrid = Grid(input)

    val visibleTrees = treeGrid.treeIterator().filter { it.isVisible() }.toList()
    println("Solution 1: ${visibleTrees.size}")

    val maxScore = treeGrid.treeIterator().map { it.scenicScore() }.max()
    println("Solution 2: $maxScore")
}