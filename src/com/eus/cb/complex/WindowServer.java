package com.eus.cb.complex;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

class WindowServer extends JFrame {
	private JButton button1, button2, button3, button123;
	List clientObjs = new ArrayList();

	WindowServer() {
		super("Callback Window");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = getContentPane();
		cp.setLayout(new FlowLayout());
		button1 = new JButton("Button 1");
		button1.addActionListener(new ButtonHandler(this,1));
		button2 = new JButton("Button 2");
		button2.addActionListener(new ButtonHandler(this,2));
		button3 = new JButton("Button 3");
		button3.addActionListener(new ButtonHandler(this,3));
		button123 = new JButton("Button 123");
		button123.addActionListener(new ButtonHandler(this,123));
		cp.add(button1);
		cp.add(button2);
		cp.add(button3);
		cp.add(button123);
	}

	void registerCallback(Callback client) {
		clientObjs.add(client);
	}

	void unregisterCallback(Callback client) {
		clientObjs.remove(client);
	}

	void notifyClients(String str) {
		for (Iterator it = clientObjs.iterator(); it.hasNext();)
			((Callback) (it.next())).performCallback(str);
	}
}