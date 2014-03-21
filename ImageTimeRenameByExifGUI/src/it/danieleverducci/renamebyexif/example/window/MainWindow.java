package it.danieleverducci.renamebyexif.example.window;

import it.danieleverducci.renamebyexif.example.logic.BackgroundTaskRunner;
import it.danieleverducci.renamebyexif.example.logic.PhotoOrdererLauncher;
import it.danieleverducci.renamebyexif.lib.interfaces.OnProgressUpdateListener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class MainWindow implements OnProgressUpdateListener {

	private JFrame frmImageordererByDaniele;
	private JTextField sourceTextView;
	private JTextField destinationTextView;
	private JButton destinationSelectButton;
	private JProgressBar progressBar;
	private JButton btnOrderFiles;
	private PhotoOrdererLauncher po;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmImageordererByDaniele.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmImageordererByDaniele = new JFrame();
		frmImageordererByDaniele.setTitle("ImageOrderer by Daniele Verducci");
		frmImageordererByDaniele.setBounds(100, 100, 700, 211);
		frmImageordererByDaniele.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmImageordererByDaniele.getContentPane().setLayout(null);
		frmImageordererByDaniele.setResizable(false);
		
		sourceTextView = new JTextField();
		sourceTextView.setBounds(12, 15, 539, 19);
		frmImageordererByDaniele.getContentPane().add(sourceTextView);
		sourceTextView.setColumns(10);
		
		JButton sourceSelectButton = new JButton("Source");
		sourceSelectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Open file window
				String selectedPath = MainWindow.showFileDialog(sourceTextView.getText());
				if(selectedPath!=null) sourceTextView.setText(selectedPath);
			}
		});
		sourceSelectButton.setBounds(563, 12, 117, 25);
		frmImageordererByDaniele.getContentPane().add(sourceSelectButton);
		
		destinationTextView = new JTextField();
		destinationTextView.setColumns(10);
		destinationTextView.setBounds(12, 49, 539, 19);
		frmImageordererByDaniele.getContentPane().add(destinationTextView);
		
		destinationSelectButton = new JButton("Destination");
		destinationSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Open file window
				String selectedPath = MainWindow.showFileDialog(sourceTextView.getText());
				if(selectedPath!=null) destinationTextView.setText(selectedPath);
			}
		});
		destinationSelectButton.setBounds(563, 49, 117, 25);
		frmImageordererByDaniele.getContentPane().add(destinationSelectButton);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(22, 105, 658, 14);
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(100);
		frmImageordererByDaniele.getContentPane().add(progressBar);
		
		btnOrderFiles = new JButton("Order files");
		btnOrderFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//User clicked the start button
				po = new PhotoOrdererLauncher();
				po.setOnProgressUpdateListener(MainWindow.this);
				String source = sourceTextView.getText();
				String destination = destinationTextView.getText();
				//Execute code in background to not block the UI
				BackgroundTaskRunner tr = new BackgroundTaskRunner(po, source, destination);
				progressBar.setValue(0);
				//Disable button to prevent user click it a second time
				btnOrderFiles.setEnabled(false);
				//Start thread
				tr.execute();
			}
		});
		btnOrderFiles.setBounds(309, 135, 117, 25);
		frmImageordererByDaniele.getContentPane().add(btnOrderFiles);
		
		//Set source and destination dirs default
		File currentDirFile = new File("");
		String currentDir = "/";
		try {
			currentDir = currentDirFile.getCanonicalPath();
		} catch (IOException e) {}
		sourceTextView.setText(currentDir);
		destinationTextView.setText(currentDir);
	}

	
	
	protected static String showFileDialog(String currentPath) {
		File currentPathFile = new File(currentPath);
		if(!(currentPathFile.exists()&&currentPathFile.isDirectory())) currentPath="";
		
		JFileChooser ch = new JFileChooser(new File(currentPath));
		ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int userSelectedButton = ch.showOpenDialog(null);
		if(userSelectedButton==JFileChooser.APPROVE_OPTION){
			//User clicked yes. Return dir path
			File selectedDir = ch.getSelectedFile();
			try {
				return selectedDir.getCanonicalPath();
			} catch (IOException e) {
				System.out.println("Error: unable to get user selected path.");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void onProgressUpdate(int percent) {
		if(percent>99 || percent<0) {
			btnOrderFiles.setEnabled(true);
			progressBar.setValue(0);
		} else progressBar.setValue(percent);		
	}

}
