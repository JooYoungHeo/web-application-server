package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class LoginController extends AbstractController {
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse){
        User user = DataBase.findUserById(httpRequest.getParameter("userId"));
        if (user != null) {
            if (user.login(httpRequest.getParameter("password"))) {
                httpResponse.addHeader("Set-Cookie", "logined=true");
                httpResponse.sendRedirect("/index.html");
            } else {
                httpResponse.sendRedirect("/user/login_failed.html");
            }
        } else {
            httpResponse.sendRedirect("/user/login_failed.html");
        }
    }
}
