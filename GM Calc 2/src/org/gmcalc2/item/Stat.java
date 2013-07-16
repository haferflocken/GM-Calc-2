package org.gmcalc2.item;

import java.util.Arrays;

public class Stat {

	//The different parts of a stat.
	private String[] strings;
	private Range range;
	private int number;
	
	//Constructors.
	public Stat(String[] strings, Range range, int number) {
		this.strings = strings;
		this.range = range;
		this.number = number;
	}
	
	public Stat() {
		this(null, null, 0);
	}
	
	public Stat(Object[] values) {
		this();
		
		//Count the number of strings in the array.
		int numStrings = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof String)
				numStrings++;
		}
		
		//Look through the array and assign the values appropriately.
		if (numStrings > 0)
			strings = new String[numStrings];
		for (int q = 0, i = 0; i < values.length; i++) {
			if (values[i] instanceof String) {
				strings[q] = (String)values[i];
				q++;
			}
			else if (values[i] instanceof Object[]) {
				Object[] rawRange = (Object[])values[i];
				if (rawRange.length == 2 && rawRange[0] instanceof Integer && rawRange[1] instanceof Integer) {
					range = new Range((Integer)rawRange[0], (Integer)rawRange[1]);
				}
			}
			else if (values[i] instanceof Integer) {
				number = (Integer)values[i];
			}
		}
	}
	
	//Return a copy of this stat.
	public Stat copy() {
		Stat out = new Stat();
		if (strings != null)
			out.strings = Arrays.copyOf(strings, strings.length);
		if (range != null)
			out.range = new Range(range.getMin(), range.getMax());
		out.number = number;
		return out;
	}
	
	//Take a stat and merge its values into this, adding ranges and numbers.
	public void merge(Stat other) {
		//Merge the strings.
		if (other.strings != null) {
			if (strings == null) {
				strings = Arrays.copyOf(other.strings, other.strings.length);
			}
			else {
				//Count the number of strings in the incoming array that we don't already have.
				int numNew = other.strings.length; //Assume we are adding nothing but new strings.
				for (int q, i = 0; i < other.strings.length; i++) {
					//Loop through strings to see if we have this or not. If we do, get rid of the assumption.
					for (q = 0; q < strings.length; q++) {
						if (strings[q].equals(other.strings[i])) {
							numNew--;
							break;
						}
					}
				}
					
				//Merge the new strings into the array.
				String[] newStrings = Arrays.copyOf(strings, strings.length + numNew);
				for (int n = strings.length, i = 0; i < other.strings.length; i++) {
					//See if the string is unique.
					boolean newString = true;
					for (int q = 0; q < strings.length; q++) {
						if (strings[q].equals(other.strings[i])) {
							newString = false;
							break;
						}
					}
					if (newString)
						newStrings[n++] = other.strings[i];
				}
				strings = newStrings;
			}
		}
		
		//Add the ranges.
		if (other.range != null) {
			if (range == null)
				range = new Range(other.range.getMin(), other.range.getMax());
			else
				range.add(other.range);
		}
		
		//Add the numbers.
		number += other.number;
	}
	
	//Return an array of strings that represents the different parts of this stat.
	public String[] toDisplayStrings() {
		//Create the output array.
		String[] out = new String[((strings == null)? 0 : strings.length) + ((range != null || number != 0)? 1 : 0)];
		
		//Add in the strings if we have any.
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				out[i] = strings[i];
			}
		}
		
		//The last string in the output is the Range + " +/- " + number.
		if (range != null || number != 0) {
			if (range == null)
				out[out.length - 1] = "" + number;
			else if (number == 0)
				out[out.length - 1] = range.toString();
			else
				out[out.length - 1] = range.toString() + ((number < 0)? " - " + (number * -1) : " + " + number);
		}
		
		//Return that shit.
		return out;
	}
	
	public String toString() {
		String[] dispStrings = toDisplayStrings();
		if (dispStrings.length == 1)
			return dispStrings[0];
		else
			return  Arrays.toString(dispStrings);
	}
}
