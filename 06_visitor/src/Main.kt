fun main() {
    val toRpnVisitor = ParserVisitor()
    val printVisitor = PrintVisitor(System.out)

    val tokens = Tokenizer(readLine()!!).tokens()

    toRpnVisitor.visitAll(tokens)
    printVisitor.visitAll(toRpnVisitor.rpn())
}