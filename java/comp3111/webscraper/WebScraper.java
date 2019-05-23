package comp3111.webscraper;

import java.net.URLEncoder;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.Vector;

/**
 * WebScraper provide a sample code that scrape web content. After it is
 * constructed, you can call the method scrape with a keyword, the client will
 * go to the default url and parse the page by looking at the HTML DOM. <br/>
 * In this particular sample code, it access to craigslist.org. You can directly
 * search on an entry by typing the URL <br/>
 * https://newyork.craigslist.org/search/sss?sort=rel&amp;query=KEYWORD <br/>
 * where KEYWORD is the keyword you want to search. <br/>
 * Assume you are working on Chrome, paste the url into your browser and press
 * F12 to load the source code of the HTML. You might be freak out if you have
 * never seen a HTML source code before. Keep calm and move on. Press
 * Ctrl-Shift-C (or CMD-Shift-C if you got a mac) and move your mouse cursor
 * around, different part of the HTML code and the corresponding the HTML
 * objects will be highlighted. Explore your HTML page from body &rarr; section
 * class="page-container" &rarr; form id="searchform" &rarr; div class="content"
 * &rarr; ul class="rows" &rarr; any one of the multiple li class="result-row"
 * &rarr; p class="result-info". You might see something like this: <br/>
 * 
 * <pre>
 * {@code
 *    <p class="result-info">
 *        <span class="icon icon-star" role="button" title=
"save this post in your favorites list">
 *           <span class="screen-reader-text">favorite this post</span>
 *       </span>
 *       <time class="result-date" datetime="2018-06-21 01:58" title=
"Thu 21 Jun 01:58:44 AM">Jun 21</time>
 *       <a href=
"https://newyork.craigslist.org/que/clt/d/green-star-polyp-gsp-on-rock/6596253604.html" data-id
="6596253604" class=
"result-title hdrlnk">Green Star Polyp GSP on a rock frag</a>
 *       <span class="result-meta">
 *               <span class="result-price">$15</span>
 *               <span class="result-tags">
 *                   pic
 *                   <span class="maptag" data-pid="6596253604">map</span>
 *               </span>
 *               <span class="banish icon icon-trash" role="button">
 *                   <span class="screen-reader-text">hide this posting</span>
 *               </span>
 *           <span class="unbanish icon icon-trash red" role=
"button" aria-hidden="true"></span>
 *           <a href="#" class="restore-link">
 *               <span class="restore-narrow-text">restore</span>
 *               <span class="restore-wide-text">restore this posting</span>
 *           </a>
 *       </span>
 *   </p>
 *}
 * </pre>
 * 
 * <br/>
 * The code
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	List<?> items = (List<?>) page.getByXPath("//li[@class='result-row']");
 * }
 * </pre>
 * 
 * extracts all result-row and stores the corresponding HTML elements to a list
 * called items. Later in the loop it extracts the anchor tag &lsaquo; a
 * &rsaquo; to retrieve the display text (by .asText()) and the link (by
 * .getHrefAttribute()). It also extracts
 * 
 *
 */
public class WebScraper {

	private WebClient client;
	private String[] website = new String[2];

