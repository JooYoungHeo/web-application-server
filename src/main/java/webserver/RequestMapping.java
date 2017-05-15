package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class RequestMapping {
    private static Map<String, Controller> controllerMap = new HashMap<String, Controller>();

    void tmp(){
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/list", new ListUserController());
        controllerMap.put("/user/login", new LoginController());
    }

    public Controller getController(String url){
        return controllerMap.get(url);
    }
}
