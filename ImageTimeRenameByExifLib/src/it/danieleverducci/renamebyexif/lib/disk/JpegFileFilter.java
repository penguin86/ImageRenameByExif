package it.danieleverducci.renamebyexif.lib.disk;

import java.io.File;

public class JpegFileFilter implements java.io.FileFilter {
	private static final String[] SUPPORTED_FILE_TYPES = {".jpg", "jpeg"};

	@Override
	public boolean accept(File pathname) {
		String filename = pathname.getName();
		if(filename.length()<4) return false;
		String extension = (filename.substring(filename.length()-4)).toLowerCase();	//Last 4 letters of the name. Es: .jpg, .png, jpeg etc
		for(int i=0; i<SUPPORTED_FILE_TYPES.length; i++){
			if(extension.equals(SUPPORTED_FILE_TYPES[i].toLowerCase())) return true;
		}
		return false;
	}

}
