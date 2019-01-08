package samples.featurelayers.feature_layer_query;


import javafx.application.Platform;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundFill;
//import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.paint.Paint;
//import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * 从FeatureTable中查询FeatureLayer
 * 
 * @author caipch
 * @date 2019年1月7日
 */
public class FeatureLayerQuerySample extends Application {

	private Alert dialog;

	private MapView mapView;
	private FeatureLayer featureLayer;
	private ServiceFeatureTable featureTable;
	private Point startPoint;
	private ListenableFuture<FeatureQueryResult> tableQueryResult;

	private static final String SERVICE_FEATURE_URL = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer/2";
	private static final int SCALE = 100000000;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			// 创建展示面板
			StackPane stackPane = new StackPane();
			Scene scene = new Scene(stackPane);
//			scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			// 设置属性
			primaryStage.setTitle("查询要素图层");
			primaryStage.setWidth(600);
			primaryStage.setHeight(500);
			primaryStage.setScene(scene);
			primaryStage.show();

			// 创建控制面板
			VBox controlsBox = new VBox(6);
		//	controlsBox.setBackground(new Background(
		//			new BackgroundFill(Paint.valueOf("rgba(0,0,0,0,3)"), CornerRadii.EMPTY, Insets.EMPTY)));
			controlsBox.setPadding(new Insets(10.0));
			controlsBox.setMaxSize(250, 80);
			controlsBox.getStyleClass().add("panel-region");

			// 创建搜索的区域
			Label searchLabel = new Label("搜索:");
			searchLabel.getStyleClass().add("panel-label");
			TextField searchField = new TextField();
			searchField.setMaxWidth(150);
			Button searchButton = new Button("搜索");
			HBox searchBox = new HBox(5);
			searchBox.getChildren().addAll(searchField, searchButton);
			searchBox.setDisable(true);

			// 创建提示对话信息
			dialog = new Alert(AlertType.WARNING);
			dialog.initOwner(stackPane.getScene().getWindow());

			// 创建事件
			searchButton.setOnAction(e -> {
				// 清除选中要素
				featureLayer.clearSelection();
				String stateText = searchField.getText();

				if (stateText.trim().length() > 0) {
					searchForState(stateText);
				} else {
					dialog.setContentText("找不到！");
					dialog.showAndWait();
					mapView.setViewpointCenterAsync(startPoint, SCALE);
				}
			});

			// 在控制面板上添加标签和盒子
			controlsBox.getChildren().addAll(searchLabel, searchBox);

			// 创建点、线
			startPoint = new Point(-11000000, 5000000, SpatialReferences.getWebMercator());
			SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF000000, 1);
			SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0xFFFFCC00, lineSymbol);

			// 1. 从URL创建featurelayertable
			featureTable = new ServiceFeatureTable(SERVICE_FEATURE_URL);
			// 2. 创建要素图层
			featureLayer = new FeatureLayer(featureTable);
			featureLayer.setOpacity(0.8f);

			featureLayer.addDoneLoadingListener(() -> {
				if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
					searchBox.setDisable(false);
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Feature Layer Failed to Load!");
					alert.show();
				}
			});

			// set renderer for feature layer
			featureLayer.setRenderer(new SimpleRenderer(fillSymbol));

			// create a ArcGISMap with basemap topographic
			final ArcGISMap map = new ArcGISMap(Basemap.createTopographic());

			// add feature layer to operational layers
			map.getOperationalLayers().add(featureLayer);

			// create a view for this ArcGISMap
			mapView = new MapView();
			mapView.setMap(map);

			// set viewpoint to the start point
			mapView.setViewpointCenterAsync(startPoint, SCALE);

			// add the map view and control panel to stack pane
			stackPane.getChildren().addAll(mapView, controlsBox);
			StackPane.setAlignment(controlsBox, Pos.TOP_LEFT);
			StackPane.setMargin(controlsBox, new Insets(10, 0, 0, 10));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void searchForState(String stateText) {
		   // 3. 创建一个QueryParameters对象，并使用用户输入的文本中的QueryParameters.setWhereClause（）为其指定where子句。
	    QueryParameters query = new QueryParameters();
	    query.setWhereClause("upper(STATE_NAME) LIKE '" + stateText.toUpperCase() + "'");

	    // 4. 使用ServiceFeatureTable.queryFeaturesAsync（query）在服务要素表上触发查询
	    tableQueryResult = featureTable.queryFeaturesAsync(query);

	    tableQueryResult.addDoneListener(() -> {
	      try {
	        // 从FeatureQueryResult获取结果。
	        FeatureQueryResult result = tableQueryResult.get();
	        // if a state feature was found
	        if (result.iterator().hasNext()) {
	          // get state feature and zoom to it
	          Feature feature = result.iterator().next();
	          Envelope envelope = feature.getGeometry().getExtent();
	          mapView.setViewpointGeometryAsync(envelope, 200);

	          // set the state feature to be selected
	          featureLayer.selectFeature(feature);
	        } else {
	        	
	          Platform.runLater(() -> {
	            dialog.setContentText("State Not Found! Add a valid state name.");
	            dialog.showAndWait();
	            mapView.setViewpointCenterAsync(startPoint, SCALE);
	          });
	        }
	      } catch (Exception e) {
	        // on any error, display the stack trace
	        e.printStackTrace();
	      }
	    });
	}

	@Override
	public void stop() throws Exception {
		if (mapView != null) {
			mapView.dispose();
		}
	}

	public static void main(String[] args) {

		Application.launch(args);
	}

}
