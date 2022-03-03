class CalcVisitor() : TokenVisitor() {
    private val stack = mutableListOf<Int>()

    fun result() : Int {
        if (stack.size == 0) return 0
        if (stack.size != 1) {
            throw RuntimeException("Stack not empty on exit")
        }
        return stack.last()
    }

    override fun visit(token: Plus) {
        val a = stack.last()
        stack.removeLast()
        val b = stack.last()
        stack.removeLast()
        stack.add(a + b)
    }

    override fun visit(token: Minus) {
        val a = stack.last()
        stack.removeLast()
        val b = stack.last()
        stack.removeLast()
        stack.add(b - a)
    }

    override fun visit(token: Mult) {
        val a = stack.last()
        stack.removeLast()
        val b = stack.last()
        stack.removeLast()
        stack.add(a * b)
    }

    override fun visit(token: Div) {
        val a = stack.last()
        stack.removeLast()
        val b = stack.last()
        stack.removeLast()
        if (a == 0) throw RuntimeException("Division by zero")
        stack.add(b / a)
    }

    override fun visit(token: NumberToken) {
        stack.add(token.n)
    }

    override fun visitAll(tokens: List<Token>) {
        super.visitAll(tokens)
    }
}