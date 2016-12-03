package info.simply.chat.core;

import info.simply.chat.core.resource.ResourceBean;
import info.simply.chat.user.UserBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

@ApplicationScoped
public class Config {

    @Inject
    UserBean userBean;

    public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(ResourceBean.getConfig()).getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                StringTokenizer str = new StringTokenizer(scanner.nextLine(), ":");
                userBean.add(str.nextToken(), str.nextToken(), str.nextToken(), true);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) ServletContext context) {
        // Do stuff during webapp's shutdown.
    }
}