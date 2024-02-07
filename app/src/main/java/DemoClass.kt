fun main() {
    val myFun = { name: String -> "Hello $name" }
    val message = myFun.invoke("Sid")
    println(message)

    val higherOrderFun = { name: String -> println(name)}

    higherOrderFun("Sid")
}