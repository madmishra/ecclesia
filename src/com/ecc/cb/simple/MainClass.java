package com.ecc.cb.simple;

public class MainClass {
	public static void main(String[] args) {
		Listener listener =  new Listener();
		//With implementation of interface
		EventCreator ec = new EventCreator(listener);
		ec.event();
		//Without implementation of interface and using lambda
		IListener listener2 = ()->{System.out.println("No Implementation");};
		EventCreator ec2 = new EventCreator(listener2);
		ec2.event();
	}
}
