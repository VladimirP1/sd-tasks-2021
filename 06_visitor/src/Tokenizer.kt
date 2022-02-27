class Tokenizer(s: String) {
    private val tokens = mutableListOf<Token>()
    private var state = StartState() as State

    init {
        s.forEach { state.char(it) }
        state.eof()
    }

    fun tokens(): List<Token> {
        return tokens
    }

    private abstract inner class State {
        abstract fun char(c: Char)
        abstract fun eof()
    }

    private inner class StartState : State() {
        override fun char(c: Char) {
            when (c) {
                in '0'..'9' -> {
                    state = NumberState()
                    state.char(c)
                }
                in listOf('+', '-', '*', '/') ->
                    tokens.add(makeOpToken(c))
                in listOf('(', ')') -> tokens.add(makeParToken(c))
                else -> throw RuntimeException()
            }
        }

        override fun eof() {}

        private fun makeOpToken(op: Char): Token {
            return (
                    when (op) {
                        '+' -> Plus()
                        '-' -> Minus()
                        '*' -> Mult()
                        '/' -> Div()
                        else -> throw RuntimeException()
                    })

        }

        private fun makeParToken(p: Char): Token {
            return (
                    when (p) {
                        '(' -> OpeningBrace()
                        ')' -> ClosingBrace()
                        else -> throw RuntimeException()
                    })
        }
    }

    private inner class NumberState : State() {
        var number = 0

        override fun char(c: Char) {
            when (c) {
                in '0'..'9' ->
                    number = 10 * number + (c - '0')
                else -> {
                    tokens.add(NumberToken(number))
                    state = StartState()
                    state.char(c)
                }
            }
        }

        override fun eof() {
            state = EndState()
            tokens.add(NumberToken(number))
        }
    }

    private inner class EndState : State() {
        override fun char(c: Char) {
            throw RuntimeException()
        }

        override fun eof() {}
    }
}