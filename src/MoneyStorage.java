import java.io.*;

public class MoneyStorage {

    private int fiveBill;

    public void setFiveBill(int fiveBill) {
        this.fiveBill = fiveBill;
    }

    public void setTenBill(int tenBill) {
        this.tenBill = tenBill;
    }

    public void setTwentyBill(int twentyBill) {
        this.twentyBill = twentyBill;
    }

    private int tenBill;
    private int twentyBill;

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

    public boolean giveMoney(int needMoney) throws IOException {
        if (needMoney > getBalance()) {
            System.out.println("Не достаточно средств в терминале");
            return false;
        }
        if (needMoney % 5 != 0) {
            System.out.println("Отсутствуют необходимые купюры");
            return false;
        }
        int countOfTwentyBill = needMoney / 20;
        int moneyWithoutTwentyBills = needMoney - (countOfTwentyBill * 20);
        int countOfTenBill = moneyWithoutTwentyBills / 10;
        int moneyWithoutTwentyAndTenBills = moneyWithoutTwentyBills - (countOfTenBill * 10);
        int countOfFiveBill = moneyWithoutTwentyAndTenBills / 5;
        int diff = 0;


        if (this.twentyBill >= countOfTwentyBill) {
            this.twentyBill -= countOfTwentyBill;
        } else {
            diff = countOfTwentyBill - this.twentyBill;
            countOfTenBill += 2 * diff;
        }
        if (this.tenBill >= countOfTenBill) {
            this.tenBill -= countOfTenBill;
        } else {
            diff = countOfTenBill - this.tenBill;
            countOfFiveBill += 2 * diff;
        }
        this.fiveBill -= countOfFiveBill;

        saveMoneyStorage();
        return true;
    }

    public void updateMoneyStorage() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("\\moneyStorage.csv"));
        String line = reader.readLine();
        String[] values = line.split(";");
        this.fiveBill = Integer.parseInt(values[0]);
        this.tenBill = Integer.parseInt(values[1]);
        this.twentyBill = Integer.parseInt(values[2]);
    }

    public void saveMoneyStorage() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("\\moneyStorage.csv", false));
        StringBuilder forWrite = new StringBuilder();
        forWrite.append(this.getFiveBill()).append(";").append(this.getTenBill()).append(";").append(this.getTwentyBill());
        writer.write(forWrite.toString());
        writer.flush();
        writer.close();
    }
}
