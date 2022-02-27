abstract class Token {
    abstract fun accept(visitor: TokenVisitor)
}

class NumberToken(public val n: Int) : Token() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Token)
    }
}

class OpeningBrace() : Token() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Token)
    }
}

class ClosingBrace() : Token() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Token)
    }
}

abstract class Operation() : Token() {
}

class Plus : Operation() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Operation)
        visitor.visit(this as Token)
    }
}

class Minus : Operation() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Operation)
        visitor.visit(this as Token)
    }
}

class Mult : Operation() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this)
        visitor.visit(this as Operation)
        visitor.visit(this as Token)
    }
}

class Div : Operation() {
    override fun accept(visitor: TokenVisitor) {
        visitor.visit(this as Operation)
        visitor.visit(this)
    }
}

public abstract class TokenVisitor {
    open fun visit(token: NumberToken) {}
    open fun visit(token: Operation) {}
    open fun visit(token: Plus) {}
    open fun visit(token: Minus) {}
    open fun visit(token: Mult) {}
    open fun visit(token: Div) {}
    open fun visit(token: OpeningBrace) {}
    open fun visit(token: ClosingBrace) {}
    open fun visit(token: Token) {}

    open fun visitAll(tokens: List<Token>) {
        tokens.forEach { it.accept(this) }
    }
}
