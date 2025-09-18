import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.*

open class Human
{
    var name: String = ""
    var surname: String = ""
    var second_name: String = ""
    var age: Int = 0
    var speed: Double = 0.0
    var time: Double = 1.0
    var x: Double = 0.0
    var y: Double = 0.0

    constructor(_name: String, _surname: String, _second_name: String, _age: Int, _speed: Double, _time: Double, _x: Double, _y: Double){
        name = _name
        surname = _surname
        second_name = _second_name
        age = _age
        speed = _speed
        time = _time
        x = _x
        y = _y
    }

    fun getHumanSurname(): String = surname
    fun setHumanSurname(value: String) { surname = value }

    fun getHumanName(): String = name
    fun setHumanName(value: String) { name = value }

    fun getSecondName(): String = second_name
    fun setSecondName(value: String) { second_name = value }

    fun getHumanFullName(): String = "$surname $name $second_name"

    fun getHumanAge(): Int = age
    fun setHumanAge(value: Int) {
        if (value >= 0) age = value else println("Возраст не может быть отрицательным")
    }

    fun getHumanSpeed(): Double = speed
    fun setHumanSpeed(value: Double) {
        if (value >= 0.0) speed = value else println("Скорость не может быть отрицательной")
    }

    fun getHumanX(): Double = x
    fun getHumanY(): Double = y
    fun setPosition(newX: Double, newY: Double) { x = newX; y = newY }

    fun getHumanTime(): Double = time
    fun setHumanTime(value: Double) {
        if (value > 0.0) time = value else println("time должно быть > 0")
    }

    open fun move() {
        val theta = Random.nextDouble(0.0, 2.0 * PI)
        val s = speed * time
        x += s * cos(theta)
        y += s * sin(theta)
    }

    fun printState(id: Int) {
        println(String.format(
            "ФИО: %s, возраст: %d, скорость: %.2f ед/с, позиция: (%.2f, %.2f)",
            getHumanFullName(), getHumanAge(), speed, x, y
        ))
    }
}

class Driver: Human{
    constructor(_name: String, _surname: String, _second_name: String, _age: Int, _speed: Double, _time: Double, _x: Double, _y: Double) : super(_name, _surname, _second_name, _age, _speed, _time, _x, _y)

    override fun move() {
        val s = speed * time
        x += s
    }
}

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