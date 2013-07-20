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
import org.haferlib.slick.gui.OutputFrame;
import org.haferlib.util.DataReader;

public class WorldFactory implements Factory<World> {
	
	// This makes worlds. Similar to a factory, but only makes one world rather than many.
	private class WorldBuilder {
		
		private String worldPath;
		private ComponentFactory prefixFactory;
		private ComponentFactory materialFactory;
		private ItemBaseFactory itemBaseFactory;
		private PlayerFactory playerFactory;
		private TreeMap<String, Object> ruleValues;
		private World world;
		
		private WorldBuilder() {
			prefixFactory = new ComponentFactory(WorldFactory.this.dataReader);
			materialFactory = new ComponentFactory(WorldFactory.this.dataReader);
			itemBaseFactory = new ItemBaseFactory(WorldFactory.this.dataReader);
			playerFactory = new PlayerFactory(WorldFactory.this.dataReader, null);
		}
		
		private void setOutputFrame() {
			prefixFactory.setOutputFrame(WorldFactory.this.out);
			materialFactory.setOutputFrame(WorldFactory.this.out);
			itemBaseFactory.setOutputFrame(WorldFactory.this.out);
			playerFactory.setOutputFrame(WorldFactory.this.out);
		}
		
		private void setDirectory(String dirPath) throws IOException {
			worldPath = dirPath;
			
			prefixFactory.setDirectory(dirPath + "prefixes\\");
			materialFactory.setDirectory(dirPath + "materials\\");
			itemBaseFactory.setDirectory(dirPath + "itemBases\\");
			playerFactory.setDirectory(dirPath + "players\\");
			
			ruleValues = null;
			
			world = null;
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
				world = new World(ruleValues, prefixFactory.getLoadedValues(),
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
	}
	
	private Map<String, World> cache;
	private DataReader dataReader;
	private File[] worldDirectories;
	private int worldIndex;
	private WorldBuilder worldBuilder;
	private OutputFrame out;
	
	// Constructor.
	public WorldFactory(DataReader dataReader) {
		this.dataReader = dataReader;
		worldBuilder = new WorldBuilder();
	}

	// Set the output frame.
	public void setOutputFrame(OutputFrame frame) {
		out = frame;
		worldBuilder.setOutputFrame();
	}

	// Set the directory.
	public void setDirectory(String dirPath) throws IOException {
		// Validate the file.
		if (dirPath == null)
			throw new IOException("Null directory passed to WorldFactory.");
		File dirFile = new File(dirPath);
		if (!dirFile.isDirectory())
			throw new IllegalArgumentException("dirPath must represent a directory.");
		
		// Get the world directories and clear the cache.
		worldDirectories = dirFile.listFiles(
				new FileFilter () {
					public boolean accept(File file) {
						return file.isDirectory();
					}
				} );
		worldIndex = -1;
		findNextWorld();
		cache = new TreeMap<>();
	}
	
	// Figure out what the next world to load is.
	private void findNextWorld() {
		worldIndex++;
		if (worldIndex < worldDirectories.length) {
			try {
				String worldPath = worldDirectories[worldIndex].getAbsolutePath();
				if (worldPath.charAt(worldPath.length() - 1) != '\\')
					worldPath += '\\';
				worldBuilder.setDirectory(worldPath);
			}
			catch (IOException e) {
				worldIndex = worldDirectories.length;
			}
		}
	}

	// Load the next thing.
	public void loadNext() {
		// Make sure we have an element to look at.
		if (worldIndex >= worldDirectories.length) 
			throw new NoSuchElementException();
		
		// Tell the world builder to load the next thing.
		worldBuilder.loadNext();
		
		// If the world is finished loading, move on to the next world.
		if (worldBuilder.isFinished()) {
			cache.put(worldDirectories[worldIndex].getName(), worldBuilder.world);
			findNextWorld();
		}
	}

	// Are we done loading?
	public boolean isFinished() {
		return worldIndex >= worldDirectories.length;
	}

	// Get the world.
	public Map<String, World> getLoadedValues() {
		return cache;
	}

}
