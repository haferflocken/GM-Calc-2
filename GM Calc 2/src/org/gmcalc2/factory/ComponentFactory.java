//Loads item bases from files.

package org.gmcalc2.factory;

import org.gmcalc2.item.Component;

import java.util.TreeMap;

import org.haferlib.util.DataReader;

public class ComponentFactory extends AbstractFactoryDataReader<Component> {
	
	public static final String FILE_EXTENSION = ".txt";
	
	// Constructor.
	public ComponentFactory(DataReader dataReader) {
		super(dataReader);
	}
	
	// Make the component.
	public Component makeFromValues(TreeMap<String, Object> values) {
		return new Component(values);
	}

	// Get the file extension.
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
}
