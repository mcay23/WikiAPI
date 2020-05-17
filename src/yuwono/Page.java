package yuwono;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Page {

	public static final String QUERY_EXTRACT_HDR = "https://en.wikipedia.org/w/api.php?format=json&action=query"
			+ "&prop=extracts&exintro&explaintext&redirects=1&pageids=";
	public static final String QUERY_HDR = "https://en.wikipedia.org/w/api.php"
			+ "?action=query&format=json&list=search&srsearch=";
	public static final String QUERY_ID_HDR = "https://en.wikipedia.org/w/api.php"
			+ "?action=query&format=json&prop=info&inprop=url&pageids=";

	private String title;
	private String extract;
	private String url;
	private int id;
	private int word_count;

	public Page(String url) {
		this.title = "";
		this.url = url;
		this.id = 0;
		this.extract = "";
		this.word_count = 0;
		update();
	}

	public static Page createPage(int id) {
		String req = QUERY_ID_HDR + id;
		String url = "";
		try {
			String response = WikiAPI.get(req);
			JSONParser parser = new JSONParser();
			JSONObject JSONobj = (JSONObject) parser.parse(response);
			JSONobj = (JSONObject) JSONobj.get("query");
			JSONobj = (JSONObject) JSONobj.get("pages");
			JSONobj = (JSONObject) JSONobj.get(Integer.toString(id));
			url = (String) JSONobj.get("fullurl");
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid Page ID createPage");
		}
		return new Page(url);
	}

	public void update() {
		if (this.url == null) {
			throw new NullPointerException("null url");
		}

		Document doc;
		String url_format = "";

		try {
			doc = Jsoup.connect(url).userAgent("mozilla/17.0").timeout(10000).get();
			this.title = doc.select("#firstHeading").text();
			url_format = this.title.replaceAll(" ", "%20");
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error fetching title, invalid URL or no internet connection");
		}

		String get_URL = QUERY_HDR + url_format;
		try {
			String response = WikiAPI.get(get_URL);
			JSONParser parser = new JSONParser();
			JSONObject JSONwordcount = (JSONObject) parser.parse(response);
			JSONObject JSONID = (JSONObject) parser.parse(response);

			JSONID = (JSONObject) JSONID.get("pages");
			JSONwordcount = (JSONObject) JSONwordcount.get("query");
			JSONArray search = (JSONArray) JSONwordcount.get("search");

			for (Object x : search) {
				JSONObject obj = (JSONObject) x;
				if (obj.get("title").equals(this.title)) {
					this.id = ((Long) obj.get("pageid")).intValue();
					this.word_count = ((Long) obj.get("wordcount")).intValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String get_URL2 = QUERY_EXTRACT_HDR + id;
		try {
			String response = WikiAPI.get(get_URL2);
			JSONParser parser = new JSONParser();
			JSONObject JSONextract = (JSONObject) parser.parse(response);
			JSONextract = (JSONObject) JSONextract.get("query");
			JSONextract = (JSONObject) JSONextract.get("pages");
			JSONextract = (JSONObject) JSONextract.get(Integer.toString(id));
			this.extract = (String) JSONextract.get("extract");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		if (title.equals("")) {
			return "NULL TITLE";
		}
		return this.title;
	}

	public String getURL() {
		if (this.url.equals("")) {
			return "NULL URL";
		}
		return this.url;
	}

	public int getWordCount() {
		if (word_count == 0) {
			return -1;
		}
		return this.word_count;
	}

	public String getExtract() {
		if (extract.equals("")) {
			return "NULL EXTRACT";
		}
		return this.extract;
	}

	public int getID() {
		if (this.id == 0) {
			return -1;
		}
		return this.id;
	}
}