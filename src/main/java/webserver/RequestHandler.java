package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import controller.Controller;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            Controller controller = RequestMapping.getController(httpRequest.getUrl());

            if(controller == null){
                String path = getDefaultPath(httpRequest.getUrl());
                httpResponse.forward(path);
            } else {
                controller.service(httpRequest, httpResponse);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path){
        if(path.equals("/")){
            return "/index.html";
        }
        return path;
    }
}
