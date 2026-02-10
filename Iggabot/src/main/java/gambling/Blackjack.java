package gambling;

import java.math.BigInteger;
import java.util.ArrayList;

import logic.*;


public class Blackjack{
	
	public ArrayList<Cards> cards = Cards.shuffle();
	public ArrayList<Cards> dealer = new ArrayList<>(); 
	public ArrayList<Cards> player = new ArrayList<>(); 
	public Iggacoin $;
	public BigInteger bet;
	//Starts new Blackjack game
	public void BlackJack(Iggacoin $) {
		this.$ = $;
	}
	//Money Logic
	public void raise(BigInteger num) {
		
	}
	//End Logic
	public void win() {
		//Bet*2
		
	}
	public void lose() {
		//just loses the bet, no pot
	}
	//Game Logic
	public void hit() {
		
	}
	public void stand() {
		
	}
	
}
