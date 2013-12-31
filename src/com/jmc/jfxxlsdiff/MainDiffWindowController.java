/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmc.jfxxlsdiff;

import com.jmc.jfxxlsdiff.task.GetWorkSheetContent;
import com.jmc.jfxxlsdiff.task.GetWorkSheetNames;
import com.jmc.jfxxlsdiff.util.TableViewUtil;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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

	@FXML private ProgressIndicator sheets1LoadProgress;

	@FXML private ProgressIndicator sheets2LoadProgress;

	@FXML private ProgressIndicator loadProgress;

	@FXML private TableView table1;

	@FXML private TableView table2;

	@FXML
	protected void handleSelectFile1Action( ActionEvent event ) {
		logger.log( Level.INFO, "handleSelectFile1Action( ActionEvent )" );
		handleSelectFile( txtFile1, cbxTabs1, sheets1LoadProgress );
	}

	@FXML
	protected void handleSelectFile2Action( ActionEvent event ) {
		logger.log( Level.INFO, "handleSelectFile2Action( ActionEvent )" );		
		handleSelectFile( txtFile2, cbxTabs2, sheets2LoadProgress );
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

		logger.log( Level.INFO, "create thread for GetWorkSheetNames and start it" );
		Thread thread = new Thread( wsnTask );
		thread.setDaemon( true );
		thread.start();
	}

	@FXML
	protected void handleLoadFile1Action( ActionEvent event )
			throws IOException, InvalidFormatException
	{
		logger.log( Level.INFO, "handleLoadFile1Action( ActionEvent )" );
		startLoadXlsFileContentsTask(
				txtFile1.getText(),
				cbxTabs1.getSelectionModel().getSelectedItem().toString(),
				table1,
				loadProgress );
	}

	@FXML
	protected void handleLoadFile2Action( ActionEvent event )
			throws IOException, InvalidFormatException
	{
		logger.log( Level.INFO, "handleLoadFile2Action( ActionEvent )" );
		startLoadXlsFileContentsTask(
				txtFile2.getText(),
				cbxTabs2.getSelectionModel().getSelectedItem().toString(),
				table2,
				loadProgress );
	}

	private void startLoadXlsFileContentsTask( String fileName,
											   String sheetName,
											   final TableView table,
											   ProgressIndicator progress )
			throws IOException, InvalidFormatException
	{
		logger.log( Level.INFO,
					"startLoadXlsFileContentsTask( fileName=[{0}], sheetName=[{1}] )",
					new Object[] { fileName, sheetName } );
	
		final GetWorkSheetContent wscTask;

		try {
			logger.log( Level.INFO, "create instance of GetWorkSheetContent task" );			
			wscTask = new GetWorkSheetContent( fileName, sheetName );
		} catch( IOException | InvalidFormatException ex ) {
			Logger.getLogger( MainDiffWindowController.class.getName() ).log( Level.SEVERE, null, ex );
			throw ex;
		}
		
		logger.log( Level.INFO, "bind ProgressIndicator to GetWorkSheetContent" );
		progress.progressProperty().bind( wscTask.progressProperty() );

		logger.log( Level.INFO, "assign event handler for GetWorkSheetNames.onSucceeded" );
		wscTask.setOnSucceeded( new EventHandler() {
			@Override
			public void handle( Event evt ) {
				populateTable( wscTask.getValue(), table );
			}
		} );

		logger.log( Level.INFO, "create thread for GetWorkSheetNames and start it" );
		Thread thread = new Thread( wscTask );
		thread.setDaemon( true );
		thread.start();
	}

	private void populateTable( GetWorkSheetContent.Result r, TableView t ) {
		logger.log( Level.INFO,	"populateTable( GetWorkSheetContent.Result, TableView )" );

		Callback cb = new XlsCellValueFactory();

		t.getColumns().clear();
		for( String s : r.colNames ) {
			TableColumn tc = new TableColumn( s );
			tc.setCellValueFactory( cb );
			t.getColumns().add( tc );
		}

		t.setItems( r.rows );
	}
	
	public void finishInitializingControls() {
		logger.log( Level.INFO,	"finishInitializingControls()" );
		TableViewUtil.syncScrolling( table1, table2 );		
	}

	/**
	 * Initializes the controller class.
	 * <p>
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize( URL url, ResourceBundle rb ) {
		logger.log( Level.INFO,	"initialize( URL, ResourceBundle )" );
	}

}
