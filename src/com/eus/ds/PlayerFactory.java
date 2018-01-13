package com.eus.ds;

import java.util.ArrayList;
import java.util.List;




public class PlayerFactory {
	List< player> map = null;
	public void createPlayers(int numPlayer) {
		map = new ArrayList<>();
		for (int i = 0; i < numPlayer; i++) {
			map.add(new player());
		}
	}
	class player{
		Card firstCard=null;
		public Card getFirstCard() {
			return firstCard;
		}
		public void setFirstCard(Card firstCard) {
			this.firstCard = firstCard;
		}
		public Card getSecondCard() {
			return secondCard;
		}
		public void setSecondCard(Card secondCard) {
			this.secondCard = secondCard;
		}
		Card secondCard = null;
		void hit(){
			
		}
		void pack(){
			
		}
		void split(){
			
		}
		void doublegame(){
			
		}
	}

}
