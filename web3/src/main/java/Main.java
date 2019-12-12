
import exception.DBException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import service.BankClientService;
import model.BankClient;
import servlet.ApiServlet;
import servlet.MoneyTransactionServlet;
import servlet.RegistrationServlet;
import servlet.ResultServlet;

public class Main {
    public static void main(String[] args) throws Exception{
        ApiServlet apiServlet = new ApiServlet();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(apiServlet), "/api/*"); // http://localhost:8080/api
        context.addServlet(new ServletHolder(new MoneyTransactionServlet()), "/transaction"); // http://localhost:8080/transaction
        context.addServlet(new ServletHolder(new RegistrationServlet()), "/registration"); // http://localhost:8080/registration
        context.addServlet(new ServletHolder(new ResultServlet()), "/result"); // http://localhost:8080/result

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
