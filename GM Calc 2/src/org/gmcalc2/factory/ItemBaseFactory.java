//Loads item bases from files.

package org.gmcalc2.factory;

import org.gmcalc2.item.ItemBase;

import java.util.TreeMap;

import org.haferlib.util.DataReader;

public class ItemBaseFactory extends AbstractFactoryDataReader<ItemBase> {
	
	public static final String FILE_EXTENSION = ".txt";
	
	// Constructor.
	public ItemBaseFactory(DataReader dataReader) {
		super(dataReader);
	}
	
	// Make the component.
	public ItemBase makeFromValues(TreeMap<String, Object> values) {
		return new ItemBase(values);
	}
	
	// Get the file extension.
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
}
