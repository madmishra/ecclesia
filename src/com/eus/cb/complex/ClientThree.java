package com.eus.cb.complex;

public class ClientThree implements Callback {
	private WindowServer server;
	public ClientThree(WindowServer ws) {
		System.out.println("Creating Client Three");
		server =  ws;
		server.registerCallback(this);
	}

	@Override
	public void performCallback(String str) {
		System.out.println("ClientThree"+str);

	}

}
