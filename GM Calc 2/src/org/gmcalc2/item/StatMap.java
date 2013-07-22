//A map of stats.

package org.gmcalc2.item;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;

import org.haferlib.util.expression.ConstantExpression;
import org.haferlib.util.expression.Expression;
import org.haferlib.util.expression.ExpressionBuilder;
import org.haferlib.util.expression.VariableExpression;

public class StatMap {
	
	// Compares the expressions in stats to figure out what order to evaluate them in.
	public static class StatEvalOrderComparator implements Comparator<Stat> {

		// 1:  o1 > o2
		// 0:  o1 == o2;
		// -1: o1 < o2
		@Override
		public int compare(Stat o1, Stat o2) {
			Expression exp1 = o1.getExpression();
			Expression exp2 = o2.getExpression();
			if (exp1 == null) {
				if (exp2 == null)
					return 0;
				return -1;
			}
			if (exp2 == null)
				return 1;
			if (exp1 instanceof ConstantExpression) {
				if (exp2 instanceof ConstantExpression)
					return 0;
				return -1;
			}
			if (exp2 instanceof ConstantExpression)
				return 1;
			return 0;
		}
		
	}
	
	private TreeMap<String, Stat> stats; //The stats and their names.
	
	// Constructors.
	public StatMap() {
		stats = new TreeMap<>();
	}
	
	public StatMap(Map<?, ?> rawStats, ExpressionBuilder expBuilder) {
		this();
		// Look at the pairs in the map. Those that have a string key and a valid stat value are put into the map of stats.
		for (Map.Entry<?, ?> entry : rawStats.entrySet()) {
			if (entry.getKey() instanceof String && entry.getValue() instanceof Object[]) {
				// Get the key and value for easy reference.
				String key = (String)entry.getKey();
				Object[] val = (Object[])entry.getValue();
				
				// Put the stat in the map of stats.
				stats.put(key, new Stat(val, expBuilder));
			}
		}
	}
	
	// Clear the values from this stat map.
	public void clear() {
		stats.clear();
	}
	
	//Put a stat in this, overriding the old value if there is one.
	public void put(String key, Stat value) {
		stats.put(key, value);
	}
	
	//Put a stat in this, adding the value to the old value if there is one.
	public void addPut(String key, Stat value) {
		Stat oldValue = stats.get(key);
		if (oldValue == null) {
			stats.put(key, value.copy());
		}
		else {
			oldValue.merge(value);
		}
	}
	
	//Add the like values of a StatMap to this one.
	public void addMap(StatMap other) {
		for (Map.Entry<String, Stat> entry : stats.entrySet()) {
			Stat otherValue = other.stats.get(entry.getKey());
			if (otherValue != null) {
				entry.getValue().merge(otherValue);
			}
		}
	}
	
	// Add all the values of another StatMap to this one.
	public void mergeMap(StatMap other) {
		for (Map.Entry<String, Stat> otherEntry : other.stats.entrySet()) {
			addPut(otherEntry.getKey(), otherEntry.getValue());
		}
	}
	
	// Get the display strings.
	public String[] toDisplayStrings() {
		String[] out = new String[stats.size()];
		int i = 0;
		for (Map.Entry<String, Stat> entry : stats.entrySet()) {
			out[i] = entry.getKey() + ": " + entry.getValue().toString();
			i++;
		}
		return out;
	}
	
	// Get the stat for a key.
	public Stat get(String key) {
		return stats.get(key);
	}
	
	// Return a copy of the tree in this StatMap. While changing the stats will affect this StatMap (it isn't a deep copy),
	// changing the returned tree will not affect this StatMap.
	public TreeMap<String, Stat> copyTree() {
		TreeMap<String, Stat> out = new TreeMap<>();
		out.putAll(stats);
		return out;
	}

	// Evaluate the expressions in the stat map.
	public void evaluateExpressions() {
		// Sort the stats by their order of operations.
		Collection<Stat> statCollection = stats.values();
		Stat[] statVals = statCollection.toArray(new Stat[statCollection.size()]);
		Arrays.sort(statVals, new StatEvalOrderComparator());
		
		// Make a map of the expressions.
		TreeMap<String, Expression> statExpressions = new TreeMap<>();
		for (Map.Entry<String, Stat> entry : stats.entrySet()) {
			Expression exp = entry.getValue().getExpression();
			if (exp != null)
				statExpressions.put(entry.getKey(), exp);
		}
		
		// Evaluate all the stats.
		for (Stat s : statVals) {
			Expression exp = s.getExpression();
			if (exp instanceof VariableExpression) {
				((VariableExpression)exp).evaluate(statExpressions);
			}
		}
	}
	
}
