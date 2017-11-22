import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class ATMcode {

     String url = "jdbc:mysql:localhost:3306/atm";
     String name = "root";
     String password = "12345";
    Connection connection = null;
    Scanner sc = new Scanner(System.in);

    private HashMap<Integer, Card> cardsLoad () throws IOException, SQLException {
        HashMap<Integer, Card> cardsMap = new HashMap<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, name, password);
            Statement start = connection.createStatement();

            ResultSet result = start.executeQuery("SELECT * FROM cards");

            while (result.next()) {
                int cardID = result.getInt("ID");
                int pinCode = result.getInt("PIN");
                int balance = result.getInt("MONEY");
                byte block = result.getByte("BLOCKED");
                Card card = new Card(cardID, pinCode, balance, block);
                cardsMap.put(cardID, card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return cardsMap;
    }

    private void cardsSave(HashMap<Integer, Card> cardMap) throws IOException, SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, name, password);
            Statement start = connection.createStatement();

            for (HashMap.Entry<Integer, Card> pair : cardMap.entrySet()) {
                int cardId = pair.getValue().getCardId();
                int balance = pair.getValue().getBalance();
                byte block = pair.getValue().getBlock();
                String update = String.format("INSERT INTO cards (MONEY, BLOCKED) VALUES (%s,%s) WHERE ID = %s", balance, block, cardId);
                start.executeUpdate(update);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private Card incertcard(HashMap<Integer, Card> cards) throws IOException, InterruptedException, SQLException {

        int enteryCardID;
        while (true) {
            System.out.println("Вставьте карту.");
            enteryCardID = sc.nextInt();
            if (enteryCardID == 7777) {
                atmMenuAdmin();
            } else {
                if (cards.containsKey(enteryCardID)) {
                    if (cards.get(enteryCardID).getBlock() == 0) {
                        Card currentCard = cards.get(enteryCardID);
                        if (pinCheck(currentCard)) {
                            return currentCard;
                        }
                    } else {
                        System.out.println("Ваша карта заблокирована");
                    }
                } else {
                    System.out.println("Данная карта не поддерживается");
                }
            }
        }
    }


    private boolean pinCheck(Card card) throws IOException, InterruptedException, SQLException {
        for (int i = 3; ; i--) {
            if (i == 0) {
                System.out.println("Введен неверный пинкод 3 раза. Ваша карта заюлокирована");
                card.setBlock((byte) 1);
                HashMap cardS = cardsLoad();
                cardS.put(card.getCardId(), card);
                cardsSave(cardS);
                Thread.sleep(3000);
                return false;
            }

            System.out.println(String.format("Введите PIN-код карты. Осталось попыток %s", i));
            if (sc.nextInt() == card.getPinСode()) {
                return true;
            } else {
                System.out.println("Неверный PIN-код.");
            }
        }
    }

    public void atmMenu() throws InterruptedException, IOException, SQLException {
        while (true) {
            HashMap<Integer, Card> cards = cardsLoad();

            Card checkedCard = incertcard(cards);
            MoneyStorage moneyStorage = new MoneyStorage();
            int cardMoney = checkedCard.getBalance();
            while (true) {
                System.out.println("Выберите необходимую операцию");
                System.out.println("1 - баланс");
                System.out.println("2 - снятие наличных");
                System.out.println("0 - вернуть карту");

                int menuNum = sc.nextInt();

                if (menuNum == 0) {
                    checkedCard.setBalance(cardMoney);
                    cardsSave(cards);
                    System.out.println("Возьмите вашу карту");
                    Thread.sleep(3000);
                    break;
                }
                if (menuNum == 1) {
                    System.out.println(String.format("Баланс вашего счета %s р.", cardMoney));
                    Thread.sleep(3000);
                    continue;

                }
                if (menuNum == 2) {
                    while (true) {
                        if (noteInfo(moneyStorage)) {
                            System.out.println("Введите необходимую сумму");
                            int needMoney = sc.nextInt();
                            if (cardMoney >= needMoney) {
                                boolean isMoneyGiven = moneyStorage.giveMoney(needMoney);
                                if (!isMoneyGiven) {
                                    Thread.sleep(3000);
                                    break;
                                }
                                cardMoney -= needMoney;
                                System.out.println(String.format("Выдана сумма %s р.", needMoney));
                                checkedCard.setBalance(cardMoney);
                                cards.put(checkedCard.getCardId(), checkedCard);
                                Thread.sleep(3000);
                                break;
                            } else {
                                System.out.println("Недостаточно средств на счёте");
                                Thread.sleep(3000);
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void atmMenuAdmin() throws IOException, InterruptedException, SQLException {
        MoneyStorage moneyStorage = new MoneyStorage();
        moneyStorage.updateMoneyStorage();

        while (true) {

            System.out.println("Выберите необходимую операцию:");
            System.out.println("1 - Добавить купюры в банкомат.");
            System.out.println("0 - Вернуть карту");
            int menuNum = sc.nextInt();
            if (menuNum == 1) {
                int a = moneyStorage.getTwentyBill();
                int b = moneyStorage.getTenBill();
                int c = moneyStorage.getFiveBill();

                System.out.println(String.format("В наличии купюры номиналом 20р %s единиц. Добавьте необходимое количесво купюр", a));
                moneyStorage.setTwentyBill(a + sc.nextInt());

                System.out.println(String.format("В наличии купюры номиналом 10р %s единиц. Добавьте необходимое количесво купюр", b));
                moneyStorage.setTenBill(b + sc.nextInt());

                System.out.println(String.format("В наличии купюры номиналом 5р %s единиц. Добавьте необходимое количесво купюр", c));
                moneyStorage.setFiveBill(c + sc.nextInt());

                System.out.println(String.format("В наличии купюры 20р %s шт., 10р %s шт., 5р %s шт., общей суммой %s р.", moneyStorage.getTwentyBill(), moneyStorage.getTenBill(), moneyStorage.getFiveBill(), moneyStorage.getBalance()));
                moneyStorage.saveMoneyStorage();
                Thread.sleep(3000);
            }
            if (menuNum == 0) {
                System.out.println("Заберите карту");
                Thread.sleep(3000);
                break;
            }
        }
    }

    public boolean noteInfo(MoneyStorage moneyStorage) throws IOException, InterruptedException, SQLException {
        moneyStorage.updateMoneyStorage();
        if (moneyStorage.getBalance() == 0) {
            System.out.println("В терминале закончились деньги");
            Thread.sleep(3000);
            return false;
        }
        else {
            System.out.println("В терменали имеются купюры номиналом:");
            if (moneyStorage.getTwentyBill() > 0) {
                System.out.print("20р. ");
            }
            if (moneyStorage.getTenBill() > 0) {
                System.out.print("10р. ");
            }
            if (moneyStorage.getFiveBill() > 0) {
                System.out.print("5р. ");
            }
            System.out.println();
            return true;
        }
    }
}
