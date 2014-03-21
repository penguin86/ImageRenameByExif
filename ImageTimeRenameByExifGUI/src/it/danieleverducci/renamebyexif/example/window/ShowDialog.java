package it.danieleverducci.renamebyexif.example.window;

import javax.swing.JOptionPane;

public class ShowDialog {
	/**
	 * Shows a dialog asking the user to create a folder
	 * @param folder the folder name to display to the user
	 * @return true if the user wants the folder to be created, false otherwise
	 */
	public static boolean createFolderDialog(String folder){
		if(folder==null || folder.equals("")) return false;

		String[] options = {"Yes", "No"};
		String title = "Create folder?";
		String message = "The folder "+folder+" does not exist. Do you want to create it?";

		int result = JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return result==0;
	}

	public static void errorDialog(){
		JOptionPane.showMessageDialog(null,
				"Unable to proceed: the destination folder does not exist.",
				"Operation not started",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
