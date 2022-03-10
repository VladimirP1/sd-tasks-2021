package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.connection.ConnectionProvider;
import ru.akirakozov.sd.refactoring.connection.ConnectionProviderImpl;
import ru.akirakozov.sd.refactoring.dao.DatabaseProductsDao;
import ru.akirakozov.sd.refactoring.dao.ProductsDao;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ConnectionProvider provider = new ConnectionProviderImpl();

        ProductsDao dao = new DatabaseProductsDao(provider);

        dao.createTable();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(dao)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(dao)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(dao)),"/query");

        server.start();
        server.join();
    }
}
