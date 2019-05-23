package comp3111.webscraper;

import javafx.scene.control.Hyperlink;

public class Item {
	private String title ; 
	private double price ;
	private Hyperlink url ;
	private String source ;
	private String postedDate;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	/**
	 * Get function of url
	 * 
	 * @author Leung Chun Ting
	 * @return url
	 */
	public Hyperlink getUrl() {
		return url;
	}
	/**
	 * Set function of url
	 * 
	 * @author Leung Chun Ting
	 * @param url - the url want to put into item private variable
	 */
	public void setUrl(String url) {
		this.url = new Hyperlink(url);
	}
	/**
	 * Get function of source
	 * 
	 * @author Leung Chun Ting
	 * @return source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * Set function of source
	 * 
	 * @author Leung Chun Ting
	 * @param source - the source want to put into item private variable
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**used to set posted date
	 * @author Suen Tsz Wing
	 * @param postedDate - posted date of item
	 */
	public void setPostedDate(String postedDate) {
		this.postedDate= postedDate;
	}
	/**used to return posted date
	 * @author Suen Tsz Wing
	 * @return return the item posted date which is private variable
	 */
	public String getPostedDate() {
		return postedDate;
	}

}
