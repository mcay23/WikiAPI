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
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class WikiAPI {
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
	public static final String QUERY_FTR = "&format=json";
	public static final String TOPICS_URL = "https://en.wikipedia.org/wiki/Category:Main_topic_classifications";
	public static final String RANDOM_HDR = "https://en.wikipedia.org/wiki/Special:RandomInCategory/";
	
	// articles outside main page to add
	public static final String[] more_categories = {"Good articles"};
	
    public static ArrayList<Page> query(String input) {
        ArrayList<Page> pages = new ArrayList<>();
    	String format_input = input.replaceAll(" ", "%20");
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
            while (iterator.hasNext()) {
            	JSONObject page_obj = (JSONObject) iterator.next();
            	String title = (String) page_obj.get("title");
            	Page pg = new Page(title);
            	pages.add(pg);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return pages;
    }
    
    public static ArrayList<String> categories() {
        ArrayList<String> categories = new ArrayList<>();

        Document doc;
        try {
            doc = Jsoup.connect(TOPICS_URL).userAgent("mozilla/17.0").timeout(10000).get();
            Elements topics = doc.select("div.CategoryTreeItem");
            for (Element a : topics) {
                String title = a.select("a").first().text();
                categories.add(title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        categories.remove("Main topic articles");
        for (String x : more_categories) {
            categories.add(x);
        }
        
        for (String x : categories) {
            System.out.print(x + "; ");
        }
        return categories;
    }
    
    public static Page getRandomPage(String category) {
        return getRandomPage(category, 200);
    }
    
    // size_threshold is word count lower bound
    public static Page getRandomPage(String category, int size_threshold) {
        Response response = null;
        Page pg;
        do {
            try {
                response = Jsoup.connect(RANDOM_HDR + category).followRedirects(true).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pg = new Page(response.url().toString());
        } while (pg.getWordCount() < size_threshold);
        return pg;
    }
    
    public static Page getPage(String url) {
        return new Page(url);
    }
}
