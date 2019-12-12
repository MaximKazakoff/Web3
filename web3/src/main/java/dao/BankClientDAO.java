package dao;

//import com.sun.deploy.util.SessionState;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() {
        System.out.println("Class: BankClientDAO; method: getAllBankClient;");

        List<BankClient> resultList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client")) {
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                resultList.add(
                        new BankClient(
                                result.getLong("id"),
                                result.getString("name"),
                                result.getString("password"),
                                result.getLong("money")
                        ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resultList.forEach(el -> System.out.println(el.toString()));

        return resultList;
    }

    public boolean validateClient(String name, String password) {
        boolean resultBoolean = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ? AND password= ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                resultBoolean = result.getString("name").equals(name) && result.getString("password").equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Class: BankClientDAO; method: validateClient; result: " + resultBoolean + "\n");

        return resultBoolean;
    }

    // передалеть
    public void updateClientsMoney(String name, String password, Long transactValue) {
        /*2. SQL-запрос для транзакции в методе updateClientsMoney()  должен быть защищен от SQL-Injection.
          3. updateClientsMoney() должен вызываться с положительным transactValue, если средства начисляются,
          и с отрицательным, если списываются.
         */
        System.out.println("Class: BankClientDAO; method: updateClientsMoney; Params: " +
                "name: " + name + " ; " +
                "password: " + password + " ; " +
                "transactValue: " + transactValue + "\n");
        try (PreparedStatement preparedStatement = connection.prepareStatement("update bank_client set money = ? where name= ? and password= ?")) {
            preparedStatement.setLong(1, getClientByName(name).getMoney() + transactValue);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BankClient getClientById(long id) {
        System.out.println("Class: BankClientDAO; method: getClientById; Params: " + "id: " + id);

        BankClient clientResult = new BankClient();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where id= ?")) {
            preparedStatement.setLong(1, id);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            clientResult.setId(result.getLong("id"));
            clientResult.setName(result.getString("name"));
            clientResult.setPassword(result.getString("password"));
            clientResult.setMoney(result.getLong("money"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Class: BankClientDAO; method: getClientById; Result bankClient: " + "\n" + clientResult.toString() + "\n");

        return clientResult;
    }

    public boolean deleteClientByName(String name) {
        System.out.println("Class: BankClientDAO; method: deleteClient; Params: " + "name: " + name);

        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from bank_client where name= ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isClientHasSum(String name, Long expectedSum) {
        System.out.println("Class: BankClientDAO; method: isClientHasSum; Params: " + "\n" + "name: " + name + " ; " + "expectedSum: " + expectedSum);

        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?")) {
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            boolean resultBoolean = result.getLong("money") >= expectedSum;
            System.out.println("result: " + result);
            System.out.println("resultBoolean: " + resultBoolean + "\n");
            return resultBoolean;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getClientIdByName(String name) throws SQLException {
        System.out.println("Class: BankClientDAO; method: getClientIdByName; Params: name: " + name);

        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        long id = result.getLong(1);
        result.close();
        stmt.close();
        System.out.println("result: " + result);

        return id;
    }

    public BankClient getClientByName(String name) {
        System.out.println("Class: BankClientDAO; method: getClientByName; Params: name: " + name);

        BankClient clientResult = new BankClient();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?")) {
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            clientResult.setId(result.getLong("id"));
            clientResult.setName(result.getString("name"));
            clientResult.setPassword(result.getString("password"));
            clientResult.setMoney(result.getLong("money"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("BankClient: " + clientResult.toString());

        return clientResult;
    }

    public boolean isClientNameExist(String name) {
        return getAllBankClient()
                .stream()
                .map(BankClient::getName)
                .anyMatch(el -> el.equals(name));
    }

    public void addClient(BankClient client) {
        /*
        8. В базе данных не должны храниться 2 пользователя с одинаковым именем.
         */
        System.out.println("Class: BankClientDAO; method: addClient; Params: client: " + client.toString() + "\n");
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bank_client(name, password, money) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() throws SQLException {
        System.out.println("Class: BankClientDAO; method: createTable; \n");

        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        System.out.println("Class: BankClientDAO; method: dropTable; \n");

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
