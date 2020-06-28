package com.x.server.console.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class VoteCenterEvent implements Event {

	private static Logger logger = LoggerFactory.getLogger(VoteCenterEvent.class);

	public final String type = Event.TYPE_VOTECENTER;

	public void execute() throws Exception {

		List<Entry<String, CenterServer>> list = Config.nodes().centerServers().orderedEntry();

		for (Entry<String, CenterServer> entry : list) {
			try {

				ActionResponse response = CipherConnectionAction.get(false,
						Config.url_x_program_center_jaxrs(entry, "echo"));
				JsonElement jsonElement = response.getData(JsonElement.class);
				if (null != jsonElement && (!jsonElement.isJsonNull())) {
					if ((!StringUtils.equals(Config.resource_node_centersPirmaryNode(), entry.getKey()))
							|| (!Objects.equals(Config.resource_node_centersPirmaryPort(), entry.getValue().getPort()))
							|| (!Objects.equals(Config.resource_node_centersPirmarySslEnable(),
									entry.getValue().getSslEnable()))) {
						logger.warn("pirmary center set as: {}, in {}.", entry.getKey(), this.nodes(list));
						Config.resource_node_centersPirmaryNode(entry.getKey());
						Config.resource_node_centersPirmaryPort(entry.getValue().getPort());
						Config.resource_node_centersPirmarySslEnable(entry.getValue().getSslEnable());
					}
					return;
				}
			} catch (Exception e) {
				// logger.warn("failed to connect center: {}, port: {}, sslEnable: {}.",
				// entry.getKey(),
				// entry.getValue().getPort(), entry.getValue().getSslEnable());
			}
		}

	}

	private String nodes(List<Entry<String, CenterServer>> list) {
		List<String> os = new ArrayList<>();
		for (Entry<String, CenterServer> entry : list) {
			os.add(entry.getKey());
		}
		return StringUtils.join(os, ",");
	}

}