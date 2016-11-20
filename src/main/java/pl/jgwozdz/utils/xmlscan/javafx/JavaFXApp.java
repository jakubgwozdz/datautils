package pl.jgwozdz.utils.xmlscan.javafx;

/**
 *
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import pl.jgwozdz.utils.xmlscan.javafx.model.MainAppModel;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class JavaFXApp extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    private MainAppController rootController;
    private MainAppModel mainAppModel = new MainAppModel();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("XML Scanner");

//        mainAppModel.fileChooserModel.selectedFileProperty().addListener((observable, oldValue, newValue) -> System.out.println("selectedFile changed from `" + oldValue + "' to '" + newValue + "'"));

        initRootLayout();
        mainAppModel.fileChooserModel.setDirectoryToScan(Paths.get("C:\\Users\\gwozd_000\\Downloads"));
    }

//    public void initFileLoader() {
//        try {
//            // Load person overview.
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(JavaFXApp.class.getResource("view/FileChooserModel.fxml"));
//            AnchorPane fileLoader = (AnchorPane) loader.load();
//
//            // Set person overview into the center of root layout.
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            URL resource = JavaFXApp.class.getResource("view/XMLScan.fxml");
//            System.out.println("Opening file " + resource);
            loader.setLocation(resource);
            Region rootLayout = loader.load();
            rootController = loader.getController();
            rootController.setModel(mainAppModel);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
