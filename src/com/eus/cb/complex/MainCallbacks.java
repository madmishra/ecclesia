package com.eus.cb.complex;

public class MainCallbacks {

	public static void main(String[] args) {
		WindowServer ws = new WindowServer();
		ws.setSize(300, 200);
		ws.setVisible(true);
		ClientOne client1 = new ClientOne(ws);
		ClientTwo client2 = new ClientTwo(ws);
		ClientThree client3 = new ClientThree(ws);
	}
}
