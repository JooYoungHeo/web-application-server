package http;

import com.google.common.collect.Maps;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Created by heojooyoung on 2017. 4. 19..
 */
public class HttpRequest {
//    public HttpRequest(InputStream in){
//        InputStreamReader isr = new InputStreamReader(in);
//        BufferedReader br = new BufferedReader(isr);
//
//        try {
//            String line= br.readLine();
//            if (line == null) {
//                return;
//            }
//            String method = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_METHOD);
//            String url = HttpRequestUtils.parseRequestString(line, HttpRequestUtils.CONST_URL);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static Map parseRequest(BufferedReader br) {
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
