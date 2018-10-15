package com.ecc.cb.simple;

public class EventCreator {
	IListener listener;
	public EventCreator(IListener listener) {
		this.listener=listener;
	}
	void event(){
		listener.listen();
	}
	
}
