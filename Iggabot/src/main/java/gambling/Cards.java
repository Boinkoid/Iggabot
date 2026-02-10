package gambling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Cards extends ListenerAdapter{
	int card;
	int suit;
	public static final ArrayList<Cards> LIST_OF_CARDS = new ArrayList<>(Arrays.asList(new Cards(1, 1), new Cards(2, 1), new Cards(3, 1), new Cards(4, 1), new Cards(5, 1), new Cards(6, 1), new Cards(7, 1), new Cards(8, 1), new Cards(9, 1), new Cards(10, 1), new Cards(11, 1), new Cards(12, 1), new Cards(13, 1), new Cards(1, 2), new Cards(2, 2), new Cards(3, 2), new Cards(4, 2), new Cards(5, 2), new Cards(6, 2), new Cards(7, 2), new Cards(8, 2), new Cards(9, 2), new Cards(10, 2), new Cards(11, 2), new Cards(12, 2), new Cards(13, 2), new Cards(1, 3), new Cards(2, 3), new Cards(3, 3), new Cards(4, 3), new Cards(5, 3), new Cards(6, 3), new Cards(7, 3), new Cards(8, 3), new Cards(9, 3), new Cards(10, 3), new Cards(11, 3), new Cards(12, 3), new Cards(13, 3), new Cards(1, 4), new Cards(2, 4), new Cards(3, 4), new Cards(4, 4), new Cards(5, 4), new Cards(6, 4), new Cards(7, 4), new Cards(8, 4), new Cards(9, 4), new Cards(10, 4), new Cards(11, 4), new Cards(12, 4), new Cards(13, 4)));
	String[] cards = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
	String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
	/* card = 1-13 Ace-King
	 * suit = 1-4 Hearts, Diamonds, Clubs, Spades
	 */
	public Cards(int card, int suit) {
		this.card = card;
		this.suit = suit;
	}
	public String getCardName(){
		return cards[card] + " of " + suits[suit];
	}
	public static ArrayList<Cards> shuffle() {
		ArrayList<Cards> tmp = new ArrayList<>();
		LIST_OF_CARDS.forEach(e->tmp.add(e));
		for(int i = 0; i<tmp.size()*2; i++) {
			Collections.shuffle(tmp);
		}
		return tmp;
	}
}
