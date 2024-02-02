




fun main() {
    val myFun = { name: String -> "Hello $name" }
    val message = myFun.invoke("Sid")
    println(message)
}