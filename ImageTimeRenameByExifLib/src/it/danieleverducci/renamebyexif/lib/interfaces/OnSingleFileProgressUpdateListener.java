package it.danieleverducci.renamebyexif.lib.interfaces;

import java.io.File;

public interface OnSingleFileProgressUpdateListener {
	public void onSingleFileProgressUpdate(File source, File destination, int currentFileCount, int totalFileCount, boolean success);
}
