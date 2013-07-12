//Represents a range of values, eg. [1, 6] would be the same as a d6. Useful for representing dice values and adding them together. Obviously, a range
//does not reflect the bell curve of dice values, but that is a known tradeoff for how much easier it is to use a range instead of dice.

package org.gmcalc2.item;

public class Range {

	private int min, max;
	private String displayString;
	
	public Range(int min, int max) {
		this.min = min;
		this.max = max;
		updateDisplayString();
	}
	
	public void add(Range other) {
		min += other.min;
		max += other.max;
		updateDisplayString();
	}
	
	public void subtract(Range other) {
		min -= other.min;
		max -= other.max;
		updateDisplayString();
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public void updateDisplayString() {
		displayString = "[" + min + ", " + max + "]";
	}
	
	public String toString() {
		return displayString;
	}
}
