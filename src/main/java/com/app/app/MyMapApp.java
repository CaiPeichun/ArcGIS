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
		//1.�������
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		
		//2.��������
		primaryStage.setTitle("��ͼչʾ");
		primaryStage.setWidth(500);
		primaryStage.setHeight(400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//3.��ӵ�ͼ������ͼ��ʾ����ͼ��ͼ
			//3.1���������ͼ��������
		ArcGISMap map = new ArcGISMap(Basemap.createImagery());
			//3.2��������ӵ�ͼ����ʾimageryӳ��
		mapView = new MapView();
		mapView.setMap(map);
			//3.3��mapview��ӵ�JavaFXӦ�ó���
		stackPane.getChildren().addAll(mapView);
	}
	
	@Override
	public void stop() {
		if(mapView!=null) {
			mapView.dispose();
			
		}
	}

}
