package info.simply.chat.websocket.monitoring;

import info.simply.chat.core.GenericWrapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;


@ServerEndpoint("/monitoring/{client-id}")
public class MonitoringEventSocketMediator {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @Inject
    WsBean wsBean;

    @OnMessage
    public String onMessage(String message, Session session, @PathParam("client-id") String clientId) {
        try {
            JSONObject jObj = new JSONObject(message);
//            System.out.println("received message from client " + clientId);

            for (Session s : peers) {
                try {
                    s.getBasicRemote().sendText(message);
//                    System.out.println("send message to peer ");
                } catch (IOException e) {
//                    e.printStackTrace();
                }

            }
//            session.getBasicRemote().sendText(message);
        } catch (JSONException e) {
//            e.printStackTrace();
        }
//        catch (IOException e) {
//        }
        return "message was received by socket mediator and processed: " + message;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("client-id") String clientId) {
//        System.out.println("mediator: opened websocket channel for client " + clientId);
        peers.add(session);

        try {
//            session.getBasicRemote().sendText("good to be in touch Monitoring " + clientId);
            session.getBasicRemote().sendText(wsBean.messageOnLaunch());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        sendAllAboutOnline(session.getId(), getLogin(session), true);
    }

    @OnClose
    public void onClose(Session session, @PathParam("client-id") String clientId) {
        String login = getLogin(session);
        peers.remove(session);
        sendAllAboutOnline(session.getId(), login, false);
    }

    private String getLogin(Session session) {
        if (session == null)
            return null;
        else {
            if (session.getUserPrincipal() == null)
                return null;
            else {
                return session.getUserPrincipal().getName();
            }
        }
    }

    private void sendAllAboutOnline(String sessionId, String login, boolean isOnlineEvent) {
            List<String> logins = new ArrayList<>();

            for (Session session : peers) {
                String l = getLogin(session);
                if (l != null)
                    logins.add(l);
            }

            String event = isOnlineEvent ? WsBean.Event.ON_WS_OPEN.toString() : WsBean.Event.ON_WS_CLOSE.toString();
            StringBuilder sb = new StringBuilder("{\"event\":\""+event+"\",");
            if (isOnlineEvent)
                sb.append("\"online\"");
            else
                sb.append("\"offline\"");
            sb.append(":\""+login+"\"}");

            String onOrOffline = sb.toString();

            for (Session s : peers) {
                try {
                    if (s.getId().equals(sessionId)) {
                        JSONObject json = new JSONObject();
                        json.put("event", event);
                        json.put("online", new JSONArray(GenericWrapper.wrap(logins).toString()));
                        s.getBasicRemote().sendText(json.toString());
                    } else
                        s.getBasicRemote().sendText(onOrOffline);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
    }
}