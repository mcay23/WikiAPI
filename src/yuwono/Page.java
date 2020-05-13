package yuwono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Page {

	public static final String QUERY_ID_HDR = "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids=";
	public static final String QUERY_ID_FTR = "&inprop=url&format=json";
	
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
	public static final String QUERY_FTR = "&format=json";

	private String title;
	private int id;
	private String url;
	private int word_count;

	public Page(int id) {
	    this.title = "";
	    this.url = "";
		this.id = id;
		this.word_count = 0;
		updateURL();
		updateTitle();
	}
	
	public Page(String url) {
	    this.title = "";
	    this.url = url;
	    this.word_count = 0;
	    updateTitle();
	}
	
	public void updateTitle() {
	    if (this.url == null) {
	        throw new NullPointerException("null url");
	    }
        Document doc;
	    try {
            doc = Jsoup.connect(url).userAgent("mozilla/17.0").timeout(10000).get();
            this.title = doc.select("#firstHeading").text();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void updateURL() {
		String get = QUERY_ID_HDR + this.id + QUERY_ID_FTR;
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
			this.url = (String) jsonObject.get("fullurl");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void updateWordCount() {
	    String format_input = getTitle().replaceAll(" ", "%20");
        String get = QUERY_HDR + format_input + QUERY_FTR;
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
            JSONObject page_obj = (JSONObject) iterator.next();
            Long wc = (Long) page_obj.get("wordcount");
            this.word_count = wc.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public String getTitle() {
	    if (title.equals("")) {
	        updateTitle();
	    }
		return this.title;
	}
	
	public String getURL() {
	    if (url.equals("")) {
	        updateURL();
	    }
		return this.url;
	}
	
	public int getWordCount() {
	    if (word_count == 0) {
	        updateWordCount();
	    }
	    return this.word_count;
	}
}