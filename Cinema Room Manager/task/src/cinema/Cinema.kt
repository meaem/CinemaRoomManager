package cinema

const val MAX = 60

typealias MOption = Menu.MenuOption

class Menu(val options: List<MenuOption>) {
    val menu = options.map { it.optionString }.joinToString("\n")
    fun display() {
        println(menu)
    }

    fun matchedOption(choice: String) = options.firstOrNull { it.choiceMatch(choice) }

    class MenuOption(val optionString: String, private val choice: String, private val action: () -> Unit) {

        fun choiceMatch(otherChoice: String) = choice == otherChoice
        fun doAction() {
            action()
        }
    }


}


class Seat {
    private var status: String = "S"
    override fun toString(): String {
        return status
    }

    fun reserve(): Boolean {
        return if (status == "S") {
            status = "B"
            true
        } else false
    }

    fun isReserved() = status == "B"

}

class Cinema(val rows: Int, val seatsPerRow: Int) {
    val seats = Array(rows) { Array(seatsPerRow) { Seat() } }

    var purchasedTickets = 0
        private set
    var income = 0
        private set

    val roomSize = rows * seatsPerRow

    override fun toString(): String {
        return "Cinema:\n${cinemaDispaly()}"
    }

    private fun cinemaDispaly(): String {
        val header = " " + (1..seatsPerRow).joinToString(" ")
//        val seats = " S".repeat(seatsPerRow)
        val rowsStr = (1..rows).map { "$it ${seats[it - 1].joinToString(" ")}" }.joinToString("\n")
        return " $header\n$rowsStr"
    }

    fun calcTicketsPrice(row: Int): Int {

//        return if (row * seat <= MAX) row * seat * 10 else {
//            val firstHalf = row / 2
//            val secHalf = row - firstHalf
//
//            seat * (firstHalf * 10 + secHalf * 8)
//        }

        return if (roomSize <= MAX || row <= rows / 2) 10 else 8
    }

    private fun checkIfSeatNumIsValid(row: Int, seat: Int): Boolean {
        return row - 1 in 0 until rows && seat - 1 in 0 until seatsPerRow
    }

    fun reserveSeat(row: Int, seat: Int): Boolean {
        if (checkIfSeatNumIsValid(row, seat)) {
            purchasedTickets++
            income += calcTicketsPrice(row)
            return seats[row - 1][seat - 1].reserve()
        } else throw IllegalArgumentException("Wrong input!")
    }

    fun calcTotalIncoem(): Int {
        return (1..rows).map { calcTicketsPrice(it) * seatsPerRow }.sum()
    }

}

fun buy() {

}

fun exit() {

}

fun main() {
    println("Enter the number of rows:")
    val rows = readln().toInt()
    println("Enter the number of seats in each row:")
    val seats = readln().toInt()
    val cinema = Cinema(rows, seats)
    var running = true

    val optionsList = listOf(
        MOption("1. Show the seats", "1") { println("\n$cinema\n") },
        MOption("2. Buy a ticket", "2") {

            var isBusy = true
            while (isBusy) {
                try {
                    println("\nEnter a row number:")
                    val row = readln().toInt()
                    println("Enter a seat number in that row:")
                    val seat = readln().toInt()

                    if (cinema.reserveSeat(row, seat)) {
                        println("Ticket price: $${cinema.calcTicketsPrice(row)}")
                        isBusy = false

                    } else {
                        println("That ticket has already been purchased")

                    }
                } catch (ex: IllegalArgumentException) {
                    println(ex.message)
                }
            }
        },

        MOption("3. Statistics", "3") {
            val prct = String.format("%.2f", cinema.purchasedTickets * 100.0 / cinema.roomSize)
            println(
                "Number of purchased tickets: ${cinema.purchasedTickets}\n" +
                        "Percentage: ${prct}%\n" +
                        "Current income: \$${cinema.income}\n" +
                        "Total income: \$${cinema.calcTotalIncoem()}"
            )
        },
        MOption("0. Exit", "0") { running = false }
    )

    val menu = Menu(optionsList)
    while (running) {
        menu.display()
        val choice = readln()
        val selectedOption = menu.matchedOption(choice)
        if (selectedOption != null) {
            selectedOption.doAction()
        } else {
            println("Invalid Option")
        }
    }


}