package com.github.vladimirp1.sd;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.akirakozov.sd.refactoring.Main;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

interface ThrowingRunnable {
    void run() throws Exception;
}

interface ThrowingCallable<T> {
    void call(T t) throws Exception;
}

class TestHelper {
    public void test(ThrowingRunnable f) throws Exception {
        Connection c = Mockito.spy(DriverManager.getConnection("jdbc:sqlite::memory:"));
        // Create database
        doThrow(new RuntimeException()).when(c).close();
        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection("jdbc:sqlite:test.db")).thenReturn(c);
            Main.main(null);
        } catch (RuntimeException ignored) {
        } catch (Exception e) {
            c.close();
            throw e;
        }

        // Test the actual servlet
        doNothing().when(c).close();
        try (MockedStatic<DriverManager> dm = mockStatic(DriverManager.class)) {
            dm.when(() -> DriverManager.getConnection("jdbc:sqlite:test.db")).thenReturn(c);

            f.run();
        } finally {
            c.close();
        }
    }

    public String withPrintWriter(ThrowingCallable<PrintWriter> c) throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        c.call(printWriter);
        return stringWriter.toString();
    }
};

public class ServletTests {
    @Test
    void SmokeTest() throws Exception {

        TestHelper hlp = new TestHelper();
        hlp.test(() -> {
            String s = hlp.withPrintWriter((it)-> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                GetProductsServlet sr = new GetProductsServlet();
                sr.service(req, resp);
            });
            assertEquals("", Jsoup.parse(s).body().text());
        });
    }

    @Test
    void AddProductTest() throws Exception {
        TestHelper hlp = new TestHelper();
        hlp.test(() -> {
            String s_add = hlp.withPrintWriter((it)-> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("name")).thenReturn("iphone");
                when(req.getParameter("price")).thenReturn("1000000");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                AddProductServlet sr = new AddProductServlet();
                sr.service(req, resp);
            });
            assertEquals("OK", Jsoup.parse(s_add).body().text());

            String s_get = hlp.withPrintWriter((it)-> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                GetProductsServlet sr = new GetProductsServlet();
                sr.service(req, resp);
            });
            assertEquals("iphone 1000000", Jsoup.parse(s_get).body().text());
        });
    }

    @Test
    void QueryTest() throws Exception {
        TestHelper hlp = new TestHelper();
        hlp.test(() -> {
            String s_add = hlp.withPrintWriter((it)-> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("name")).thenReturn("iphone");
                when(req.getParameter("price")).thenReturn("1000000");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                AddProductServlet sr = new AddProductServlet();
                sr.service(req, resp);
            });
            assertEquals("OK", Jsoup.parse(s_add).body().text());

            String s_get = hlp.withPrintWriter((it)-> {
                HttpServletRequest req = mock(HttpServletRequest.class);
                when(req.getMethod()).thenReturn("GET");
                when(req.getParameter("command")).thenReturn("count");
                HttpServletResponse resp = mock(HttpServletResponse.class);
                when(resp.getWriter()).thenReturn(it);
                QueryServlet sr = new QueryServlet();
                sr.service(req, resp);
            });
            assertEquals("Number of products: 1", Jsoup.parse(s_get).body().text());
        });
    }
}
