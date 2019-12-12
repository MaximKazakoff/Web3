package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) {
        return getBankClientDAO().getClientById(id);
    }

    public BankClient getClientByName(String name) {
        return getBankClientDAO().getClientByName(name);
    }

    public List<BankClient> getAllClient() {
        return getBankClientDAO().getAllBankClient();
    }

    public boolean deleteClient(String name) {
        return getBankClientDAO().deleteClientByName(name);
    }

    public boolean addClient(BankClient client) {
        /*
        8. В базе данных не должны храниться 2 пользователя с одинаковым именем.
         */
        BankClientDAO dao = getBankClientDAO();
        if (!dao.isClientNameExist(client.getName())) {
            dao.addClient(client);
            return true;
        } else {
            return false;
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        /*
         1. isClientHasSum() - Проверка наличия средств у отправителя  обязательна при совершении транзакции,
            validateUser() - так же обязательна валидация логина и пароля в методе validateUser().
         */
        BankClientDAO dao = getBankClientDAO();
        if (dao.isClientHasSum(sender.getName(), value) & dao.validateClient(sender.getName(), sender.getPassword())) {
            // списание средств
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), -value);
            // начисление средств
            dao.updateClientsMoney(name, dao.getClientByName(name).getPassword(), value);
            return true;
        } else {
            return false;
        }
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            DriverManager.registerDriver(driver);

            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db_example?serverTimezone=Europe/Moscow",
                    "root",
                    "root1234");
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
