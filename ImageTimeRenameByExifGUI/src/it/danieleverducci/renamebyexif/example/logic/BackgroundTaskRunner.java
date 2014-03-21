package it.danieleverducci.renamebyexif.example.logic;

import it.danieleverducci.renamebyexif.example.window.ShowDialog;

import javax.swing.SwingWorker;

public class BackgroundTaskRunner extends SwingWorker<Void, Void> {
	private PhotoOrdererLauncher po;
	private String source;
	private String destination;

	public BackgroundTaskRunner(PhotoOrdererLauncher po, String source, String destination) {
		this.po = po;
		this.source = source;
		this.destination = destination;
	}

	@Override
	protected Void doInBackground() throws Exception {
		//Start file ordering
		if(!po.orderPhotos(source, destination)) ShowDialog.errorDialog();
		return null;
	}

}
