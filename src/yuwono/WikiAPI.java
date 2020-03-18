package yuwono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public abstract class WikiAPI {
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
	public static final String QUERY_FTR = "&format=json";
	
    public static void main(String[] args) throws IOException, InterruptedException {
    	System.out.println(query("jakarta"));
    }
    
    public static ArrayList<Page> query(String input) {
        ArrayList<Page> pages = new ArrayList<>();
    	String input2 = input;
    	input2.replaceAll(" ", "%20");
    	String get = QUERY_HDR + input2 + QUERY_FTR;
        try {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(get))
	                .build();
			HttpResponse<String> response = client.send(request,
			        HttpResponse.BodyHandlers.ofString());
	        JSONParser parser = new JSONParser();
	        JSONObject jsonObject = (JSONObject) parser.parse(response.body());
			jsonObject = (JSONObject) jsonObject.get("query");
			JSONArray search = (JSONArray) jsonObject.get("search");
            @SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = search.iterator();
            while (iterator.hasNext()) {
            	JSONObject page_obj = (JSONObject) iterator.next();
            	String title = (String) page_obj.get("title");
            	Long id = (Long) page_obj.get("pageid");
            	Page pg = new Page(title, (int) id.intValue());
            	pages.add(pg);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return pages;
    }
}
