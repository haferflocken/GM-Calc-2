//Loads item bases from files.

package org.gmcalc2.factory;

import org.gmcalc2.item.ItemBase;

import java.util.TreeMap;

import org.haferlib.util.DataReader;
import org.haferlib.util.expression.ExpressionBuilder;

public class ItemBaseFactory extends AbstractFactoryDataReader<ItemBase> {
	
	public static final String FILE_EXTENSION = ".txt";
	
	private ExpressionBuilder expBuilder;
	
	// Constructor.
	public ItemBaseFactory(DataReader dataReader, ExpressionBuilder expBuilder) {
		super(dataReader);
		this.expBuilder = expBuilder;
	}
	
	// Make the component.
	public ItemBase makeFromValues(TreeMap<String, Object> values) {
		return new ItemBase(values, expBuilder);
	}
	
	// Get the file extension.
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
}