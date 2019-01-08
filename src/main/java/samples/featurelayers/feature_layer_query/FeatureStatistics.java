package samples.featurelayers.feature_layer_query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.QueryParameters.SpatialRelationship;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.StatisticDefinition;
import com.esri.arcgisruntime.data.StatisticRecord;
import com.esri.arcgisruntime.data.StatisticType;
import com.esri.arcgisruntime.data.StatisticsQueryParameters;
import com.esri.arcgisruntime.data.StatisticsQueryResult;

/**
 * ͳ��Ҫ��
 * @author caipch
 * @date 2019��1��8��
 */
public class FeatureStatistics {
	private static StatisticsQueryResult statisticsQueryResult;
	public static void main(String[] args) {
		
		//1.��url�м��ص�ͼ�����Ҫ�ر�
		String url = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";
		FeatureTable usStateTable = new ServiceFeatureTable(url);
		usStateTable.loadAsync();
		
		//2.�����ѯ���ص�ͳ����Ϣ
		StatisticDefinition statDefAvgPop = new StatisticDefinition("POP2007", StatisticType.AVERAGE, "pop2007��ƽ����");
		StatisticDefinition statDefSumPop = new StatisticDefinition("POP2007", StatisticType.SUM, "pop2007���ܺ�");
		StatisticDefinition statDefCount = new StatisticDefinition("OBJECTID", StatisticType.COUNT, "����");
		
		//3.���鵽����Ϣ���ݵ�StatisticsQueryParameters������
		List<StatisticDefinition> statisticDefinitions = new ArrayList<>();
		statisticDefinitions.add(statDefAvgPop);
		statisticDefinitions.add(statDefSumPop);
		statisticDefinitions.add(statDefCount);
		StatisticsQueryParameters statisticsQueryParameters = new StatisticsQueryParameters(statisticDefinitions);
		
		//4.���ռ��ϵ������������ӵ���ѯ������
//		statisticsQueryParameters.setGeometry();
		statisticsQueryParameters.setSpatialRelationship(SpatialRelationship.EQUALS.INTERSECTS);
		
		//5.ָ��Ҫ������ֶβ�ʹ����Խ����������
		statisticsQueryParameters.getGroupByFieldNames().add("SUB_REGION");
		QueryParameters.OrderBy orderSubregion = new QueryParameters.OrderBy("SUB_REGION",QueryParameters.SortOrder.ASCENDING);
		statisticsQueryParameters.getOrderByFields().add(orderSubregion);
		
		//6.ִ�в�ѯ
		ListenableFuture<StatisticsQueryResult> statQueryResultFeature = usStateTable.queryStatisticsAsync(statisticsQueryParameters);
		statQueryResultFeature.addDoneListener(()->{
			try {
				 statisticsQueryResult = statQueryResultFeature.get();
				//7.���������¼�Զ�ȡͳ��ֵ
					LinkedHashMap<String, List<String>> groupedStatistics = new LinkedHashMap<>();
					for(Iterator<StatisticRecord> results = statisticsQueryResult.iterator();results.hasNext(); ) {
						StatisticRecord statisticRecord = results.next();
						if(statisticRecord.getGroup().isEmpty()) {
							List<String> statsWithoutGroup = new ArrayList<>();
							for (Map.Entry<String, Object> stat : statisticRecord.getStatistics().entrySet()) {
								statsWithoutGroup.add(stat.getKey()+":"+stat.getValue());
							}
						}else {
							for (Map.Entry<String, Object > group : statisticRecord.getGroup().entrySet()) {
								List<String> statsForGroup = new ArrayList<>();
								for (Map.Entry<String, Object> stat : statisticRecord.getStatistics().entrySet()) {
									statsForGroup.add(stat.getKey()+":"+stat.getValue());
								}
								groupedStatistics.put(group.getValue().toString(), statsForGroup);
							}
						}
					}
					Iterator iterator = groupedStatistics.entrySet().iterator();
					while (iterator.hasNext()) {
						System.out.println(iterator.next().toString());
						
					}
						
				

			} catch (InterruptedException |ExecutionException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		});
		
	}	
	

}
