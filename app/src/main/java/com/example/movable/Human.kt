import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Human(_name: String, _surname: String, _second_name: String, _age: Int, _speed: Double, _time: Double, _x: Double, _y: Double) : Movable
{
    var name: String = _name
    var surname: String = _surname
    var second_name: String = _second_name
    var age: Int = _age
    override var speed = _speed
    override var time = _time
    override var x = _x
    override var y = _y

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

    override fun move() {
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