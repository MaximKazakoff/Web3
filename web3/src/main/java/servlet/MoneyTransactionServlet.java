package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class MoneyTransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>()));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String senderName = req.getParameter("senderName");
        String senderPass = req.getParameter("senderPass");
        Long count = Long.parseLong(req.getParameter("count"));
        String nameTo = req.getParameter("nameTo");
        BankClientService bankClientService = new BankClientService();

        if (senderName == null || senderPass == null || nameTo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            /*
             5. При выполнении операции транзакции должна быть возвращена страница resultPage с сообщением
             "The transaction was successful" или "transaction rejected".
             7. Сервлеты не должны выбрасывать исключения, если перевод средств или регистрация не удались.
             */
            if (bankClientService.sendMoneyToClient(new BankClient(senderName, senderPass), nameTo, count)) {
                resp.getWriter().println("The transaction was successful");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.getWriter().println("transaction rejected");
            }
        }
    }
}
