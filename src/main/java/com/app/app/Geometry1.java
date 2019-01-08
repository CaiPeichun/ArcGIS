package com.app.app;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
/**
 * �ϲ���������ͼ�Σ���;��Σ�
 * @author caipch
 * @date 2019��1��7��
 */
public class Geometry1 extends Application{

	private MapView mapView;
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
				
		// 1.����һ����
		Point ptWgs84 = new Point(34.056295, -117.195800, 414, SpatialReferences.getWgs84());
		//2.����һ������
		Envelope envelope = new Envelope(-123.0, 33.5, -101.0, 48.0, SpatialReferences.getWgs84());
		//3.�ϲ�
		Envelope res = GeometryEngine.combineExtents(ptWgs84, envelope);
		
		System.out.println(res.toString());
		System.out.println("����"+GeometryEngine.union(ptWgs84, envelope));
		
	}
	
	@Override
	public void stop() {
		if(mapView!=null) {
			mapView.dispose();
			
		}
	}

}
