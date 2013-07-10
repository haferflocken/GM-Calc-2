//Loads components from files.

package org.gmcalc2.item;

import java.util.TreeMap;
import java.io.File;
import java.io.IOException;

import org.haferlib.util.FileTree;
import org.haferlib.util.DataReader;

public class ItemBaseFactory {
	
	public static final String COMPONENT_FILE_EXTENSION = ".txt";
	
	private TreeMap<String, ItemBase> cache;	//The cache maps absolute file paths to components.
	private DataReader dataReader;				//Loads files as TreeMaps.

	//Constructor.
	public ItemBaseFactory() {
		cache = new TreeMap<>();
		dataReader = new DataReader();
	}
	
	//Cache all the components in a directory.
	public void cacheDirectory(String dirPath) {
		//Make sure the path is a directory.
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory())
			return;
		
		//If the file is a directory, make a file tree and look through it.
		FileTree dirFileTree = new FileTree(dirPath, COMPONENT_FILE_EXTENSION);
		for (File[] group : dirFileTree) {
			if (group != null) {
				for (File file : group) {
					cacheFile(file);
				}
			}
		}
	}
	
	//Cache a file.
	public void cacheFile(File file) {
		try {
			//Read the file.
			TreeMap<String, Object> values = dataReader.readFile(file.toPath());
			
			//Make an ItemBase out of it.
			ItemBase component = new ItemBase(values);
			
			//Add the component to the cache.
			cache.put(file.getAbsolutePath(), component);
		}
		catch (IOException e) {
			System.out.println("Failed to read file " + file.getAbsolutePath());
		}
	}
	
	//Get a component.
	public ItemBase getItemBase(String key) {
		//Try to find it in the cache.
		ItemBase out = cache.get(key);
		//If it's not in the cache, see if there is a file that could be loaded.
		if (out == null) {
			File file = new File(key);
			if (file.exists() && file.isFile()) {
				cacheFile(file);
				return cache.get(key);
			}
		}
		//Otherwise, return the value.
		return out;
	}
	
}
