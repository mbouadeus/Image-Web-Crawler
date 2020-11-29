

import java.util.Set;
import java.io.IOException;
import java.util.HashSet;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebsiteCrawl {
	private Set<String> links;
	private Set<String> images;
	private URL starterUrl;
	private String starterHost;
	private String favicon;
	private String logo;
	
	public WebsiteCrawl(String starterLink) {
		starterUrl = getUrl(starterLink);
		
		starterHost = null;
		if (starterUrl != null) {
			starterHost = starterUrl.getHost();
			if (starterUrl.toString().startsWith("https"))
				starterHost = "https://" + starterHost;
			else
				starterHost = "http://" + starterHost;
		}
		
		links = new HashSet<>();
		images = new HashSet<>();
		
		favicon = null;
		logo = null;
		
		System.out.println(FaceDetector.hasFace("https://readahead.org/wp-content/uploads/2020/07/LM1a-960x1024.jpg"));
	}
	
	private URL getUrl(String link) {
		URL url = null;
		try {
			url = new URL(link);
		} catch (IOException exp) {
			System.out.println("Link: " + link + ", is an invalid URL.");
		}
		return url;
	}
	
	public void startCrawling() {
		if (starterUrl != null)
			crawlLink(starterUrl.toString());
	}
	
	private void crawlLink(String link) {
		if (!links.contains(link)) {
			try {
				System.out.println("Crawling: " + link);
				links.add(link);
				
				Document webDoc = Jsoup.connect(link).get();
				
				if (favicon == null) {
					Elements linkElems = webDoc.select("link");
					for (Element linkElem : linkElems) {
						if (isFavicon(linkElem)) {
							String image = getImageUrl(linkElem.attr("href"));
							favicon = image;
							System.out.println("Favicon: " + favicon);
							break;
						}
					}
				}
				
				Elements imageElems = webDoc.select("img[src]");
				for (Element imageElem : imageElems) {
					String image = getImageUrl(imageElem.attr("src"));
					if (logo == null && isLogo(imageElem)) {
						logo = image;
						System.out.println("Logo: " + logo);
					}
					images.add(image);
				}
				
				Elements svgElems = webDoc.select("svg");
				for (Element svgElem : svgElems) {
					Element root = new Element("div");
					svgElem.appendTo(root);
					String image = root.html();
					if (logo == null && isLogo(svgElem)) {
						logo = image;
						System.out.println("Logo: " + logo);
					}
					images.add(image);
				}
				
				Elements newLinkElems = webDoc.select("a");
				
				for (Element newLinkElem : newLinkElems) {
					String newLink = newLinkElem.attr("href");
					System.out.println(newLink);
					URL newUrl = getUrl(newLink);
					if (newUrl != null && isPageURL(newUrl)) {
						crawlLink(newLink);
					}
					
				}
			} catch (IOException exp) {
				System.out.println("For url: " + link + "\n" + exp.getMessage());
			}
		}
	}
	
	private boolean isPageURL(URL url) {
		String file = url.getFile();
		return (!file.contains(".") || file.endsWith(".html")) && !file.contains("?") && url.getHost().equals(starterUrl.getHost());
	}
	
	private boolean isFavicon(Element linkElem) {
		String relAttr = linkElem.attr("rel");
		if (relAttr != null && relAttr.contains("icon")) return true;
		return false;
	}
	
	private boolean isLogo(Element imageElem) {
		String classAttr = imageElem.attr("class");
		if (classAttr != null && classAttr.contains("logo")) return true;
		
		String srcAttr = imageElem.attr("src");
		if (srcAttr != null && srcAttr.contains("logo")) return true;
		
		String altAttr = imageElem.attr("alt");
		if (altAttr != null && altAttr.contains("logo")) return true;
		
		return false;
	}
	
	private String getImageUrl(String imageLink) {
		if (imageLink.startsWith("/")) return starterHost + imageLink;
		return imageLink;
	}
	public Set<String> getImages() {
		return images;
	}
}
