import kotlinx.coroutines.*

suspend fun main() = coroutineScope{
    val simulationTime = 10
    val speed = 36.0
    val driver = Driver("Иван", "Иванов", "Иванович", 27, speed, 1.0, 0.0, 0.0)
    val firstHuman = Human("Петр", "Петров", "Петрович", 53, 2.0, 1.0, 0.0, 0.0)
    val secondHuman = Human("Владимир", "Владимиров", "Влвдимирович", 34, 3.0, 1.0, 0.0, 0.0)

    val jobs = listOf(
        launch(Dispatchers.Default) {
            repeat(simulationTime) { step ->
                driver.move()
                println("Шаг ${step + 1} — Driver")
                driver.printState(1)
                delay((driver.time * 1000).toLong())
            }
        },
        launch(Dispatchers.Default) {
            repeat(simulationTime) { step ->
                firstHuman.move()
                println("Шаг ${step + 1} — Human1")
                firstHuman.printState(2)
                delay((firstHuman.time * 1000).toLong())
            }
        },
        launch(Dispatchers.Default) {
            repeat(simulationTime) { step ->
                secondHuman.move()
                println("Шаг ${step + 1} — Human2")
                secondHuman.printState(3)
                delay((secondHuman.time * 1000).toLong())
            }
        }
    )

    jobs.joinAll()

    driver.printState(1)
    firstHuman.printState(2)
    secondHuman.printState(3)
}