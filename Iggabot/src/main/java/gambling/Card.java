package gambling;

public class Card{
	int card;
	int suit;
	String[] cards = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
	String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
	/* card = 1-13 Ace-King
	 * suit = 1-4 Hearts, Diamonds, Clubs, Spades
	 */
	public Card(int card, int suit) {
		this.card = card;
		this.suit = suit;
	}
	public String getCardName(){
		return cards[card] + " of " + suits[suit];
	}
}
