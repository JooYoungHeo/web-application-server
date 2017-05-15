package http;

import java.io.DataOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by heojooyoung on 2017. 4. 19..
 */
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    DataOutputStream dos;

    public HttpResponse(DataOutputStream dos){
        this.dos = dos;
    }

    public void addHeader(String key, String value) throws IOException{
        dos.writeBytes(key + ": " + value + "\r\n");
    }

    public void forward(String contentType) throws IOException{
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
    }

    public void forwardBody(String body) throws IOException{

    }

    public void response200Header(int contentLength) throws IOException{
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Length: " + contentLength + "\r\n");
    }

    public void responseBody(byte[] body) throws IOException{
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void sendRedirect() throws IOException {
        String redirectUrl = "http://localhost:8080/index.html";
        sendRedirect(redirectUrl);
    }

    public void sendRedirect(String redirectUrl) throws IOException{
        dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Location: " + redirectUrl + "\r\n");
    }

    public void processHeaders() throws IOException{
        dos.writeBytes("\r\n");
    }
}
