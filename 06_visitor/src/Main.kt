fun main() {
    val toRpnVisitor = ParserVisitor()
    val printVisitor = PrintVisitor(System.out)
    val calcVisitor = CalcVisitor()

    val tokens = Tokenizer(readLine()!!).tokens()

    try {
        toRpnVisitor.visitAll(tokens)
        printVisitor.visitAll(toRpnVisitor.rpn())
        calcVisitor.visitAll(toRpnVisitor.rpn())
    } catch (e : RuntimeException) {
        println("Error: ${e.message}")
        return
    }
    println(calcVisitor.result())
}