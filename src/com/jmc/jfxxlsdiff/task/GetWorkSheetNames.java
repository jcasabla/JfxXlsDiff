/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmc.jfxxlsdiff.task;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Justo_Casablanca
 */
public class GetWorkSheetNames extends Task<ObservableList<String>> {

	private static final Logger logger = Logger.getLogger( GetWorkSheetNames.class.getName() );

	private File xlsFile = null;

	public GetWorkSheetNames( File xlsFile ) {
		super();
		this.xlsFile = xlsFile;
	}

	@Override
	protected ObservableList<String> call() throws Exception {
		logger.log( Level.INFO, "call()" );
		updateProgress( -1L, 1L );

		ObservableList<String> ol = null;
		Workbook wb = null;

		try {
			logger.log( Level.INFO, "creating workbook" );
			wb = WorkbookFactory.create( xlsFile );
		} catch( IOException | InvalidFormatException ex ) {
			logger.log( Level.SEVERE, null, ex );
			updateProgress( 0L, 1L );
			throw ex;
		}

		if( wb != null ) {
			logger.log( Level.INFO, "get sheet names" );

			ol = FXCollections.observableArrayList();
			for( int i = 0; i < wb.getNumberOfSheets(); i++ ) {
				ol.add( wb.getSheetAt( i ).getSheetName() );
			}

			logger.log( Level.INFO, "got {0} sheet names", ol.size() );
			updateProgress( 1L, 1L );
		} else {
			logger.log( Level.WARNING, "workbook *is* null" );
			updateProgress( 0L, 1L );
		}

		return ol;
	}

}
