import java.io.IOException;
import java.sql.*;

public class MoneyStorage {
     String url = "jdbc:mysql:localhost:3306/atm";
     String name = "root";
     String password = "12345";
    Connection connection = null;
    private int fiveBill;
    private int tenBill;
    private int twentyBill;

    public void setFiveBill(int fiveBill) {
        this.fiveBill = fiveBill;
    }

    public void setTenBill(int tenBill) {
        this.tenBill = tenBill;
    }

    public void setTwentyBill(int twentyBill) {
        this.twentyBill = twentyBill;
    }


    public int getFiveBill() {
        return fiveBill;
    }

    public int getTenBill() {
        return tenBill;
    }

    public int getTwentyBill() {
        return twentyBill;
    }


    public int getBalance() {
        return (fiveBill * 5) + (tenBill * 10) + (twentyBill * 20);
    }

    public boolean giveMoney(int needMoney) throws IOException, SQLException {

        int countOfTwentyBill = needMoney / 20;
        int moneyWithoutTwentyBills = needMoney - (countOfTwentyBill * 20);
        int countOfTenBill = moneyWithoutTwentyBills / 10;
        int moneyWithoutTwentyAndTenBills = moneyWithoutTwentyBills - (countOfTenBill * 10);
        int countOfFiveBill = moneyWithoutTwentyAndTenBills / 5;
        int diff = 0;

        if (needMoney > getBalance()) {
            System.out.println("Недостаточно средств в терминале");
            return false;
        }
        if (needMoney % 5 != 0) {
            System.out.println("Отсутствуют необходимые купюры");
            return false;
        }
        if (this.twentyBill >= countOfTwentyBill) {
            this.twentyBill -= countOfTwentyBill;
        }
        else {
            diff = countOfTwentyBill - this.twentyBill;
            countOfTenBill += 2 * diff;
        }
        if (this.tenBill >= countOfTenBill) {
            this.tenBill -= countOfTenBill;
        } else {
            diff = countOfTenBill - this.tenBill;
            countOfFiveBill += 2 * diff;
        }
        if (this.fiveBill >= countOfFiveBill) {
            this.fiveBill -= countOfFiveBill;
        }
        else {
            System.out.println("Отсутствуют необходимые купюры");
            return false;
        }
        saveMoneyStorage();
        return true;
    }
    public void updateMoneyStorage() throws IOException, SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, name, password);
            Statement start = connection.createStatement();

            ResultSet result = start.executeQuery("SELECT * FROM atmmoney");
            this.fiveBill = result.getInt("5mon");
            this.tenBill = result.getInt("10mon");
            this.twentyBill = result.getInt("20mon");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public void saveMoneyStorage() throws IOException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, name, password);
            Statement start = connection.createStatement();
            String update = String.format("INSERT INTO atmmoney (20mon,10mon,5mon) VALUES (%s,%s,%s)", getTwentyBill(), getTenBill(), getFiveBill());
            start.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
