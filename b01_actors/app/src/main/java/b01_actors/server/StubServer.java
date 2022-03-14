package b01_actors.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.LoggerFactory;

import b01_actors.pojo.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class StubSearchServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String engine = req.getParameter("engine");
        if (engine.equals("slow")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (engine.equals("bad")) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().println("### 403 ###");
            return;
        }
        Result[] results = gson.fromJson(getResource("/test_resp.txt"), Result[].class);
        results = Arrays.asList(results).stream().map((it) -> {
            return new Result(it.title + " - " + engine, it.url);
        }).collect(Collectors.toList()).toArray(new Result[0]);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(gson.toJson(results));
        LoggerFactory.getLogger(StubSearchServlet.class).info("responding");
    }

    private String getResource(String name) {
        try (InputStream in = getClass().getResourceAsStream(name)) {
            try (Scanner s = new Scanner(in).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        } catch (IOException e) {
            return "";
        }
    }
}

public class StubServer {
    public interface ThrowingRunnable {
        public void run() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        StubServer self = new StubServer();
        Server server = self.setupServer();
        server.start();
        server.join();
    }

    public void withStubServer(ThrowingRunnable r) throws Exception {
        Server server = setupServer();
        try {
            server.start();
            r.run();
        } finally {
            server.stop();
        }
    }

    private Server setupServer() {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8081);
        server.addConnector(connector);

        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new StubSearchServlet()), "/search");

        server.setHandler(handler);

        return server;
    }
}
