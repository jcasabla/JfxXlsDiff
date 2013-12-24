/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmc.jfxxlsdiff;

import com.jmc.jfxxlsdiff.task.GetWorkSheetNames;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * FXML Controller class
 *
 * @author Justo_Casablanca
 */
public class MainDiffWindowController implements Initializable {

	private static final Logger logger = Logger.getLogger( MainDiffWindowController.class.getName() );

	@FXML private TextField txtFile1;

	@FXML private TextField txtFile2;

	@FXML private ChoiceBox cbxTabs1;

	@FXML private ChoiceBox cbxTabs2;

	@FXML private ProgressIndicator progress1;

	@FXML private ProgressIndicator progress2;

	@FXML
	protected void handleSelectFile1Action( ActionEvent event ) {
		logger.log( Level.INFO, "handleSelectFile1Action( ActionEvent )" );
		handleSelectFile( txtFile1, cbxTabs1, progress1 );
	}

	@FXML
	protected void handleSelectFile2Action( ActionEvent event ) {
		logger.log( Level.INFO, "handleSelectFile2Action( ActionEvent )" );		
		handleSelectFile( txtFile2, cbxTabs2, progress2 );
	}

	private void handleSelectFile( TextField tf,
								   ChoiceBox cbx,
								   ProgressIndicator pi )
	{
		logger.log( Level.INFO, "handleSelectFile( TextField, ChoiceBox, ProgressIndicator )" );		

		logger.log( Level.INFO, "ask the user to select a file" );
		File xlsFile = showFileOpenDialog();

		if( xlsFile != null ) {
			logger.log( Level.INFO, "user selected: {0}", xlsFile.getAbsolutePath() );
			tf.setText( xlsFile.getAbsolutePath() );
			
			logger.log( Level.INFO, "start background process to get worksheet names from the file" );
			startGetWorkSheetNamesTask( xlsFile, cbx, pi );
		} else {
			logger.log( Level.INFO, "user did *not* select a file" );
		}
	}

	private File showFileOpenDialog() {
		logger.log( Level.INFO, "showFileOpenDialog()" );		

		logger.log( Level.INFO, "add list of MS Excel extensions" );
		List<String> extensions = new ArrayList<>();
		extensions.add( "*.xls" );
		extensions.add( "*.xlsx" );

		logger.log( Level.INFO, "create the extension filter" );
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(
				new ExtensionFilter( "Microsoft Office Workbook", extensions ) );

		logger.log( Level.INFO, "prompt the user and return the user's selection" );
		return fc.showOpenDialog( null );
	}

	private void startGetWorkSheetNamesTask( final File xlsFile,
											 final ChoiceBox cbxXlsSheetNames,
											 final ProgressIndicator progress )
	{
		logger.log( Level.INFO, "startGetWorkSheetNamesTask( File, ChoiceBox, ProgressIndicator )" );

		logger.log( Level.INFO, "create instance of GetWorkSheetNames task" );
		final GetWorkSheetNames wsnTask = new GetWorkSheetNames( xlsFile );

		logger.log( Level.INFO, "bind ProgressIndicator to GetWorkSheetNames" );
		progress.progressProperty().bind( wsnTask.progressProperty() );

		logger.log( Level.INFO, "assign event handler for GetWorkSheetNames.onSucceeded" );
		wsnTask.setOnSucceeded( new EventHandler() {
			@Override
			public void handle( Event evt ) {
				cbxXlsSheetNames.setItems( wsnTask.getValue() );
			}
		} );

		logger.log( Level.INFO, "crate thread for GetWorkSheetNames and start it" );
		Thread thread = new Thread( wsnTask );
		thread.setDaemon( true );
		thread.start();
	}

	/**
	 * Initializes the controller class.
	 * <p>
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize( URL url, ResourceBundle rb ) {
		// TODO
	}

}
