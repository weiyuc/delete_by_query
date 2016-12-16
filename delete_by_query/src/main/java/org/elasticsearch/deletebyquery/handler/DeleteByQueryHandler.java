package org.elasticsearch.deletebyquery.handler;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.deletebyquery.action.DeleteByQueryAction;
import org.elasticsearch.deletebyquery.action.DeleteByQueryAction.DeleteByQueryResult;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestRequest.Method;

import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

/**
 * Created by yuwei on 2016/12/14
 */
public class DeleteByQueryHandler extends BaseRestHandler {

	@Inject
	public DeleteByQueryHandler(Settings settings, RestController controller, Client client) {
		super(settings, controller, client);
		controller.registerHandler(Method.DELETE, "/{index}/{type}/_query", this);
		logger.info("regedit RestHandler named : DeleteByQueryHandler");
	}

	@Override
	public void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {

		if (!request.hasContent()) 
			return;
		
		String index = request.param("index");
		String type = request.param("type");
		
		String query = new String(request.content().array(), "utf-8");
		
		logger.info("query :" + query);
		
		DeleteByQueryAction action = new DeleteByQueryAction(client, index, type, query.toString());
		DeleteByQueryResult result = action.action();
		
		RestResponse response = new BytesRestResponse(RestStatus.OK, "text/json; charset=UTF-8", result.toString());
		channel.sendResponse(response);
	}

}
