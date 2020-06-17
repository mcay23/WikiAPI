# WikiAPI
Wikipedia Random API for Java

Easily grab random articles from specific categories, currently not in the WikiMedia API.

# Usage
Grab a random page in category Physics.
```
Page p = WikiAPI.getRandomPage("Physics");

System.out.println(p.getTitle());
System.out.println(p.getURL());
System.out.println(p.getWordCount());
System.out.println(p.getExtract());
```
Defaults to searching for articles 200 words or longer. To override
this behavior, call:
```
WikiAPI.getRandomPage("Physics", threshold)
```
where threshold is the minimum number of words the randomizer will accept.
Higher thresholds will take longer to process.
