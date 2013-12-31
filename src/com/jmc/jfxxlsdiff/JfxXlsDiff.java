/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jmc.jfxxlsdiff;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Justo_Casablanca
 */
public class JfxXlsDiff extends Application {

	private static final Logger logger = Logger.getLogger( JfxXlsDiff.class.getName() );
    
    @Override
    public void start(Stage stage) throws Exception {
		logger.log( Level.INFO, "start( Stage )");
		
		final FXMLLoader loader = new FXMLLoader( getClass().getResource( "MainDiffWindow.fxml" ) );
		Parent root = (Parent) loader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("MainDiffWindow.fxml"));

		stage.addEventHandler(
			WindowEvent.WINDOW_SHOWN,
			new EventHandler<WindowEvent>() {
				@Override
				public void handle( WindowEvent window ) {
					MainDiffWindowController controller = (MainDiffWindowController)
						loader.getController();
					controller.finishInitializingControls();
				}
			} );

        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
