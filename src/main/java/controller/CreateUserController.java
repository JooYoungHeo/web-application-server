package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse){
        User user = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"),
                httpRequest.getParameter("name"), httpRequest.getParameter("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
        httpResponse.sendRedirect("/index.html");
    }
}
