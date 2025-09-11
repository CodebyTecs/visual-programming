import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Human
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

    fun move() {
        val theta = Random.nextDouble(0.0, 2.0 * PI)
        val s = speed * time
        x += s * cos(theta)
        y += s * sin(theta)
    }

    fun moveTo(targetX: Double, targetY: Double) {
        x = targetX
        y = targetY
        println("${getHumanFullName()} перемещён в точку (%.2f, %.2f)".format(x, y))
    }

    fun printState(id: Int) {
        println(String.format(
            "Human%d: ФИО: %s, возраст: %d, скорость: %.2f ед/с, позиция: (%.2f, %.2f)",
            id, getHumanFullName(), getHumanAge(), speed, x, y
        ))
    }
}

fun main(){
    val humans = List(20) { i ->
        Human("Человек${i + 1}", "Иванов", "Тестович", 18 + (i % 5), (1..5).random().toDouble(), 1.0, 0.0, 0.0)
    }

    val simulationTime = 10

    for (t in 1..simulationTime) {
        println("Шаг времени $t")
        humans.forEachIndexed { idx, human ->
            human.move()
            human.printState(idx + 1)
        }
    }
}