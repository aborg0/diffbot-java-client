package com.diffbot.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;

/**
 * Created by wadi chemkhi on 02/01/14.
 * Email : wadi.chemkhi@gmail.com
 */
public class DiffbotHttpClient {

    private String token;
    private String version;

    /**
     * @param token the access token for the diffbot API
     */
    public DiffbotHttpClient(String token) {
        this.token = token;
        this.version = "2";
    }

    public DiffbotHttpClient(String token, String version) {
        this.version = version;
        this.token = token;
    }

    /**
     * @param url the url to be processed using the diffbot articles API.
     * @return String representing the json data of an article according to the provided url.
     */
    public String getArticle(String url) throws IOException {
        return getJson("article", url);
    }
    public String getArticle(String url,Map<String,String> params) throws IOException {
        return getJson("article", url,params);
    }

    /**
     * @param url the url to be processed using the diffbot products API.
     * @return String representing the json data of an array of products according to the provided url.
     */
    public String getProducts(String url,Map<String,String> params) throws IOException {
        return getJson("product", url,params);

    }
    public String getProducts(String url) throws IOException {
        return getJson("product", url);

    }
    public String getJson(String api, String url) throws IOException {
        return getJson( api,  url, null);
    }
    public String getJson(String api, String url, Map<String, String> params) throws IOException {

        URI uri = null;
        URIBuilder ub = new URIBuilder()
                .setScheme("http")
                .setHost("api.diffbot.com")
                .setPath("/v" + version + "/" + api);

        ub.addParameter("token", token);
        ub.addParameter("url", url);
//        StringBuilder query=new StringBuilder()
//                .append("token=").append(token)
//                //TODO URLEncode?
//                .append("&url=").append(url);

        if (params!=null){
        	for (Entry<String, String> entry: params.entrySet()) {
        		ub.addParameter(entry.getKey(), entry.getValue());
        	}
//        Iterator<String> it = params.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            //TODO URLEncode?
//            query.append("&").append(key).append("=").append(params.get(key));
//        }
        }
//        ub.setQuery(query.toString());
        try {
            String string = ub.build().toString();
			uri = new URI(string.replaceAll("%25([\\da-fA-F]{2})", "%$1"));
            ////TODO uh?
            //System.out.println(uri.toString());
        } catch (URISyntaxException e) {
        	throw new RuntimeException(e);
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = null;
        String json = null;

        response = httpClient.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent(), "utf-8"), 2048);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        json = sb.toString();
        httpGet.releaseConnection();
        //response.close();
        return json;

    }
}
