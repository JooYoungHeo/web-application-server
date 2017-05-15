package controller;

import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by heojooyoung on 2017. 5. 15..
 */
public interface Controller {
    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
