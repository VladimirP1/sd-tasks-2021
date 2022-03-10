package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductsDao;
import ru.akirakozov.sd.refactoring.html.HtmlWriter;
import ru.akirakozov.sd.refactoring.model.Product;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    private final ProductsDao dao;

    public QueryServlet(ProductsDao dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        try (HtmlWriter writer = new HtmlWriter(response.getWriter())) {
            if ("max".equals(command)) {
                writer.addH1("Product with max price: ");
                Product p = dao.getWithMaxPrice();
                if (p != null) {
                   writer.addProduct(p);
                }
            } else if ("min".equals(command)) {
                writer.addH1("Product with min price: ");
                Product p = dao.getWithMinPrice();
                if (p != null) {
                    writer.addProduct(p);
                }
            } else if ("sum".equals(command)) {
                writer.addRawText("Summary price: ");
                writer.addRawText(Long.toString(dao.getSumPrices()));

            } else if ("count".equals(command)) {
                writer.addRawText("Number of products: ");
                writer.addRawText(Integer.toString(dao.count()));
            } else {
                writer.addRawText("Unknown command: " + command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
