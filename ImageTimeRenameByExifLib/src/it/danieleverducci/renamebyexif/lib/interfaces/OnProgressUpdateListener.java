package it.danieleverducci.renamebyexif.lib.interfaces;

public interface OnProgressUpdateListener {
	/**
	 * Called on every progress update.
	 * The parameter @param progress is an integer from 0 to 100 representing
	 * the percent of files renamed. 
	 */
	public void onProgressUpdate(int progress);
}
