package controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class ListUserController extends AbstractController {
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse){

    }

    public boolean isLogin(String token){
        return true;
    }
}
