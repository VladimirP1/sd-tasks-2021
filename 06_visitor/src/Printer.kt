import java.io.OutputStream

class PrintVisitor(stream: OutputStream) : TokenVisitor() {
    val writer = stream.bufferedWriter()

    override fun visit(token: Plus) {
        writer.write("+")
        writer.newLine()
    }

    override fun visit(token: Minus) {
        writer.write("-")
        writer.newLine()
    }

    override fun visit(token: Mult) {
        writer.write("*")
        writer.newLine()
    }

    override fun visit(token: Div) {
        writer.write("/")
        writer.newLine()
    }

    override fun visit(token: OpeningBrace) {
        writer.write("(")
        writer.newLine()
    }

    override fun visit(token: ClosingBrace) {
        writer.write(")")
        writer.newLine()
    }

    override fun visit(token: NumberToken) {
        writer.write(token.n.toString())
        writer.newLine()
    }

    override fun visitAll(tokens: List<Token>) {
        super.visitAll(tokens)
        writer.flush()
    }
}