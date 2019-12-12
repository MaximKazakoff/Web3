package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        Long money = Long.parseLong(req.getParameter("money"));
        BankClientService bankClientService = new BankClientService();

        if (name == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            /*
             4. При выполнении операции добавления пользователя должна быть возвращена страница resultPage
             с сообщением "Add client successful" или "Client not add".
             7. Сервлеты не должны выбрасывать исключения, если перевод средств или регистрация не удались.
             */
            if (bankClientService.addClient(new BankClient(name, password, money))) {
                resp.getWriter().println("Add client successful");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.getWriter().println("Client not add");
            }
        }
    }
}
