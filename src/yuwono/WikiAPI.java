package yuwono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch=";
	public static final String QUERY_URL_HDR = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=info&inprop=url&titles=";
	public static final String TOPICS_URL = "https://en.wikipedia.org/wiki/Category:Main_topic_classifications";
	public static final String RANDOM_HDR = "https://en.wikipedia.org/wiki/Special:RandomInCategory/";

	// articles outside main page to
	public static final String[] more_categories = { "Good articles" };

	public static ArrayList<Page> query(String input, int threshold) {
		ArrayList<Page> pages = new ArrayList<>();
		String format_input = input.replaceAll(" ", "%20");
		String request = QUERY_HDR + format_input;
		try {
			String response = get(request);
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(response);
			jsonObject = (JSONObject) jsonObject.get("query");
			JSONArray search = (JSONArray) jsonObject.get("search");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = search.iterator();
			int i = 0;
			while (i != threshold && iterator.hasNext()) {
				JSONObject page_obj = (JSONObject) iterator.next();
				int id = ((Long) page_obj.get("pageid")).intValue();
				pages.add(Page.createPage(id));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}

	public static ArrayList<Page> query(String input) {
		return query(input, 10);
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

	public static String get(String str) throws Exception {
		URL yahoo = new URL(str);
		URLConnection yc = yahoo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;
		String ret = "";
		while ((inputLine = in.readLine()) != null)
			ret += inputLine;
		in.close();
		return ret;
	}
}
