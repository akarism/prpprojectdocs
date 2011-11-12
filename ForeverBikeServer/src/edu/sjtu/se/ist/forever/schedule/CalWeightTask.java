package edu.sjtu.se.ist.forever.schedule;

public class CalWeightTask {

	private int count = 1;
	public void execute(){
		System.out.println("Execute " + count++ +" times.");
	}
	
	public void doIt(){
		System.out.println("We did it.");
	}
}
