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
		//1.创建面板
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		
		//2.设置属性
		primaryStage.setTitle("纽约地图");
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
	 * 向地图添加图层
	 */
	private void addTrainlheadLayer() {
		if(mapView!=null) {
			//1.从在线要素服务中保存trailhead数据。
			String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0";
			ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
			//2.FeatureLayer使用服务功能表创建一个表，并将其作为操作层添加到地图中。这将显示底图上方的功能。
			final FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
			ArcGISMap map = mapView.getMap();
			map.getOperationalLayers().add(featureLayer);
			//3.要将视图更改为要素图层要素的中心，请在图层加载完成后将地图视图的视点设置为要素图层的范围：
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
