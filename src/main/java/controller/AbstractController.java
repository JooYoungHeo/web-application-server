package controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        String method = httpRequest.getMethod();

        if(method.equals("GET")){
            doGet(httpRequest, httpResponse);
        } else if(method.equals("POST")){
            doPost(httpRequest, httpResponse);
        }
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {}

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {}
}
