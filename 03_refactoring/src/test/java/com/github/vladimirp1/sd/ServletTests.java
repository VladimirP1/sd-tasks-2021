package com.github.vladimirp1.sd;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import ru.akirakozov.sd.refactoring.connection.ConnectionProvider;
import ru.akirakozov.sd.refactoring.dao.DatabaseProductsDao;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

interface ThrowingCallable<T> {
    void call(T t) throws Exception;
}

class TestHelper {
    public String withPrintWriter(ThrowingCallable<PrintWriter> c) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        c.call(printWriter);
        return stringWriter.toString();
    }
};

class TestConnectionProvider implements ConnectionProvider, AutoCloseable {
    private final Connection conn;

    TestConnectionProvider(Connection conn) {
        this.conn = spy(conn);
        try {
            // We need to do this because the sqlite in-memory implementation drops all data on close()
            doNothing().when(this.conn).close();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return conn;
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
};

public class ServletTests {
    @Test
    void SmokeTest() throws Exception {
        try (TestConnectionProvider provider = new TestConnectionProvider(DriverManager.getConnection("jdbc:sqlite::memory:"))) {
            DatabaseProductsDao dao = new DatabaseProductsDao(provider);
            dao.createTable();

            TestHelper hlp = new TestHelper();

            String s = hlp.withPrintWriter((it) -> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                GetProductsServlet sr = new GetProductsServlet(dao);
                sr.service(req, resp);
            });
            assertEquals("", Jsoup.parse(s).body().text());
        }
    }

    @Test
    void AddProductTest() throws Exception {
        try (TestConnectionProvider provider = new TestConnectionProvider(DriverManager.getConnection("jdbc:sqlite::memory:"))) {
            DatabaseProductsDao dao = new DatabaseProductsDao(provider);
            dao.createTable();

            TestHelper hlp = new TestHelper();
            String s_add = hlp.withPrintWriter((it) -> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("name")).thenReturn("iphone");
                when(req.getParameter("price")).thenReturn("1000000");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                AddProductServlet sr = new AddProductServlet(dao);
                sr.service(req, resp);
            });
            assertEquals("OK", Jsoup.parse(s_add).body().text());

            String s_get = hlp.withPrintWriter((it) -> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                GetProductsServlet sr = new GetProductsServlet(dao);
                sr.service(req, resp);
            });
            assertEquals("iphone 1000000", Jsoup.parse(s_get).body().text());
        }
    }

    @Test
    void QueryTest() throws Exception {
        try (TestConnectionProvider provider = new TestConnectionProvider(DriverManager.getConnection("jdbc:sqlite::memory:"))) {
            DatabaseProductsDao dao = new DatabaseProductsDao(provider);
            dao.createTable();

            TestHelper hlp = new TestHelper();
            String s_add = hlp.withPrintWriter((it) -> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("name")).thenReturn("iphone");
                when(req.getParameter("price")).thenReturn("1000000");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                AddProductServlet sr = new AddProductServlet(dao);
                sr.service(req, resp);
            });
            assertEquals("OK", Jsoup.parse(s_add).body().text());

            String s_get = hlp.withPrintWriter((it) -> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("command")).thenReturn("count");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                QueryServlet sr = new QueryServlet(dao);
                sr.service(req, resp);
            });
            assertEquals("Number of products: 1", Jsoup.parse(s_get).body().text());
        }
    }
}
