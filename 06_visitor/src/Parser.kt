class ParserVisitor : TokenVisitor() {
    private val stack = mutableListOf<Token>()
    private val output = mutableListOf<Token>()

    fun rpn(): List<Token> {
        return output
    }

    override fun visit(token: NumberToken) {
        output.add(token)
    }

    override fun visit(token: Operation) {
        while (stack.isNotEmpty()) {
            when (val top = stack.last()) {
                is Operation -> {
                    val top_pv = PriorityVisitor()
                    val tok_pv = PriorityVisitor()
                    top.accept(top_pv)
                    token.accept(tok_pv)
                    if (tok_pv.priority!! <= top_pv.priority!!) {
                        output.add(top)
                        stack.removeLast()
                    } else {
                        break
                    }
                }
                else -> break
            }
        }
        stack.add(token)
    }

    override fun visit(token: OpeningBrace) {
        stack.add(token)
    }

    override fun visit(token: ClosingBrace) {
        while (stack.isNotEmpty()) {
            when (val top = stack.last()) {
                is OpeningBrace -> break
                is Operation -> {
                    output.add(top)
                    stack.removeLast()
                }
                else -> throw RuntimeException()
            }
        }
    }

    override fun visitAll(tokens: List<Token>) {
        visit(OpeningBrace())
        super.visitAll(tokens)
        visit(ClosingBrace())
    }

    private class PriorityVisitor(var priority: Int? = null) : TokenVisitor() {
        override fun visit(token: Plus) {
            priority = 0
        }

        override fun visit(token: Minus) {
            priority = 0
        }

        override fun visit(token: Mult) {
            priority = 1
        }

        override fun visit(token: Div) {
            priority = 1
        }

    }
}