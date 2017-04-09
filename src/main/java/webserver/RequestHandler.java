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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

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
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
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
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));

                DataBase.addUser(user);
                log.debug("db user : {} ", DataBase.findAll());
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
            }
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
