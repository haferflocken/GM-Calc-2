//Loads item bases from files.

package org.gmcalc2.factory;

import org.gmcalc2.item.Component;

import java.util.Map;

import org.haferutil.DataReader;
import org.hafermath.expression.ExpressionBuilder;

public class ComponentFactory extends AbstractFactoryDataReader<Component> {
	
	public static final String FILE_EXTENSION = ".txt";
	
	private ExpressionBuilder expBuilder;
	
	// Constructor.
	public ComponentFactory(DataReader dataReader, ExpressionBuilder expBuilder) {
		super(dataReader);
		this.expBuilder = expBuilder;
	}
	
	// Make the component.
	public Component makeFromValues(String absolutePath, String relativePath, Map<String, Object> values) {
		return new Component(relativePath, values, expBuilder);
	}

	// Get the file extension.
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
}
