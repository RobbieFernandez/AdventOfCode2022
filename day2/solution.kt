enum class Move {
    ROCK, PAPER, SCISSORS
}

enum class Outcome {
    WIN, LOSE, DRAW
}

fun gameScore(opponentMove: Move, playerMove: Move): Int {
    val playerWins = when(opponentMove) {
        Move.ROCK -> playerMove == Move.PAPER
        Move.PAPER -> playerMove == Move.SCISSORS
        Move.SCISSORS -> playerMove == Move.ROCK
    }

    val playerDraw = !playerWins && opponentMove == playerMove

    val shapeScore = when(playerMove) {
        Move.ROCK -> 1
        Move.PAPER -> 2
        Move.SCISSORS -> 3
    }

    return shapeScore + (if (playerWins) 6 else if (playerDraw) 3 else 0)
}

fun gameScore(opponentMove: Move, gameOutcome: Outcome): Int {
    // Gross
    val playerMove = when(opponentMove) {
        Move.ROCK -> when(gameOutcome) {
            Outcome.LOSE -> Move.SCISSORS
            Outcome.DRAW -> Move.ROCK
            Outcome.WIN -> Move.PAPER
        }
        Move.PAPER -> when(gameOutcome) {
            Outcome.LOSE -> Move.ROCK
            Outcome.DRAW -> Move.PAPER
            Outcome.WIN -> Move.SCISSORS
        }
        Move.SCISSORS -> when(gameOutcome) {
            Outcome.LOSE -> Move.PAPER
            Outcome.DRAW -> Move.SCISSORS
            Outcome.WIN -> Move.ROCK
        }
    }

    return gameScore(opponentMove, playerMove)
}

fun parseMove(move: String): Move {
    return when (move) {
        "A" -> Move.ROCK
        "B" -> Move.PAPER
        "C" -> Move.SCISSORS
        "X" -> Move.ROCK
        "Y" -> Move.PAPER
        else -> Move.SCISSORS  // Assume "Z"
    }
}

fun parseOutcome(outcome: String): Outcome {
    return when (outcome) {
        "X" -> Outcome.LOSE
        "Y" -> Outcome.DRAW
        else -> Outcome.WIN
    }
}

fun nextRound(): Pair<String, String>? {
    try {
        val line = readln()
        val moves = line.split(' ')
        return Pair(
            moves[0],
            moves[1]
        )
    } catch (ex: RuntimeException) {
        // EOF
        return null;
    }
}

fun main() {
    // Parse input
    val rounds = ArrayList<Pair<String, String>>()
    var round = nextRound()

    while (round != null) {
        rounds.add(round)
        round = nextRound()
    }

    // Part 1
    var score1 = 0;

    rounds.forEach {
        score1 += gameScore(
            parseMove(it.first),
            parseMove(it.second)
        )
    }

    println("Part 1: ${score1}");

    // Part 2
    var score2 = 0;

    rounds.forEach {
        score2 += gameScore(
            parseMove(it.first),
            parseOutcome(it.second)
        )
    }

    println("Part 1: ${score2}");
}