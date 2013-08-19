// An interface for factories that load things one at a time.

package org.gmcalc2.factory;

import java.io.IOException;
import java.util.Map;

public interface Factory<E> {
	
	// MODIFIES: this
	// EFFECTS:  Set this to load from the dir path. Clears any previous loading progress.
	//			 throws IOException if dirPath is not a directory or is null.
	void setDirectory(String dirPath) throws IOException;
	
	// REQUIRES: setDirectory has been called.
	// MODIFIES: this
	// EFFECTS:  loads the next file in this.
	//			 throws NoSuchElementException if there is no next element to load.
	void loadNext();
	
	// EFFECTS: returns if this is done loading everything in the directory.
	boolean isFinished();
	
	// EFFECTS: return a map of the loaded objects to their filepath relative to the dirPath.
	Map<String, E> getLoadedValues();
	
	// REQUIRES: setDirectory has been called
	// EFFECTS:  Gets the number of files in the directory that will be loaded.
	int getDirSize();
	
}
