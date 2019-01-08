package samples.featurelayers.feature_layer_query;


import javafx.application.Platform;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * ��ServiceFeatureTable���Ҫ��
 * 
 * @author caipch
 * @date 2019��1��7��
 */
public class AddFeaturesSample extends Application {

	private MapView mapView;
	private FeatureLayer featureLayer;
	private ServiceFeatureTable featureTable;
	private static final String SERVICE_LAYER_URL =
		      "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			// ����չʾ���
			StackPane stackPane = new StackPane();
			Scene scene = new Scene(stackPane);

			// ��������
			primaryStage.setTitle("��ͼ�����Ҫ��");
			primaryStage.setWidth(600);
			primaryStage.setHeight(500);
			primaryStage.setScene(scene);
			primaryStage.show();

			ArcGISMap map = new ArcGISMap(Basemap.Type.STREETS,40,-95,4);
			mapView = new MapView();
			
			featureTable = new ServiceFeatureTable(SERVICE_LAYER_URL);
			featureLayer = new FeatureLayer(featureTable);
			map.getOperationalLayers().add(featureLayer);
			mapView.setOnMouseClicked(event -> {
		        // check that the primary mouse button was clicked
		        if (event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
		          // create a point from where the user clicked
		          Point2D point = new Point2D(event.getX(), event.getY());

		          // ����Ļ��ת��Ϊ��ͼ��
		          Point mapPoint = mapView.screenToLocation(point);

		          // for a wrapped around map, the point coordinates include the wrapped around value
		          // for a service in projected coordinate system, this wrapped around value has to be normalized
		          //�Լ���ͼ�α�׼��
		          Point normalizedMapPoint = (Point) GeometryEngine.normalizeCentralMeridian(mapPoint);

		          // ����Ҫ����ӵ�featureTable
		          addFeature(normalizedMapPoint, featureTable);
		        }
		      });

			mapView.setMap(map);
			stackPane.getChildren().addAll(mapView);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	/*
	 * ����Ҫ����ӵ�featureTable
	 */
	private void addFeature(Point normalizedMapPoint, ServiceFeatureTable featureTable2) {
		 // create default attributes for the feature
	    Map<String, Object> attributes = new HashMap<>();
	    attributes.put("typdamage", "Destroyed");
	    attributes.put("primcause", "Earthquake");

	    // �����Ժ͵㴴��Ҫ��
	    Feature feature = featureTable.createFeature(attributes, normalizedMapPoint);

	    // check if feature can be added to feature table
	    if (featureTable.canAdd()) {
	      // ������ӣ����÷������Ҫ��
	      featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));
	    } else {
	    	//������ӣ�������Ϣ
	      displayMessage(null, "Cannot add a feature to this feature table");
	    }
	}



	private void displayMessage(String title, String message) {
		Platform.runLater(() -> {
		      Alert dialog = new Alert(AlertType.INFORMATION);
		      dialog.initOwner(mapView.getScene().getWindow());
		      dialog.setHeaderText(title);
		      dialog.setContentText(message);
		      dialog.showAndWait();
		    });
	}

	/**
	 * ����Ӹ���Ӧ�õ�����
	 * @param featureTable2
	 */
	private void applyEdits(ServiceFeatureTable featureTable2) {
		// ���÷���Ӧ�ø���
	    ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
	    editResult.addDoneListener(() -> {
	      try {
	        List<FeatureEditResult> edits = editResult.get();
	        // ������Ƿ�ɹ�
	        if (edits != null && edits.size() > 0) {
	          if (!edits.get(0).hasCompletedWithErrors()) {
	            displayMessage(null, "Feature successfully added");
	          } else {
	            throw edits.get(0).getError();
	          }
	        }
	      } catch (InterruptedException | ExecutionException e) {
	        displayMessage("Exception applying edits on server", e.getCause().getMessage());
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
