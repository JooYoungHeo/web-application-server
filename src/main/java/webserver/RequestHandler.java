package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

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
//        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
//                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            HttpRequest httpRequest = new HttpRequest(in);

            String method = httpRequest.getMethod();
            String url = httpRequest.getUrl();
            String contentType = httpRequest.getHeader("Accept");
            String loginCookie = httpRequest.getHeader("Cookie");
            boolean loginFlag = false;

            if(loginCookie != null) {
                String[] splitLoginCookie = loginCookie.split("=");
                loginFlag = Boolean.valueOf(splitLoginCookie[1]);
            }

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse httpResponse = new HttpResponse(dos);

            if (method.equals("GET")) {
                if (url.equals("/user/list.html")) {
                    if (!loginFlag) {
                        httpResponse.sendRedirect();
                        httpResponse.processHeaders();
                    } else {
                        log.debug("user: {} ", DataBase.findAll());
                    }
                }
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                httpResponse.response200Header(body.length);
                httpResponse.forward(contentType);
                httpResponse.processHeaders();
                httpResponse.responseBody(body);
            }

            if(method.equals("POST")) {
                if(url.equals("/user/create")) {
                    User user = new User(
                            httpRequest.getParameter("userId"),
                            httpRequest.getParameter("password"),
                            httpRequest.getParameter("name"),
                            httpRequest.getParameter("email"));
                    DataBase.addUser(user);
                    log.debug("db user : {} ", DataBase.findAll());
                    httpResponse.sendRedirect();
                } else if(url.equals("/user/login")) {
                    User user = DataBase.findUserById(httpRequest.getParameter("userId"));
                    if(user != null && user.getPassword().equals(httpRequest.getParameter("password"))) {
                        httpResponse.sendRedirect();
                        httpResponse.addHeader("Set-Cookie", "logined=true");
                        httpResponse.processHeaders();
                    } else {
                        String redirectUrl = "http://localhost:8080/user/login_failed.html";
                        httpResponse.sendRedirect(redirectUrl);
                        httpResponse.addHeader("Set-Cookie", "logined=false");
                        httpResponse.processHeaders();
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
