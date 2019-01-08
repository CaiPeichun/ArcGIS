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
 * 统计要素
 * @author caipch
 * @date 2019年1月8日
 */
public class FeatureStatistics {
	private static StatisticsQueryResult statisticsQueryResult;
	public static void main(String[] args) {
		
		//1.从url中加载地图服务的要素表
		String url = "https://sampleserver6.arcgisonline.com/arcgis/rest/services/Census/MapServer/3";
		FeatureTable usStateTable = new ServiceFeatureTable(url);
		usStateTable.loadAsync();
		
		//2.定义查询返回的统计信息
		StatisticDefinition statDefAvgPop = new StatisticDefinition("POP2007", StatisticType.AVERAGE, "pop2007的平均数");
		StatisticDefinition statDefSumPop = new StatisticDefinition("POP2007", StatisticType.SUM, "pop2007的总和");
		StatisticDefinition statDefCount = new StatisticDefinition("OBJECTID", StatisticType.COUNT, "数量");
		
		//3.将查到的信息传递到StatisticsQueryParameters对象中
		List<StatisticDefinition> statisticDefinitions = new ArrayList<>();
		statisticDefinitions.add(statDefAvgPop);
		statisticDefinitions.add(statDefSumPop);
		statisticDefinitions.add(statDefCount);
		StatisticsQueryParameters statisticsQueryParameters = new StatisticsQueryParameters(statisticDefinitions);
		
		//4.将空间关系等其他属性添加到查询参数中
//		statisticsQueryParameters.setGeometry();
		statisticsQueryParameters.setSpatialRelationship(SpatialRelationship.EQUALS.INTERSECTS);
		
		//5.指定要分组的字段并使用其对结果进行排序。
		statisticsQueryParameters.getGroupByFieldNames().add("SUB_REGION");
		QueryParameters.OrderBy orderSubregion = new QueryParameters.OrderBy("SUB_REGION",QueryParameters.SortOrder.ASCENDING);
		statisticsQueryParameters.getOrderByFields().add(orderSubregion);
		
		//6.执行查询
		ListenableFuture<StatisticsQueryResult> statQueryResultFeature = usStateTable.queryStatisticsAsync(statisticsQueryParameters);
		statQueryResultFeature.addDoneListener(()->{
			try {
				 statisticsQueryResult = statQueryResultFeature.get();
				//7.迭代结果记录以读取统计值
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
