package it.unibo.osu.view;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


import it.unibo.osu.controller.MusicControllerImpl;
import it.unibo.osu.controller.MusicControllerImplFactory;
import it.unibo.osu.model.User;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginMenuController extends Resizeable {

	@FXML
    private Scene scene;
	
    @FXML
    private ImageView background;

    @FXML
    private ImageView icon;

    @FXML
    private AnchorPane pane;
    
    @FXML
    private AnchorPane fixedPane;

    @FXML
    private TextField textField;
    private ScaleTransition iconTrans;
    private MusicControllerImpl welcomeMusic;
    private FadeTransition fadeout;
    private FXMLLoader loader;
    private Stage stage;
    private MusicControllerImpl clickSound;
    private MusicControllerImpl loginSound;
    private Timeline musicFadeout;

	
	public void init(Stage stage){
		this.stage = stage;
		loader = new FXMLLoader(this.getClass().getResource("/fxml/MainMenu.fxml"));
		try {
			this.fixedPane.getChildren().add(0,loader.load());
			((MainMenuController) loader.getController()).init(stage);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
    	this.changeResolution(this.fixedPane, toolkit.getScreenSize().getWidth(), toolkit.getScreenSize().getHeight());
		this.setInputHandlers();
		this.initializeTransitions();
		this.initializeSounds();
		this.iconTrans.play();
		this.clickSound = MusicControllerImplFactory.getEffectImpl("/music/loginClickSound.wav");
		this.loginSound = MusicControllerImplFactory.getEffectImpl("/music/loginSound.wav");
		this.musicFadeout = new Timeline(new KeyFrame(Duration.seconds(0),new KeyValue( this.welcomeMusic.getMediaPlayer().volumeProperty(), 1)),
				new KeyFrame(Duration.seconds(3),new KeyValue( this.welcomeMusic.getMediaPlayer().volumeProperty(), 0)));
		this.welcomeMusic.startMusic();
	
//    	System.out.println(toolkit.getScreenSize().getHeight() + " " + toolkit.getScreenSize().getWidth());
    	}
	private void initializeTransitions() {
		this.iconTrans = new ScaleTransition();
		this.iconTrans.setNode(this.icon);
		this.iconTrans.setAutoReverse(true);
		this.iconTrans.setCycleCount(Animation.INDEFINITE);
		this.iconTrans.setDuration(Duration.seconds(1));
		this.iconTrans.setByX(0.1);
		this.iconTrans.setByY(0.1);
		
		this.fadeout = new FadeTransition();
		this.fadeout.setNode(this.fixedPane);
		this.fadeout.setFromValue(1);
		this.fadeout.setToValue(0);
		this.fadeout.setDuration(Duration.seconds(1));
		
		this.fadeout.setOnFinished(e -> {
			this.fixedPane.getChildren().remove(this.pane);
			this.fadeout.setFromValue(0);
			this.fadeout.setToValue(1);
			this.fadeout.setDuration(Duration.seconds(1));
			this.fadeout.setOnFinished(null);
			this.fadeout.playFromStart();
		});
		
	}
	
	private void initializeSounds() {
//		this.welcomeMusic = new MusicControllerImpl("/music/welcome_sound.wav");
		this.welcomeMusic = MusicControllerImplFactory.getSimpleMusicImpl("/music/welcome_sound.wav");
	}

	public void setInputHandlers() {
		this.icon.setOnMouseClicked(e -> {
			this.textField.setVisible(true);
			System.out.println("ok");
		});
		this.textField.setOnMouseClicked(clicked -> {
			this.clickSound.onNotify();
		});
		this.textField.setOnAction(ev -> {
			this.loginSound.onNotify();
			User.setUsername(this.textField.getText());
			this.musicFadeout.play();
			this.fadeout.play();
			((MainMenuController) loader.getController()).startAnimation();
		});
	}
	@Override
	public void changeResolution(Pane pane, double width, double height) {
		super.changeResolution(pane, width, height);
		this.stage.sizeToScene();
	}
	
	
}
