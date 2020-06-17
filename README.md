# WikiAPI
Wikipedia Random API for Java

Easily grab random articles from specific categories, currently not in the WikiMedia API.

# Usage
```
Page p = WikiAPI.getRandomPage("Physics");

System.out.println(p.getTitle());
System.out.println(p.getURL());
System.out.println(p.getWordCount());
System.out.println(p.getExtract());
```
