package org.elasticsearch.deletebyquery.plugin;

import org.elasticsearch.deletebyquery.handler.DeleteByQueryHandler;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;

/**
 * Created by yuwei on 2016/12/14
 */
public class DeleteByQueryPlugin extends Plugin {

	public static final String NAME = "delete-by-query";

	public String name() {
		return NAME;
	}

	public String description() {
		return "elasticsearch delete-by-query plugin";
	}

	public void onModule(RestModule restModule) {
		restModule.addRestAction(DeleteByQueryHandler.class);
	}

}
