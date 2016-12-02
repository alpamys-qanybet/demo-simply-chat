package info.simply.chat.core.resource;

public class ResourceBean {
    static final private String host = System.getProperty("host")+":8080";
    public static String getHost() {
        return host;
    }

    static final private String config = "init.config";
    public static String getConfig() {
        return config;
    }
}
