package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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

            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            String line= br.readLine();

            if (line == null) {
                return;
            }

            String method = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_METHOD);
            String url = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_URL);

            Map<String, String> map = HttpRequest.parseRequest(br);

            String contentType = map.get("Accept");
            String loginCookie = map.get("Cookie");
            boolean loginFlag = false;
            if(loginCookie != null) {
                String[] splitLoginCookie = loginCookie.split("=");
                loginFlag = Boolean.valueOf(splitLoginCookie[1]);
            }

            DataOutputStream dos = new DataOutputStream(out);

            if (method.equals("GET")) {
                if (url.equals("/user/list.html")) {
                    if (!loginFlag) {
                        HttpResponse.response302Header(dos);
                    } else {
                        log.debug("user: {} ", DataBase.findAll());
                    }
                }
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                HttpResponse.response200Header(dos, body.length, contentType);
                HttpResponse.responseBody(dos, body);
            }

            if(method.equals("POST")) {
                int contentLength = Integer.valueOf(map.get("Content-Length"));
                String postData = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(postData);

                if(url.equals("/user/create")) {
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    DataBase.addUser(user);
                    log.debug("db user : {} ", DataBase.findAll());
                    HttpResponse.response302Header(dos);
                } else if(url.equals("/user/login")) {
                    User user = DataBase.findUserById(params.get("userId"));
                    String redirectUrl = "http://localhost:8080/index.html";
                    if(user != null && user.getPassword().equals(params.get("password"))) {
                        HttpResponse.response302Login(dos, redirectUrl, true);
                    } else {
                        redirectUrl = "http://localhost:8080/user/login_failed.html";
                        HttpResponse.response302Login(dos, redirectUrl, false);
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
