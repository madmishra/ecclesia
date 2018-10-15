package com.ecc.ds;

import java.util.Stack;

public class Deck {
	Stack<Card> cards = null;
	enum faceValue{ace,two,three,four,five,six,seven,eight,nine,ten,jack,queen,king}
	enum suit{heart,club,diamond,spade}
	public Deck(int i) {
		cards=new Stack<Card>();
		for(faceValue f:faceValue.values()){
			for(suit s:suit.values()){
				for(int j=0;j<i;j++){
					Card c = new Card(s, f);
					cards.add(c);
				}
				
				//System.out.println("Card Suit and Value is "+s+"-"+f);
			}
		}
	}
}
