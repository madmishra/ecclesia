package com.eus.ds;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.eus.ds.PlayerFactory.player;

public class CardTable {
	static int numplayers;

	public static void main(String[] args) {
		Deck d = new Deck(8);
		// d.cards.addAll(d.cards);
		System.out.println(d.cards.size());
		shuffle(d.cards);
		try {
			System.out.println("No of players");
			numplayers = Character.getNumericValue(System.in.read());
			initializePlayers(numplayers);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deal(d.cards);
		for (int i = 0; i < numplayers; i++) {
			String string = args[i];
			
		}
	}

	private static void initializePlayers(int numPlayer) {
		getPFInstance().createPlayers(numPlayer);

	}

	static PlayerFactory pf = null;

	private static PlayerFactory getPFInstance() {
		// TODO Auto-generated method stub
		if (pf == null) {
			pf = new PlayerFactory();
		}
		return pf;
	}

	private static void deal(List<Card> cards) {
		cards.forEach((item) -> {
			int cardindex = cards.indexOf(item);
			player p = pf.map.get(cardindex % numplayers);
			if (cardindex < numplayers) {
				p.firstCard=cards.remove(cardindex);
			} else {
				p.secondCard=cards.remove(cardindex);
			}
		});
	}

	private static void shuffle(List<Card> cards) {
		Collections.shuffle(cards);

	}

}