	/**
	 * Default Constructor
	 */
	public WebScraper() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		// default website
		website[0] = "https://newyork.craigslist.org";
		// selected website (Price HK)
		website[1] = "https://www.price.com.hk";
	}

	/**
	 * The scrape procedure and output during data scraping. This function will
	 * control the program to scrape web content from the craigslist and hkprice
	 * 
	 * @author Leung Chun Ting
	 * @param keyword - the keyword you want to search
	 * @return A list of Item that has found. A zero size list is return if nothing
	 *         is found.
	 */
	public List<Item> scrape(String keyword) {
		int scrapedPages = 0;
		List<Item> result = new Vector<Item>();
		// loop of multiple website
		for (int k = 0; k < website.length; k++) {
			// loop of multiple page
			for (int pages = 0; true; pages++) {
				List<Item> data = new Vector<Item>();
				if (k == 0) {
					data = scrapeFromCraigslist(keyword, pages);
					if (data == null)
						break;
				} else {
					data = scrapeFromPricehk(keyword, pages);
					if (data == null)
						break;
				}
				// merge and sort data
				result = mergeData(result, data);
				// call function to print during scrape data
				System.out.println(++scrapedPages + " page(s) scraped.");
			}
		}
		return result;
	}

	/**
	 * This function will scrape web content from the craigslist
	 * 
	 * @author Leung Chun Ting
	 * @param keyword - the keyword you want to search
	 * @param pages   - the pages of search result
	 * @return A list of Item that has found. A zero size list is return if nothing
	 *         is found. Null if any exception (e.g. no connectivity)
	 */
	public List<Item> scrapeFromCraigslist(String keyword, int pages) {
		List<Item> data = new Vector<Item>();
		try {
			String searchUrl = website[0] + "/search/sss?s=" + (pages * 120) + "&sort=rel&query="
					+ URLEncoder.encode(keyword, "UTF-8");
			HtmlPage page = client.getPage(searchUrl);

			List<?> items = (List<?>) page.getByXPath("//li[@class='result-row']");

			if (items.size() == 0) {
				return null;
			}

			for (int i = 0; i < items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//p[@class='result-info']/a"));
				HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));
				HtmlElement date = ((HtmlElement) htmlItem.getFirstByXPath(".//p[@class='result-info']/time"));
				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

				String resultDate = date.getAttribute("datetime");

				resultDate = resultDate.substring(0, 11);

				Item item = new Item();
				item.setTitle(itemAnchor.asText());
				item.setUrl(itemAnchor.getHrefAttribute());
				item.setSource(website[0]);
				item.setPrice(new Double(itemPrice.replace("$", "")) * 7.8);
				item.setPostedDate(resultDate);
				data.add(item);
			}
			return data;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * This function will scrape web content from the PriceHK
	 * 
	 * @author Leung Chun Ting
	 * @param keyword - the keyword you want to search
	 * @param pages   - the pages of search result
	 * @return A list of Item that has found. A zero size list is return if nothing
	 *         is found. Null if any exception (e.g. no connectivity)
	 */
	public List<Item> scrapeFromPricehk(String keyword, int pages) {
		List<Item> data = new Vector<Item>();
		try {
			String searchUrl = website[1] + "/search.php?g=T&q=" + URLEncoder.encode(keyword, "UTF-8") + "&page="
					+ (pages + 1);
			HtmlPage page = client.getPage(searchUrl);

			List<?> items = (List<?>) page.getByXPath(
					"//ul[@class='list-unstyled list-inline  list-product-list list-search-product-list']/li");

			if (items.size() == 0) {
				return null;
			}

			for (int i = 0; i < items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem
						.getFirstByXPath(".//div/div/div[@class='column column-02']/div[@class='line line-01']/a"));
				HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(
						".//div/div/div[@class='column column-03']/div[@class='line line-01']/div/a/span[@class='text-price-number']"));
				HtmlElement date = ((HtmlElement) htmlItem.getFirstByXPath(
						".//div/div/div[@class='column column-02']/div[@class='line line-02']/div/table/tbody/tr/td[@class='info-content']"));
				// It is possible that an item doesn't have any price, we set the price to 0.0
				// in this case
				String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

				String datetime = date.asText();
				datetime = datetime + "     ";
				char datearray[] = datetime.toCharArray();
				/*
				 * because of price.com datetime show chinese character so change it to fufill
				 * UTF-16 to set time.
				 */
				for (int j = 0; j < datetime.length(); j++) {
					if (datearray[j] == '\u5e74' || datearray[j] == '\u6708') {
						datearray[j] = '-';
					} else if (datearray[j] == '\u65e5') {
						datearray[j] = ' ';
					}
					if (j == 5 && datearray[j + 1] == '\u6708') {
						char temp = datearray[j];
						char next;
						datearray[j] = '0';
						for (int z = j + 1; z < datetime.length(); z++) {
							next = datearray[z];
							datearray[z] = temp;
							temp = next;
						}
						if (datearray[9] == '\u65e5') {
							datearray[9] = datearray[8];
							datearray[8] = '0';
						}
					} else if (j == 8 && datearray[j + 1] == '\u65e5') {
						datearray[9] = datearray[8];
						datearray[8] = '0';
					}
				}
				String resultDate = String.valueOf(datearray);
				resultDate = resultDate.substring(0, 11);

				Item item = new Item();
				item.setTitle(itemAnchor.asText());
				item.setUrl(website[1] + itemAnchor.getHrefAttribute());
				item.setSource(website[1]);
				item.setPrice(new Double(itemPrice.replaceAll(",", "").replace("$", "")));
				item.setPostedDate(resultDate);
				data.add(item);
			}
			return data;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * This function will merge and sort data of two list of data
	 * 
	 * @author Leung Chun Ting
	 * @param result - the main list of data
	 * @param data   - the list of data will merge into the main list of data
	 * @return Return the merged and sorted data from two list of data
	 */
	public List<Item> mergeData(List<Item> result, List<Item> data) {
		for (Item d_item : data) {
			if (result.size() == 0) {
				result.add(d_item);
			} else {
				int index = 0;
				for (Item r_item : result) {
					if (d_item.getPrice() < r_item.getPrice()) {
						result.add(index, d_item);
						break;
					}
					index++;
				}
				if (index == result.size()) {
					result.add(d_item);
				}
			}
		}
		return result;
	}
}
