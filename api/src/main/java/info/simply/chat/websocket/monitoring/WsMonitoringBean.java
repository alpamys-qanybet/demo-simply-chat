package info.simply.chat.websocket.monitoring;

import info.simply.chat.core.resource.ResourceBean;
import javax.enterprise.context.RequestScoped;
import java.net.URI;
import java.net.URISyntaxException;

@RequestScoped
public class WsMonitoringBean {

    private MonitoringEventSocketClient client;
    private final String wsAddress = "ws://"+ ResourceBean.getHost()+"/api/monitoring";

    private void initWS(String clientId) throws URISyntaxException {
//        System.out.println("REST service: open websocket client at " + wsAddress);
        client = new MonitoringEventSocketClient(new URI(wsAddress + "/"+clientId));
        // add listener
        client.addMessageHandler(new MonitoringEventSocketClient.MessageHandler() {
            public void handleMessage(String message) {
//                System.out.println("messagehandler in REST service - process message " + message);
            }
        });
    }

    public void sendMessageOverSocket(String message, String clientId) {
        if (client == null) {
            try {
                initWS(clientId);
            } catch (URISyntaxException e) {
//                e.printStackTrace();
            }
        }
        client.sendMessage(message);
    }
}
