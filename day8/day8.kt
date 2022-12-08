import java.io.File

fun buildGrid(input: Iterable<String>): List<List<Int>> = input.map { line ->
    line.map { Integer.parseInt(it.toString()) }
}

fun isVisible(grid: List<List<Int>>, cell: Pair<Int, Int>): Boolean {
    val cellValue = grid[cell.first][cell.second]
    val row = grid[cell.first]
    val visibleFromLeft = row.slice(0 until cell.second).all {
        it < cellValue
    }
    if (visibleFromLeft) return true

    val visibleFromRight = row.slice(cell.second + 1 until row.size).all {
        it < cellValue
    }
    if (visibleFromRight) return true

    val col = grid.map { it[cell.second] }
    val visibleFromTop = col.slice(0 until cell.first).all {
        it < cellValue
    }
    if (visibleFromTop) return true

    return col.slice(cell.first + 1 until col.size).all {
        it < cellValue
    }
}

fun scenicScore(grid: List<List<Int>>, cell: Pair<Int, Int>): Int {
    val cellValue = grid[cell.first][cell.second]
    val indexedRow = grid[cell.first].mapIndexed { index, cell -> Pair(index, cell) }
    val indexedCol = grid.map { it[cell.second] }. mapIndexed { index, cell -> Pair(index, cell) }

    val topBlocker = (cell.first - 1 downTo 0).find {
        indexedCol[it].second >= cellValue
    } ?: 0

    val topDistance = cell.first - topBlocker

    val bottomBlocker = (cell.first + 1 until indexedCol.size).find {
        indexedCol[it].second >= cellValue
    } ?: indexedCol.size - 1

    val bottomDistance = bottomBlocker - cell.first

    val leftBlocker = (cell.second - 1 downTo 0).find {
        indexedRow[it].second >= cellValue
    } ?: 0
    val leftDistance = cell.second - leftBlocker

    val rightBlocker = (cell.second + 1 until indexedRow.size).find {
        indexedRow[it].second >= cellValue
    } ?: indexedRow.size - 1
    val rightDistance = rightBlocker - cell.second

    return topDistance * bottomDistance * leftDistance * rightDistance
}


fun main() {
    val input = File("input.txt").readLines()
    val treeGrid = buildGrid(input)

    val rows = treeGrid.size
    val cols = treeGrid[0].size
    val cells = (0 until rows).map { row ->
        (0 until cols).map { col -> Pair(row, col)}
    }.flatten()

    // Part 1
    val visibleTrees = cells.filter { isVisible(treeGrid, it).also{vis -> if (vis) println("$it.first, $it.second") } }
    println("Solution 1: ${visibleTrees.size}")

    // Part 2
    val maxScore = cells.map { scenicScore(treeGrid, it) }.max()
    println("Solution 2: $maxScore")
}