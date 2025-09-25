class Driver(_name: String, _surname: String, _second_name: String, _age: Int, _speed: Double, _time: Double, _x: Double, _y: Double): Human(_name, _surname, _second_name, _age, _speed, _time, _x, _y) {

    override fun move() {
        val s = speed * time
        x += s
    }
}