package yuwono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Page {
	
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids=";
	public static final String QUERY_FTR = "&inprop=url&format=json";
	
	private String title;
	private int id;
	private String url;

	public Page(String title, int id) {
		this.title = title;
		this.id = id;
		System.out.println(title);
		updateURL();
	}
	
	public void updateURL() {
		String get = QUERY_HDR + this.id + QUERY_FTR;
		String json = "";
        try {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(get))
	                .build();
			HttpResponse<String> response = client.send(request,
			        HttpResponse.BodyHandlers.ofString());
	    	json = response.body();
		} catch (Exception e) {
			e.printStackTrace();
		}
        JSONParser jsonParser = new JSONParser();
        try {
			Object obj = jsonParser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			jsonObject = (JSONObject) jsonObject.get("query");
			jsonObject = (JSONObject) jsonObject.get("pages");
			jsonObject = (JSONObject) jsonObject.get(Integer.toString(id));
			String tst = (String) jsonObject.get("fullurl");
			System.out.println(tst);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
