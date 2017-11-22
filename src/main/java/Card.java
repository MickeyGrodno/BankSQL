public class Card {
    private int cardId;
    private int pinСode;
    private int balance;
    private byte block;



    public Card(int cardId, int pinСode, int balance, byte block) {
        this.cardId = cardId;
        this.pinСode = pinСode;
        this.balance = balance;
        this.block = block;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setBlock(byte block) { this.block = block; }

    public int getCardId() {
        return this.cardId;
    }

    public int getPinСode() {
        return this.pinСode;
    }

    public int getBalance() {
        return this.balance;
    }

    public byte getBlock() { return block; }

}
