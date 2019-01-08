package com.app.app;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.internal.jni.ap;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MyMapApp2 extends Application{

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
		primaryStage.setTitle("ŦԼ��ͼ");
		primaryStage.setWidth(500);
		primaryStage.setHeight(400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		mapView = new MapView();
		setupMap();
//		addTrainlheadLayer();
		stackPane.getChildren().add(mapView);
	}
	
	
	@Override
	public void stop() {
		if(mapView!=null) {
			mapView.dispose();
			
		}
	}
	
	private void setupMap() {
		if(mapView!=null) {
			Basemap.Type basemaptype = Basemap.Type.STREETS_VECTOR;
			double latitude = 34.05293;
			double longitude = -118.24368;
			int levelOfDetail = 11;
			ArcGISMap map = new ArcGISMap(basemaptype, latitude, longitude, levelOfDetail);
			mapView.setMap(map);
		}	
	}
	
	/**
	 * ���ͼ���ͼ��
	 */
	private void addTrainlheadLayer() {
		if(mapView!=null) {
			//1.������Ҫ�ط����б���trailhead���ݡ�
			String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0";
			ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
			//2.FeatureLayerʹ�÷����ܱ���һ������������Ϊ��������ӵ���ͼ�С��⽫��ʾ��ͼ�Ϸ��Ĺ��ܡ�
			final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
			ArcGISMap map = mapView.getMap();
			map.getOperationalLayers().add(featureLayer);
			//3.Ҫ����ͼ����ΪҪ��ͼ��Ҫ�ص����ģ�����ͼ�������ɺ󽫵�ͼ��ͼ���ӵ�����ΪҪ��ͼ��ķ�Χ��
			featureLayer.addDoneLoadingListener(() -> {
				  if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
				    mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
				  } else {
				    featureLayer.getLoadError().getCause().printStackTrace();
				  }
				});
		}
	}

}
