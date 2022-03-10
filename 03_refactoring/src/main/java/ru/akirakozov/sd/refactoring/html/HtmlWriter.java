package ru.akirakozov.sd.refactoring.html;

import ru.akirakozov.sd.refactoring.model.Product;

import java.io.PrintWriter;

public class HtmlWriter implements AutoCloseable {
    private PrintWriter printWriter;

    public HtmlWriter(PrintWriter printWriter) {
        printWriter.println("<html><body>");
        this.printWriter = printWriter;
    }

    public void addH1(String h1) {
        printWriter.println("<h1>" + h1 + "</h1>");
    }

    public void addProduct(Product p) {
        printWriter.println(p.name + "\t" + p.price + "</br>");
    }

    public void addRawText(String p) {
        printWriter.println(p);
    }

    @Override
    public void close() throws Exception {
        printWriter.println("</body></html>");
    }
}
