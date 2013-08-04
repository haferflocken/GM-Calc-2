//Loads objects from files using a DataReader.

package org.gmcalc2.factory;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.io.File;
import java.io.IOException;

import org.haferlib.slick.gui.OutputFrame;
import org.haferlib.util.FileTree;
import org.haferlib.util.DataReader;

public abstract class AbstractFactoryDataReader<E> implements Factory<E> {
	
	public static final String COMPONENT_FILE_EXTENSION = ".txt";
	
	private TreeMap<String, E> cache;			// The cache maps absolute file paths to components.
	private DataReader dataReader;				// Loads files as TreeMaps.
	private FileTree dirTree;					// The tree of files we're loading.
	private Iterator<File[]> dirTreeIterator;	// The iterator over the tree.
	private File[] curDir;						// The contents of the current folder.
	private int curDirIndex;					// The index in the current folder.
	private boolean doneLoading;				// Are we done loading?
	private OutputFrame out;					// The output frame.

	//Constructor.
	public AbstractFactoryDataReader(DataReader dataReader) {
		this.dataReader = dataReader;
	}
	
	// Set the directory and prepare to load it.
	@Override
	public void setDirectory(String dirPath) {
		// Make sure the dir path is not null.
		if (dirPath == null)
			throw new NullPointerException("dirPath must not be null.");
		
		//Make sure the path is a directory.
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory())
			throw new IllegalArgumentException("dirPath must represent a directory.");
		
		//If the file is a directory, make a file tree and an iterator to look through it.
		dirTree = new FileTree(dirPath, getFileExtension());
		dirTreeIterator = dirTree.iterator();
		doneLoading = false;
		cache = new TreeMap<>();
		curDir = null;
		findNextFile();
	}
	
	// Find the next file to load and determine if we are done loading.
	private void findNextFile() {
		// See if there is a next for the current folder.
		if (curDir != null && curDirIndex < curDir.length - 1)
			curDirIndex++;
		//If there isn't a next in the current folder, see if there is a a next for the dirTreeIterator.
		else if (dirTreeIterator.hasNext()) {
			do {
				curDir = dirTreeIterator.next();
				curDirIndex = 0;
			} while (dirTreeIterator.hasNext() && (curDir == null || curDir.length == 0));
			//See if we are done.
			if (!dirTreeIterator.hasNext() && (curDir == null || curDir.length == 0))
				doneLoading = true;
		}
		//Otherwise, mark this as done.
		else {
			doneLoading = true;
		}
	}
	
	// Load the next file.
	@Override
	public void loadNext() {
		//Throw an exception if were done loading.
		if (doneLoading)
			throw new NoSuchElementException();
		
		//Get the file to work with.
		File file = curDir[curDirIndex];
			
		try {
			//Read the file.
			TreeMap<String, Object> values = dataReader.readFile(file.toPath());
			
			// Get both the full and relative file paths.
			String absolutePath = file.getAbsolutePath();
			String relativePath = absolutePath.substring(dirTree.getRootPath().length());
			
			//Make a component out of it.
			E component = makeFromValues(absolutePath, relativePath, values);
			
			//Add the component to the cache.
			cache.put(relativePath, component);
			out.println("Cached object from " + relativePath);
		}
		catch (IOException e) {
			out.println("Failed to read file " + file.getAbsolutePath());
		}
		
		//Find the next file.
		findNextFile();
	}
	
	// Make an object from given values.
	public abstract E makeFromValues(String absolutePath, String relativePath, TreeMap<String, Object> values);
	
	// Get the file extension to look for.
	public abstract String getFileExtension();

	// Set the output frame.
	@Override
	public void setOutputFrame(OutputFrame frame) {
		out = frame;
	}

	// Return true if we are done loading, false otherwise.
	@Override
	public boolean isFinished() {
		return doneLoading;
	}

	// Return the cache.
	@Override
	public Map<String, E> getLoadedValues() {
		return cache;
	}
	
	// Get the number of files that will be loaded.
	@Override
	public int getDirSize() {
		return dirTree.getNumFiles();
	}
	
}
