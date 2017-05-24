package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class ListUserController extends AbstractController {
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse){
        if (!isLogin(httpRequest.getHeader("Cookie"))) {
            httpResponse.sendRedirect("/user/login.html");
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        httpResponse.forwardBody(sb.toString());
    }

    private boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        return value != null && Boolean.parseBoolean(value);
    }
}
