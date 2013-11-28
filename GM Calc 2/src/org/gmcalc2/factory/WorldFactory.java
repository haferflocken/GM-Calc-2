// A world factory contains several factories that create various parts
// of a world, and when done, creates a world.

package org.gmcalc2.factory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.gmcalc2.World;
import org.haferutil.DataReader;
import org.haferutil.Log;
import org.hafermath.expression.ExpressionBuilder;

public class WorldFactory implements Factory<World> {
	
	// This makes worlds. Similar to a factory, but only makes one world rather than many.
	private class WorldBuilder {
		
		private String worldPath;
		private ComponentFactory prefixFactory;
		private ComponentFactory materialFactory;
		private ItemBaseFactory itemBaseFactory;
		private PlayerFactory playerFactory;
		private Map<String, Object> ruleValues;
		private World world;
		private int totalSize; // The total number of times loadNext can be called before failing.
		
		private WorldBuilder() {
			prefixFactory = new ComponentFactory(WorldFactory.this.dataReader, WorldFactory.this.expBuilder);
			materialFactory = new ComponentFactory(WorldFactory.this.dataReader, WorldFactory.this.expBuilder);
			itemBaseFactory = new ItemBaseFactory(WorldFactory.this.dataReader, WorldFactory.this.expBuilder);
			playerFactory = new PlayerFactory(WorldFactory.this.dataReader, null);
			totalSize = 0;
		}
		
		private void setDirectory(String dirPath) throws IOException {
			worldPath = dirPath;
			
			prefixFactory.setDirectory(dirPath + "prefixes\\");
			materialFactory.setDirectory(dirPath + "materials\\");
			itemBaseFactory.setDirectory(dirPath + "itemBases\\");
			playerFactory.setDirectory(dirPath + "players\\");
			
			ruleValues = null;
			
			world = null;
			
			totalSize = prefixFactory.getDirSize() +
					materialFactory.getDirSize() +
					itemBaseFactory.getDirSize() +
					1 +	// Rules
					1 + // Construct world
					playerFactory.getDirSize() +
					1; // Set the world's player map
		}
		
		private void loadRules() {
			try {
				ruleValues = WorldFactory.this.dataReader.readFile(worldPath + "rules.txt");
			}
			catch (IOException e) {
				ruleValues = new TreeMap<>();
			}
		}
		
		private void loadNext() {
			if (!prefixFactory.isFinished())
				prefixFactory.loadNext();
			else if (!materialFactory.isFinished())
				materialFactory.loadNext();
			else if (!itemBaseFactory.isFinished())
				itemBaseFactory.loadNext();
			else if (ruleValues == null)
				loadRules();
			else if (world == null) {
				world = new World(ruleValues, expBuilder, prefixFactory.getLoadedValues(),
						materialFactory.getLoadedValues(), itemBaseFactory.getLoadedValues());
				playerFactory.setWorld(world);
			}
			else if (!playerFactory.isFinished())
				playerFactory.loadNext();
			else if (world.getPlayerMap() == null) 
				world.setPlayerMap(playerFactory.getLoadedValues());
			else
				throw new NoSuchElementException();
		}
		
		private boolean isFinished() {
			return world != null && world.getPlayerMap() != null;
		}
		
		private int getTotalSize() {
			return totalSize;
		}
	}
	
	private Map<String, World> cache;
	private DataReader dataReader;
	private ExpressionBuilder expBuilder;
	private File[] worldDirectories;
	private WorldBuilder[] worldBuilders;
	private int worldIndex;
	private int totalSize;
	
	// Constructor.
	public WorldFactory(DataReader dataReader, ExpressionBuilder expBuilder) {
		this.dataReader = dataReader;
		this.expBuilder = expBuilder;
	}

	// Set the directory.
	@Override
	public void setDirectory(String dirPath) throws IOException {
		// Validate the file.
		if (dirPath == null)
			throw new IOException("Null directory passed to WorldFactory.");
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory())
			throw new IOException("dirPath must represent a directory.");
		
		// Get the world directories and clear the cache.
		worldDirectories = dirFile.listFiles(
				new FileFilter () {
					public boolean accept(File file) {
						return file.isDirectory();
					}
				} );
		worldIndex = 0;
		makeWorldBuilders();
		cache = new TreeMap<>();
	}
	
	// Make the world builders this will use. Called by setDirectory.
	private void makeWorldBuilders() {
		// Reset the total size because we're recalculating it.
		totalSize = 0;
		
		// Make a world builder for each world folder.
		worldBuilders = new WorldBuilder[worldDirectories.length];
		for (int i = 0; i < worldBuilders.length; i++) {
			try {
				// Make the directory path for the builder.
				String worldPath = worldDirectories[i].getAbsolutePath();
				if (worldPath.charAt(worldPath.length() - 1) != '\\')
					worldPath += '\\';
				
				// Make the builder and set its directory.
				worldBuilders[i] = new WorldBuilder();
				worldBuilders[i].setDirectory(worldPath);
				
				
				// Add the builder's size to this one.
				totalSize += worldBuilders[i].getTotalSize();
			}
			catch (IOException e) {
				// For whatever reason, the world builder couldn't be made, so set it to null.
				worldBuilders[i] = null;
			}
		}
	}

	// Load the next thing.
	@Override
	public void loadNext() {
		// Make sure we have an element to look at.
		if (worldIndex >= worldDirectories.length) 
			throw new NoSuchElementException();
		
		// If the world builder is null, skip it.
		if (worldBuilders[worldIndex] == null) {
			worldIndex++;
		}
		// Otherwise, tell the world builder to load the next thing.
		else {
			worldBuilders[worldIndex].loadNext();
			
			// If the world is finished loading, move on to the next world.
			if (worldBuilders[worldIndex].isFinished()) {
				String key = worldDirectories[worldIndex].getName();
				World value = worldBuilders[worldIndex].world;
				cache.put(key, value);
				worldIndex++;
				Log.getDefaultLog().info("Loaded world: " + value.getName());
			}
		}
	}

	// Are we done loading?
	@Override
	public boolean isFinished() {
		return worldIndex >= worldDirectories.length;
	}

	// Get the world.
	@Override
	public Map<String, World> getLoadedValues() {
		return cache;
	}
	
	// Get the number of files that will be loaded.
	@Override
	public int getDirSize() {
		return totalSize;
	}

}
