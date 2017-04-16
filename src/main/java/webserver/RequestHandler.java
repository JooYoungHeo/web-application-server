package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
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

            if(method.equals("GET")) {
                boolean isLogin = false;
                String accept = null;
                while(true) {
                    line = br.readLine();
                    HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                    boolean flag = HttpRequestUtils.getCookie(pair);
                    String tmpAccept = HttpRequestUtils.getAccept(pair);

                    if(flag != false) {
                        isLogin = flag;
                    }

                    if(tmpAccept != null) {
                        accept = tmpAccept;
                    }

                    if(line.equals("")) {
                        break;
                    }
                }
                if(url.equals("/user/list.html")) {
                    if(!isLogin) {
                        DataOutputStream dos = new DataOutputStream(out);
                        response302Header(dos);
                    } else {
                        log.debug("user: {} ", DataBase.findAll());
                        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                        DataOutputStream dos = new DataOutputStream(out);
                        responseTmp200Header(dos, body.length, accept);
                        responseBody(dos, body);
                    }

                }
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                responseTmp200Header(dos, body.length, accept);
                responseBody(dos, body);
            }

            if(method.equals("POST")) {
                int contentLength = 0;

                while(true) {
                    line = br.readLine();
                    HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                    int length = HttpRequestUtils.getContentLength(pair);

                    if(length != 0) {
                        contentLength = length;
                    }
                    if(line.equals("")) {
                        break;
                    }
                }

                String postData = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(postData);

                if(url.equals("/user/create")) {
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    DataBase.addUser(user);
                    log.debug("db user : {} ", DataBase.findAll());
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos);
                } else if(url.equals("/user/login")) {
                    User user = DataBase.findUserById(params.get("userId"));
                    DataOutputStream dos = new DataOutputStream(out);
                    String redirectUrl = "http://localhost:8080/index.html";
                    if(user != null && user.getPassword().equals(params.get("password"))) {
                        response302Login(dos, redirectUrl, true);
                    } else {
                        redirectUrl = "http://localhost:8080/user/login_failed.html";
                        response302Login(dos, redirectUrl, false);
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Login(DataOutputStream dos, String redirectUrl, boolean successFlag) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + successFlag);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + "http://localhost:8080/index.html");
            dos.writeBytes("\r\n");
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseTmp200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
