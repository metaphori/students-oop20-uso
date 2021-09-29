package it.unibo.osu.view;

import java.util.ArrayList;
import java.util.List;


import it.unibo.osu.controller.HitActionObserver;
import it.unibo.osu.controller.HitActionSubject;
import it.unibo.osu.controller.Observer;
import it.unibo.osu.controller.Subject;
import it.unibo.osu.model.GameModel;
import it.unibo.osu.model.GamePoints;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.EventDispatcher;
import javafx.print.PageLayout;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class HitcircleViewImpl implements HitcircleView, HitActionSubject {
	private Circle innerCircle;
	private Circle outerCircle;
	private double circleSize;
	private double overallDifficulty; //<--
	private double approachRate;
	private ScaleTransition scaleOuterCircle;
	private FadeTransition fadeInnerCircle;
	private FadeTransition fadeOuterCircle;
	private ParallelTransition pararallelTrans;
	private ScaleTransition finalScaleTrans;
	private FadeTransition finalFadeOutTrans;
	private ParallelTransition finalParallelTrans;
	private double x;
	private double y;
	private double fadeInTime;
	private double approachTime;
	private List<HitActionObserver> observers = new ArrayList<>();
	
	
	
	public HitcircleViewImpl(String innerCircleUrl, String outerCircleUrl, double circleSize, double overallDifficulty,
			double approachRate,double x, double y) {
		
//		this.outerCircle = new ImageView(new Image(this.getClass().getResource(outerCircleUrl).toString()));
//		this.innerCircle = new ImageView(new Image(this.getClass().getResource(innerCircleUrl).toString()));
		this.circleSize = circleSize;
		this.innerCircle = new Circle();
		this.outerCircle = new Circle();
		this.overallDifficulty = overallDifficulty;
		this.approachRate = approachRate;
		this.scaleOuterCircle = new ScaleTransition();
		this.fadeInnerCircle = new FadeTransition();
		this.fadeOuterCircle = new FadeTransition();
		this.pararallelTrans = new ParallelTransition(this.fadeInnerCircle,this.fadeOuterCircle);
		this.finalScaleTrans = new ScaleTransition();
		this.finalFadeOutTrans = new FadeTransition();
		this.finalParallelTrans = new ParallelTransition(this.finalScaleTrans,this.finalFadeOutTrans);
		this.x = x;
		this.y = y;
		this.fadeInTime = this.getFadeInTime();
		this.approachTime = this.getApproachTime();
		this.init();
		this.setInputHandlers();
	}

	
	private void init() {
		double innerCircleSize = (109 - 9 * this.circleSize) / 2;
		double outerCircleSize = innerCircleSize * 2;
		this.innerCircle.setCenterX(x);
		this.innerCircle.setCenterY(y);
		this.outerCircle.setCenterX(x);
		this.outerCircle.setCenterY(y);
		this.innerCircle.setRadius(innerCircleSize);
		this.innerCircle.toFront();
		this.outerCircle.setRadius(outerCircleSize);
		this.innerCircle.setFill(Color.RED);
		this.outerCircle.setFill(Color.TRANSPARENT);
		this.innerCircle.setStroke(Color.WHITESMOKE);
		this.innerCircle.setStrokeWidth(this.innerCircle.getRadius()*0.10);
		this.outerCircle.setStrokeWidth(this.outerCircle.getRadius()*0.10);
		this.outerCircle.setStroke(Color.WHITESMOKE);
		
//		this.innerCircle.setFitHeight(innerCircleSize);
//		this.innerCircle.setFitWidth(innerCircleSize);
//		this.outerCircle.setFitHeight(outerCircleSize);
//		this.outerCircle.setFitWidth(outerCircleSize);	
		
//		this.innerCircle.setLayoutX(this.x - this.innerCircle.getFitWidth() / 2);
//		this.innerCircle.setLayoutY(this.y - this.innerCircle.getFitHeight() / 2);
//		this.outerCircle.setLayoutX(this.x - this.outerCircle.getFitWidth() / 2);
//		this.outerCircle.setLayoutY(this.y - this.outerCircle.getFitHeight() / 2);
		
		this.scaleOuterCircle.setNode(this.outerCircle);
		this.scaleOuterCircle.setToX(0.5);
		this.scaleOuterCircle.setToY(0.5);
		this.scaleOuterCircle.setCycleCount(1);
		this.scaleOuterCircle.setDuration(Duration.millis(this.approachTime));
		
		this.fadeInnerCircle.setNode(this.innerCircle);
		this.fadeInnerCircle.setCycleCount(1);
		this.fadeInnerCircle.setFromValue(0);
		this.fadeInnerCircle.setToValue(1);
		this.fadeInnerCircle.setDuration(Duration.millis(this.fadeInTime));
		
		this.fadeOuterCircle.setNode(this.outerCircle);
		this.fadeOuterCircle.setCycleCount(1);
		this.fadeOuterCircle.setFromValue(0);
		this.fadeOuterCircle.setToValue(1);
		this.fadeOuterCircle.setDuration(Duration.millis(this.fadeInTime));
		this.pararallelTrans.setOnFinished(e -> this.scaleOuterCircle.play());
		
		this.finalScaleTrans.setNode(this.innerCircle);
		this.finalScaleTrans.setDuration(Duration.millis(500));
		this.finalScaleTrans.setCycleCount(1);
		this.finalScaleTrans.setToX(1.5);
		this.finalScaleTrans.setToY(1.5);
		
		this.finalFadeOutTrans.setDuration(Duration.millis(500));
		this.finalFadeOutTrans.setFromValue(1);
		this.finalFadeOutTrans.setToValue(0);
		this.finalFadeOutTrans.setCycleCount(1);
		this.finalFadeOutTrans.setNode(this.innerCircle);
		
		
	}

	@Override
	public List<Circle> getChildren() {
		List<Circle> list = new ArrayList<>();
		list.add(this.outerCircle);
		list.add(this.innerCircle);
		return list;
	}


	@Override
	public ParallelTransition getParallelTransition() {
		return this.pararallelTrans;
	}
	
	private double getFadeInTime() {
		if(this.approachRate < 5) {
			return 800 + 400 * (5 - this.approachRate) / 5;
		} else if ( this.approachRate == 5) {
			return 800;
		} else {
			return 800 - 500 * (this.approachRate - 5) / 5;
		}
	}
	
	private double getApproachTime() {
		double preempt;
		if( this.approachRate < 5) {
			preempt = 1200 + 600 * (5 - this.approachRate) / 5;
		} else if(this.approachRate == 5) {
			preempt = 1200;
		} else {
			preempt = 1200 - 750 * (this.approachRate - 5) / 5;
		}
		return preempt - this.getFadeInTime();
	}
	
	@Override
	public void setInputHandlers() {
		
		
		this.innerCircle.setOnMouseClicked(e -> {
			double time = this.scaleOuterCircle.getTotalDuration().toMillis() - this.scaleOuterCircle.getCurrentTime().toMillis();
			this.scaleOuterCircle.stop();
			this.finalParallelTrans.play();
			GamePoints points = this.getHitWindowScore(time);
			this.notifyObs(points);	
		});
		this.finalParallelTrans.setOnFinished(e->{
			this.innerCircle.setVisible(false);
			this.outerCircle.setVisible(false);
		});
		
		this.scaleOuterCircle.setOnFinished(e -> {
			this.innerCircle.setVisible(false);
			this.outerCircle.setVisible(false);
			this.notifyObs(GamePoints.MISS);
		});	
	}

	@Override
	public void notifyObs(GamePoints points) {
		this.observers.forEach(x -> x.onNotify(points));
	}

	@Override
	public void addObserver(HitActionObserver obs) {
		this.observers.add(obs);
	}

	@Override
	public void removeObserver(HitActionObserver obs) {
		this.observers.remove(obs);
	}

	private GamePoints getHitWindowScore(double time) {
		if(time <= 160 - 12 *  this.overallDifficulty) {
			return GamePoints.PERFECT;
		} else if(time <= 280 - 16 * this.overallDifficulty) {
			return GamePoints.GREAT;
		} else if(time <= 400 - 20 * this.overallDifficulty) {
			return GamePoints.OK;
		} else {
			return GamePoints.MISS;
		}
	}
}
