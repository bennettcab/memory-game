package bennett.chad.memorymatching;

public class Card {

    private int cardBackResource;
    private int cardFrontResource;

    public Card(int backImageResource, int frontImageResource) {
        this.cardBackResource = backImageResource;
        this.cardFrontResource = frontImageResource;
    }

    public int getValue() {
        return this.cardFrontResource;
    }

    public int getBack() {
        return this.cardBackResource;
    }
}
