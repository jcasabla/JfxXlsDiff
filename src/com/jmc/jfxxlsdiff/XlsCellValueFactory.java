/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmc.jfxxlsdiff;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Justo_Casablanca
 */
public class XlsCellValueFactory implements Callback<TableColumn.CellDataFeatures<List<List>, Object>, Object> {

	private static final Logger logger = Logger.getLogger( XlsCellValueFactory.class.getName() );

	@Override
	public Object call( TableColumn.CellDataFeatures<List<List>, Object> df ) {
		List r = df.getValue();
		Iterator ci = r.iterator();

		int i = 0;
		Iterator<TableColumn<List<List>, ?>> tci = df.getTableView().getColumns().iterator();
		while( tci.hasNext() ) {
			TableColumn<List<List>, ?> tc = tci.next();
			if( tc == df.getTableColumn() ) {
				break;
			}
			i++;
		}

		Object ret = null;
		if( i < r.size() ) {
			 ret = r.get( i );
		}

		/*for(int j=0; ci.hasNext(); j++) {
			if( j == i ) {
				ret = ci.next();
				break;
			}
		}*/

		final Object ret2 = ret;

		return new ObservableObjectValue() {
			@Override public Object get() { return getValue(); }
			@Override public Object getValue() { return ret2; }

			@Override public void addListener( ChangeListener cl ) {}
			@Override public void removeListener( ChangeListener cl ) {}
			@Override public void addListener( InvalidationListener il ) {}
			@Override public void removeListener( InvalidationListener il ) {}
		};
	}

}
