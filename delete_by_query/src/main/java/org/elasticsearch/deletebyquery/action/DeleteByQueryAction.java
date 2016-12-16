package org.elasticsearch.deletebyquery.action;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.deletebyquery.plugin.DeleteByQueryPlugin;
import org.elasticsearch.search.SearchHit;

/**
 * Created by yuwei on 2016/12/14
 */
public class DeleteByQueryAction  {
	
	private static final ESLogger LOGGER = ESLoggerFactory.getLogger(DeleteByQueryPlugin.NAME);
	
	private Client client;
	private String index;
	private String type;
	
	/**
	 * query text
	 */
	private String query;
	
	/**
	 * bulk size
	 */
	private int defaultBatchSize = 10000;
	
	public DeleteByQueryAction(Client client, String index, String type, String query) {
		this.client = client;
		this.index = index;
		this.type = type;
		this.query = query;
	}
	
	public DeleteByQueryResult action() {
		
		DeleteByQueryResult result = new DeleteByQueryResult();
		
		SearchResponse scrollResp = client.prepareSearch(index).setTypes(type)
				.setScroll(new TimeValue(60000)).setSize(defaultBatchSize).setQuery(query)
				.execute().actionGet();
		long total = scrollResp.getHits().getTotalHits();
		
		result.setTotal(total);
		
		long fail = 0;
		
		LOGGER.info("total " + total + " rows ...");
		
		while (total > 0) {
			BulkRequestBuilder requestBuilder = client.prepareBulk().setRefresh(true);

			for (SearchHit hit : scrollResp.getHits().getHits())
				requestBuilder.add(new DeleteRequest(index, type, hit.getId()));

			BulkResponse reponse = requestBuilder.execute().actionGet();
			if (reponse.hasFailures()) {
				for (BulkItemResponse item : reponse) {
					if (item.isFailed()) {
						fail ++ ;
						LOGGER.warn(item.getFailureMessage());
					}
				}
			}
			
			total = total - reponse.getItems().length;
			LOGGER.info("has removed " + reponse.getItems().length + " rows, remain " + total + " rows ...");
			
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute()
					.actionGet();

			if (scrollResp.getHits().getHits().length == 0)
				break;
		}
		
		result.setFail(fail);
		
		return result;
		
	}
	
	public class DeleteByQueryResult {
		
		private DeleteByQueryResult() {}
		
		private long total;
		
		private long fail;

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getFail() {
			return fail;
		}

		public void setFail(long fail) {
			this.fail = fail;
		}
		
		public String toString() {
			return "{\"total\" : " + total + ", \"fail\" : " + fail + "}";
		}
		
	}
	
}
