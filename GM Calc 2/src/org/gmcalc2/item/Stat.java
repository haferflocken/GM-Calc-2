package org.gmcalc2.item;

import java.util.Arrays;

public class Stat {

	//The different parts of a stat.
	private String[] strings;
	private Range range;
	private int number;
	
	//Constructors.
	public Stat() {
		strings = null;
		range = null;
		number = 0;
	}
	
	public Stat(String[] strings) {
		this();
		this.strings = strings;
	}
	
	public Stat(Range range) {
		this();
		this.range = range;
	}
	
	public Stat(int min, int max) {
		this();
		range = new Range(min, max);
	}
	
	public Stat(int number) {
		this();
		this.number = number;
	}
	
	//Return a copy of this stat.
	public Stat copy() {
		Stat out = new Stat();
		if (out.strings != null)
			out.strings = Arrays.copyOf(strings, strings.length);
		if (out.range != null)
			out.range = new Range(range.getMin(), range.getMax());
		out.number = number;
		return out;
	}
	
	//Take a stat and merge its values into this, adding ranges and numbers.
	public void merge(Stat other) {
		//Merge the strings.
		if (strings == null) {
			if (other.strings != null)
				strings = Arrays.copyOf(other.strings, other.strings.length);
		}
		else {
			int oldLength = strings.length;
			strings = Arrays.copyOf(strings, strings.length + other.strings.length);
			for (int i = 0; i < other.strings.length; i++) {
				strings[i + oldLength] = other.strings[i];
			}
		}
		
		//Add the ranges.
		if (range == null)
			range = new Range(other.range.getMin(), other.range.getMax());
		else
			range.add(other.range);
		
		//Add the numbers.
		number += other.number;
	}
	
	//Return an array of strings that represents the different parts of this stat.
	public String[] toDisplayStrings() {
		//Create the output array.
		String[] out = new String[((strings == null)? 0 : strings.length) + 1];
		
		//Add in the strings if we have any.
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				out[i] = strings[i];
			}
		}
		
		//The last string in the output is the Range + " +/- " + number.
		if (range == null)
			out[out.length - 1] = "" + number;
		else
			out[out.length - 1] = range.toString() + ' ' + ((number < 0)? "- " + (number * -1) : "+ " + number);
		
		//Return that shit.
		return out;
	}
}
