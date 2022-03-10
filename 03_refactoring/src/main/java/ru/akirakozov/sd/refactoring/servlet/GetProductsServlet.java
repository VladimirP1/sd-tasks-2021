package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.connection.ConnectionProvider;
import ru.akirakozov.sd.refactoring.dao.ProductsDao;
import ru.akirakozov.sd.refactoring.html.HtmlWriter;
import ru.akirakozov.sd.refactoring.model.Product;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {
    private final ProductsDao dao;

    public GetProductsServlet(ProductsDao dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (HtmlWriter writer = new HtmlWriter(response.getWriter())) {
            List<Product> products = dao.get();
            for (Product p : products) {
                writer.addProduct(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
