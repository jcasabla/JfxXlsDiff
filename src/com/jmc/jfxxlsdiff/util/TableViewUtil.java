/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jmc.jfxxlsdiff.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;

/**
 *
 * @author Justo_Casablanca
 */
public class TableViewUtil {

	private static final Logger logger = Logger.getLogger( TableViewUtil.class.getName() );

	public static void syncScrolling( TableView table1, TableView table2 ) {
		logger.log( Level.INFO, "syncScrolling( TableView, TableView )" );

		syncScrolling( findScrollBar( table1, Orientation.HORIZONTAL ),
					   findScrollBar( table2, Orientation.HORIZONTAL ) );

		syncScrolling( findScrollBar( table1, Orientation.VERTICAL ),
					   findScrollBar( table2, Orientation.VERTICAL ) );
	}

	public static void syncScrolling( ScrollBar scroll1, ScrollBar scroll2 ) {
		logger.log( Level.INFO, "syncScrolling( ScrollBar, ScrollBar )" );

		if( ( scroll1 == null ) || ( scroll2 == null ) ) {
			logger.log(
				Level.WARNING,
				"calling syncScrolling( ScrollBar[{0}], ScrollBar[{1}] ) too early ?",
				new Object[] { scroll1, scroll2 }
			);
		} else {
			scroll1.valueProperty().bindBidirectional( scroll2.valueProperty() );
			//scroll1.valueProperty().addListener( new ScrollSync( scroll2 ) );
			//scroll2.valueProperty().addListener( new ScrollSync( scroll1 ) );
		}
	}

	/*
	public static void configureRowHeaders( TableView table ) {
		logger.log( Level.INFO, "lockRowHeaders( TableView )" );

		TableView rowHeaders = new TableView();
		rowHeaders.getColumns().add( new TableColumn( "#" ) );

		SplitPane g = findSplitPane( table );

		g.getItems().get( 0 ).getChildren().add( 0, rowHeaders );

		table.setItems( FXCollections.observableArrayList( "Tom", "Dick", "Harry" ) );
		rowHeaders.setItems( FXCollections.observableArrayList( "1", "2", "3" ) );
	}

	private static SplitPane findSplitPane( Parent n ) {
		SplitPane g = null;
		Parent p = n;

		while( p != null ) {
			if( p instanceof SplitPane ) {
				break;
			} else {
				p = p.getParent();
			}
		}

		if( p != null ) {
			g = (SplitPane) p;
		}

		return g;
	}
	*/

	private static ScrollBar findScrollBar( TableView tv, Orientation or ) {
		ScrollBar sb = null;

		for( Node n : tv.lookupAll( ".scroll-bar" ) ) {
			if( n instanceof ScrollBar ) {
				ScrollBar bar = (ScrollBar) n;
				if( bar.getOrientation() == or ) {
					sb = bar;
					break;
				}
			}
		}

		return sb;
	}

	/*private static class ScrollSync implements ChangeListener<Number> {
		private ScrollBar targetSB = null;

		private ScrollSync( ScrollBar targetSB ) {
			this.targetSB = targetSB;
		}

		@Override
		public void changed( ObservableValue<? extends Number> ov, Number oldVal, Number newVal ) {
			targetSB.valueProperty().setValue( newVal );
		}
	};*/
}
