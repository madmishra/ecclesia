package com.eus.cb.complex;

public class ClientTwo implements Callback {

	private WindowServer server;
	public ClientTwo(WindowServer ws) {
		System.out.println("Creating Client Two");
		server =  ws;
		server.registerCallback(this);
	}

	@Override
	public void performCallback(String str) {
		System.out.println("ClientTwo"+str);

	}
}
