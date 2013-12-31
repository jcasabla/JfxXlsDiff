/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jmc.jfxxlsdiff.util;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 *
 * @author Justo_Casablanca
 */
public class POIXlsUtil {

	private static final Logger logger = Logger.getLogger( POIXlsUtil.class.getName() );

	public static Object getCellValue( Cell cell ) {
		Object cv = null;
		
		switch( cell.getCellType() ) {
			case Cell.CELL_TYPE_BLANK: {
				break;
			}
			case Cell.CELL_TYPE_BOOLEAN: {
				cv = cell.getBooleanCellValue();
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				cv = cell.getErrorCellValue();
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				cv = getFormulaValue( cell );
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				if( DateUtil.isCellDateFormatted( cell ) ) {
					// format in form of M/D/YY
					//Calendar cal = Calendar.getInstance();
					//cal.setTime( DateUtil.getJavaDate( d ) );
					//cv = cal.getTime();
					cv = cell.getDateCellValue();
				} else {
					cv = cell.getNumericCellValue();
				}
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cv = cell.getStringCellValue();
				break;
			}
			default: {
				logger.log( Level.WARNING, "Unexpected cell type = {0}", cell.getCellType() );
				break;
			}
		}
		
		return cv;
	}

	private static Object getFormulaValue( Cell cell ) {
		Object cv = null;

		FormulaEvaluator fe = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		CellValue v = fe.evaluate( cell );

		switch( v.getCellType() ) {
			case Cell.CELL_TYPE_BLANK: {
				break;
			}
			case Cell.CELL_TYPE_BOOLEAN: {
				cv = v.getBooleanValue();
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				cv = v.getErrorValue();
				break;
			}
			//case Cell.CELL_TYPE_FORMULA: {
			//	cv = cell.getCellFormula();
			//	break;
			//}
			case Cell.CELL_TYPE_NUMERIC: {
				double d = v.getNumberValue();

				if( DateUtil.isCellDateFormatted( cell ) ) {
					Calendar cal = Calendar.getInstance();
					cal.setTime( DateUtil.getJavaDate( d ) );
					cv = cal.getTime();
				} else {
					cv = d;
				}
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cv = v.getStringValue();
				break;
			}
			default: {
				logger.log( Level.WARNING, "Unexpected formula cell type = {0}", v.getCellType() );
				break;
			}
		}

		return cv;
	}
}
