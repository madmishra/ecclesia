package com.ecc.cb.complex;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ButtonHandler implements ActionListener {
	private int value;
	private WindowServer ws;

	public ButtonHandler(WindowServer windowServer, int i) {
		value = i;
		ws=windowServer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (value > 100) {
			System.out.println("Button 123 pressed.");
			ws.notifyClients("Button 123");
		} else {
			System.out.println("Button " + value + " pressed.");
			((Callback) ws.clientObjs.get(value - 1)).performCallback("says Hello");
		}
	}
}
