package com.ecc.cb.complex;

public class ClientOne implements Callback {
	private WindowServer server;
	public ClientOne(WindowServer ws) {
		System.out.println("Creating Client One");
		server =  ws;
		server.registerCallback(this);
	}

	@Override
	public void performCallback(String str) {
		System.out.println("ClientOne"+str);

	}

}
