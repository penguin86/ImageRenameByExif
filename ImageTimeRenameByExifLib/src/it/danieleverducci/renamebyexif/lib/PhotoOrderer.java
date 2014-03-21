package it.danieleverducci.renamebyexif.lib;

import it.danieleverducci.renamebyexif.lib.disk.JpegFileFilter;
import it.danieleverducci.renamebyexif.lib.interfaces.OnProgressUpdateListener;
import it.danieleverducci.renamebyexif.lib.interfaces.OnSingleFileProgressUpdateListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class PhotoOrderer {
	private int totalFilesToProcess = 0;
	private int processedFiles = 0;
	private OnProgressUpdateListener onProgressUpdateListener;
	private OnSingleFileProgressUpdateListener onSingleFileProgressUpdateListener;
	private boolean logging = false;
	private boolean skipSingleFilesError = true;
	private boolean recursive = true;
	
	/**
	 * Cycle all files and rename+move it according to the EXIF or file creation data.
	 * @param sourceDir is the directory containing all the files to be processed
	 * @param destDir is the destination directory
	 * @throws IOException if a file cannot be read or written while skipSingleFileError is set to false.
	 * @throws IllegalArgumentException if sourceDir or destDir is not a directory, does not exist or cannot be read/written
	 */
	public void renameAndMoveFilesInDir(File sourceDir, File destDir) throws IOException, IllegalArgumentException{
		if(!sourceDir.isDirectory() || !destDir.isDirectory())
			throw new IllegalArgumentException("The source or destination directory does not exist or is not a directory");
		File[] filesInSourceDir = sourceDir.listFiles(new JpegFileFilter());
		renameAndMoveFileArray(filesInSourceDir, destDir);
	}

	/**
	 * Cycle all files and rename+move it according to the EXIF or file creation data.
	 * @param filesInSourceDir is an array of files to be processed,
	 * @param destDir is the destination directory
	 * @throws IOException if a file cannot be read or written while skipSingleFileError is set to false.
	 */
	public void renameAndMoveFileArray(File[] filesInSourceDir, File destDir) throws IOException {
		if(!destDir.isDirectory())
			throw new IllegalArgumentException("The destination directory does not exist or is not a directory");
		
		totalFilesToProcess = filesInSourceDir.length;
		for(int i=0; i<filesInSourceDir.length; i++){
			if(filesInSourceDir[i].isFile()){
				try {
					renameAndMoveSingleFile(filesInSourceDir[i], destDir);
				} catch (IOException e) {
					if(!skipSingleFilesError) throw e;
					else {
						if(logging) {
							System.out.println("Disk I/O error:");
							e.printStackTrace();
						}
					}
				}
			} else if(recursive) renameAndMoveFileArray(filesInSourceDir[i].listFiles(new JpegFileFilter()), destDir);
		}
		//Rename finished: reinitialize for object recycling
		processedFiles=0;
	}

	private void renameAndMoveSingleFile(File sourceFile, File destinationDirectory) throws IOException{
		//Retrieve exif data
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(sourceFile);
		} catch (Exception e) {
			if(logging)  System.out.println("Unable to get image metadata for "+sourceFile.getName()+"\nThe file will be ignored.");
			e.printStackTrace();
			return;
		}
		Date creationDate;
		if(metadata!=null){
			//Retrieve image shooting date from exif data
			ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			creationDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		} else {
			//If exif data is unavailable, retrieve file creation date
			Path path = Paths.get(sourceFile.getCanonicalPath());
			BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
			FileTime creationTime = attributes.creationTime();
			creationDate = new Date(creationTime.toMillis());
		}
		if(creationDate==null) {
			if(logging) System.out.println("Unable to retrieve date for file "+sourceFile.getName()+". It will be ignored.");
			return;
		}
		String creationDateString = new SimpleDateFormat("yyyy-MM-dd HH-mm").format(creationDate);
		File destinationFile;
		int fileCounter = 0;
		//Exif data contains the date minutes, but not seconds. So there can be more photos taken in the same minute.
		//To avoid file collision, we add a counter at the end of the file and increment it by 1 if the file exists.
		do{
			destinationFile = new File(destinationDirectory.getCanonicalFile().toString()+File.separator+creationDateString+" - "+fileCounter+".jpg");
			fileCounter++;
		}while(destinationFile.exists());

		processedFiles++;
		//Copy file
		try{
			Files.copy(sourceFile.toPath(), destinationFile.toPath());
			if(logging) System.out.println(sourceFile.getName()+" renamed to "+destinationFile.getName());
			if(onSingleFileProgressUpdateListener!=null) onSingleFileProgressUpdateListener.onSingleFileProgressUpdate(sourceFile, destinationFile, processedFiles, totalFilesToProcess, true);
		}catch(IOException e){
			if(logging) System.out.println(sourceFile.getName()+" renaming to "+destinationFile.getName()+" failed: "+e.toString());
			if(onSingleFileProgressUpdateListener!=null) onSingleFileProgressUpdateListener.onSingleFileProgressUpdate(sourceFile, destinationFile, processedFiles, totalFilesToProcess, false);
			throw e;
		}
		//Notify progression update
		if(onProgressUpdateListener!=null) onProgressUpdateListener.onProgressUpdate((int)(((float)processedFiles/(float)totalFilesToProcess)*100.0f)+1);
	}



	/**
	 * Enable or disable console logging
	 */
	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	/**
	 * If @param skipSingleFilesError is set to true, the rename will continue ignoring errors.
	 * Otherwise, an exception will be throw
	 */
	public void setSkipSingleFilesError(boolean skipSingleFilesError) {
		this.skipSingleFilesError = skipSingleFilesError;
	}

	/**
	 * Called to notify the progression file by file
	 */
	public void setOnSingleFileProgressUpdateListener(
			OnSingleFileProgressUpdateListener onSingleFileProgressUpdateListener) {
		this.onSingleFileProgressUpdateListener = onSingleFileProgressUpdateListener;
	}

	/**
	 * Called to notify the progression in percentage
	 */
	public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
		this.onProgressUpdateListener = onProgressUpdateListener;
	}

	/**
	 * If @param recursive is set to true, the directories found will be recursively scanned searching
	 * for other orderable files
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
}
