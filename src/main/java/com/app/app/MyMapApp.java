package com.app.app;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MyMapApp extends Application{

	private MapView mapView;
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//1.创建面板
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		
		//2.设置属性
		primaryStage.setTitle("地图展示");
		primaryStage.setWidth(500);
		primaryStage.setHeight(400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//3.添加底图并将地图显示到地图视图
			//3.1创建定义地图的内容类
		ArcGISMap map = new ArcGISMap(Basemap.createImagery());
			//3.2向其中添加地图，显示imagery映射
		mapView = new MapView();
		mapView.setMap(map);
			//3.3将mapview添加到JavaFX应用程序
		stackPane.getChildren().addAll(mapView);
	}
	
	@Override
	public void stop() {
		if(mapView!=null) {
			mapView.dispose();
			
		}
	}

}
