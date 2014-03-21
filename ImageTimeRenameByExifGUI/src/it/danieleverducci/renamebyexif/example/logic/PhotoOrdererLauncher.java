package it.danieleverducci.renamebyexif.example.logic;

import it.danieleverducci.renamebyexif.example.window.ShowDialog;
import it.danieleverducci.renamebyexif.lib.PhotoOrderer;
import it.danieleverducci.renamebyexif.lib.interfaces.OnProgressUpdateListener;

import java.io.File;
import java.io.IOException;

public class PhotoOrdererLauncher implements OnProgressUpdateListener {
	private OnProgressUpdateListener onProgressUpdateListener;
	
	public boolean orderPhotos(String sourceDirectory, String destinationDirectory) {		
		File sourceDir = new File(sourceDirectory);
		if(!sourceDir.exists() || !sourceDir.isDirectory()) return false;
		File destDir = new File(destinationDirectory);

		//Verify if destDir exist. Create it if not.
		boolean proceed=false;
		if(!destDir.isDirectory()){
			if(ShowDialog.createFolderDialog(destinationDirectory)){
				proceed = destDir.mkdirs();
			}
		} else proceed=true;

		if(proceed) {
			PhotoOrderer po = new PhotoOrderer();
			po.setLogging(true);
			po.setRecursive(true);
			po.setSkipSingleFilesError(true);
			po.setOnProgressUpdateListener(this);
			try {
				po.renameAndMoveFilesInDir(sourceDir, destDir);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else onProgressUpdateListener.onProgressUpdate(-1);
		return proceed;
	}
	
	public void setOnProgressUpdateListener(OnProgressUpdateListener listener){
		this.onProgressUpdateListener = listener;
	}

	@Override
	public void onProgressUpdate(int progress) {
		onProgressUpdateListener.onProgressUpdate(progress);
	}
}
