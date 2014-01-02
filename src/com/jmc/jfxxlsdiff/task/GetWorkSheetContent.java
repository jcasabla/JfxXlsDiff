/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmc.jfxxlsdiff.task;

import com.jmc.jfxxlsdiff.util.POIXlsUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

/**
 *
 * @author Justo_Casablanca
 */
public class GetWorkSheetContent extends Task<GetWorkSheetContent.Result> {

	private static final Logger logger = Logger.getLogger( GetWorkSheetContent.class.getName() );

	private Sheet sheet = null;

	public GetWorkSheetContent( Sheet sheet ) {
		super();
		this.sheet = sheet;
	}

	public GetWorkSheetContent( Workbook wb, String sheetName ) {
		this( wb.getSheet( sheetName ) );
	}

	public GetWorkSheetContent( File xlsFile, String sheetName )
		throws IOException, InvalidFormatException {
		this( WorkbookFactory.create( xlsFile ), sheetName );
	}

	public GetWorkSheetContent( String fileName, String sheetName )
		throws IOException, InvalidFormatException {
		this( new File( fileName ), sheetName );
	}

	@Override
	protected GetWorkSheetContent.Result call() throws Exception {
		logger.log( Level.INFO, "call()" );
		updateProgress( -1L, 1L );
		GetWorkSheetContent.Result res = new GetWorkSheetContent.Result();

		res.range = new CellRangeAddress(
			sheet.getFirstRowNum(),
			sheet.getLastRowNum(),
			findFirstColumn(),
			findLastColumn()
		);

		res.colNames = FXCollections
			.observableArrayList( findColumnNames( res.range.getLastColumn() ) );
		res.rows = FXCollections
			.observableArrayList( findRows() );

		res.log();

		updateProgress( 1L, 1L );
		return res;
	}

	private int findFirstColumn() {
		int colIdx = 0;

		for( int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++ ) {
			colIdx = Math.min( colIdx, sheet.getRow( i ).getFirstCellNum() );
		}

		return colIdx;
	}

	private int findLastColumn() {
		int colIdx = 0;

		for( int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++ ) {
			colIdx = Math.max( colIdx, sheet.getRow( i ).getLastCellNum() );
		}

		// For some reason the POI API creators decided that Row.getLastCellNum()
		// should return the 1-based index instead of the 0-based index
		// Reference: http://poi.apache.org/apidocs/org/apache/poi/ss/usermodel/Row.html#getLastCellNum()
		//return ( colIdx > 0 ) ? ( colIdx - 1 ) : colIdx ;

		return colIdx;
	}

	private List<String> findColumnNames( int lastCol ) {
		List<String> colNames = new ArrayList<>();
		//colNames.add( "*" );

		for( int i = 0; i < lastCol; i++ ) {
			colNames.add( CellReference.convertNumToColString( i ) );
		}

		return colNames;
	}

	private List<List> findRows() {
		List<List> rows = new ArrayList<>();
		Iterator<Row> ri = sheet.rowIterator();

		while( ri.hasNext() ) {
			Row r = ri.next();
			Iterator<Cell> ci = r.cellIterator();

			if( ci.hasNext() ) {
				List colVals = new ArrayList();
				//colVals.add( r.getRowNum() + 1 );

				for(int i=0; ci.hasNext(); i++) {
					Cell cell = ci.next();

					while( cell.getColumnIndex() > i ) {
						i++;
						colVals.add( null);
					}

					colVals.add( POIXlsUtil.getCellValue( cell ) );
				}
				rows.add( colVals );
			} else {
				rows.add( null );
			}
			//rows.add( ri.next() );
		}

		return rows;
	}

	public class Result {

		public CellRangeAddress range = null;
		public ObservableList<List> rows = null;
		public ObservableList<String> colNames = null;

		public void log() {
			Logger.getLogger( this.getClass().getName() ).log(
				Level.INFO,
				new StringBuilder()
					.append( "range=[{0}], " )
					.append( "rows.size={1}, " )
					.append( "colNames.size={2}, " )
					.append( "colNames.range={3}:{4}, " )
					.toString(),
				new Object[] {
					range == null ? null : range.toString(),
					rows == null ? 0 : rows.size(),
					colNames == null ? 0 : colNames.size(),
					colNames == null ? "?" : colNames.get( 0 ),
					colNames == null ? "?" : colNames.get( colNames.size()-1 )
				}
			);
		}
	}
}
