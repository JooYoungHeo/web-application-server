package webserver;

import com.google.common.collect.ImmutableMap;
import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

import java.util.Map;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class RequestMapping {
    private static Map<String, Controller> controllerMap = ImmutableMap.<String, Controller>builder()
            .put("/user/create", new CreateUserController())
            .put("/user/list", new ListUserController())
            .put("/user/login", new LoginController())
            .build();

    public static Controller getController(String url){
        return controllerMap.get(url);
    }
}
