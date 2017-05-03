package http;

import com.google.common.collect.Maps;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Created by heojooyoung on 2017. 4. 19..
 */
public class HttpRequest {
    private String method;
    private String url;
    private Map headerMap;
    private Map bodyMap;

    public HttpRequest(InputStream in) throws IOException{
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);

        String line = br.readLine();

        method = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_METHOD);
        url = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_URL);
        headerMap = parseRequest(br);

        if(method.equals("POST")) {
            int contentLength = Integer.parseInt((String) headerMap.get("Content-Length"));
            String postData = IOUtils.readData(br, contentLength);
            bodyMap = HttpRequestUtils.parseQueryString(postData);
        }
    }

    public String getMethod(){
        return method;
    }

    public String getUrl(){
        return url;
    }

    public String getHeader(String fileName){
        return (String) headerMap.get(fileName);
    }

    public String getParameter(String paramName){
        return (String) bodyMap.get(paramName);
    }

    private static Map parseRequest(BufferedReader br) {
        Map<String, String> map = Maps.newHashMap();
        try{
            while(true) {
                String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                map.put(pair.getKey(), pair.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
