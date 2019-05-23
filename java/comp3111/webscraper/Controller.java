/**
 * 
 */
package comp3111.webscraper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Hyperlink;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/** task 1 **/
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.util.Observable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.Vector;

import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
/** task 5 **/
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;

/**
 * 
 * @author kevinw
 *
 *
 *         Controller class that manage GUI interaction. Please see document
 *         about JavaFX for details.
 * 
 */
public class Controller extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {

	}

	@FXML
	private Label labelCount;

	@FXML
	private Label labelPrice;

	@FXML
	private Hyperlink labelMin;

	@FXML
	private Hyperlink labelLatest;

	@FXML
	private TextField textFieldKeyword;

	@FXML
	private TextArea textAreaConsole;

	@FXML
	private TableView myTable;

	@FXML
	private TableColumn Title;

	@FXML
	private TableColumn Price;

	@FXML
	private TableColumn URL;

	@FXML
	private TableColumn PostedDate;

	@FXML
	private Button go;

	@FXML
	private Button refine;

	@FXML
	private MenuItem LastSearch;

	private String lastSearchWord;
	private String currentSearchWord;
	private WebScraper scraper;

	private String lastTextConsole;
	private ObservableList<Item> lastData;
	private String currTextConsole;
	private ObservableList<Item> currData;
	private String lastNumberOfData;
	private String lastAvgPrice;
	private String lastLowestPrice;
	private String lastLatestPost;
	private String currNumberOfData;
	private String currAvgPrice;
	private String currLowestPrice;
	private String currLatestPost;

	/**
	 * Default controller
	 */
	public Controller() {
		scraper = new WebScraper();
	}

	/**
	 * Default initializer.
	 */
	@FXML
	private void initialize() {
		BooleanBinding disable = go.disableProperty().not();
		refine.disableProperty().bind(disable);
	}

	/**
	 * Called when the search button is pressed.
	 */
	@FXML
	private void actionSearch() {
		if (textFieldKeyword.getText().trim().length() > 0) {
			textAreaConsole.setText("");
			System.out.println("actionSearch: " + textFieldKeyword.getText());
			List<Item> result = scraper.scrape(textFieldKeyword.getText());
			if (result.isEmpty() == true)
				return;
			ObservableList<Item> data = FXCollections.observableArrayList();
			String output = "";

			for (Item item : result) {
				// exclude 0 price items
				if (item.getPrice() > 0) {
					output = outputLogic(output, item);
					item.getUrl().setOnAction((event) -> {
						try {
							HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
							hostServices.showDocument(item.getUrl().getText());
						} catch (Exception e) {
						}
					});
					data.add(item);
				}
			}

			textAreaConsole.setText(output);
			BooleanBinding disable = textFieldKeyword.textProperty().isEmpty();
			refine.disableProperty().bind(disable);
			if (output.length() == 0) {
				disable = go.disableProperty().not();
				refine.disableProperty().bind(disable);
			}

			int rowcount = rowCount(result);
			avgPrice(result, rowcount);
			lowestPrice(result, rowcount);
			latestPost(result, rowcount);

			// task 4 set table
			actionTable(data);

			// Task6
			LastTextConsoleChange(textAreaConsole.getText());
			LastTableDataChange(data);
			LastNumberOfDataChange(labelCount.getText());
			LastAvgPriceChange(labelPrice.getText());
			LastMinChange(labelMin.getText());
			LastLatestPostChange(labelLatest.getText());
			LastSearchChange(textFieldKeyword.getText());
			LastSearch.setDisable(false);
		}
	}

	/**
	 * used to store last the text of text console area
	 * 
	 * @author Suen Tsz Wing
	 * @param text - current text to store in current variable
	 */
	public void LastTextConsoleChange(String text) {
		lastTextConsole = currTextConsole;
		currTextConsole = text;
		if (lastTextConsole == null) {
			lastTextConsole = text;
		}
	}

	/**
	 * used to store the last of all table item in Observable List
	 * 
	 * @author Suen Tsz Wing
	 * @param data - current data to store in current variable
	 */
	public void LastTableDataChange(ObservableList<Item> data) {
		lastData = currData;
		currData = data;
		if (lastData == null) {
			lastData = data;
		}

	}

	/**
	 * used to store the last of number of data fetched
	 * 
	 * @author Suen Tsz WIng
	 * @param number - current number of data fetched to store in current vairable
	 */
	public void LastNumberOfDataChange(String number) {
		lastNumberOfData = currNumberOfData;
		currNumberOfData = number;
		if (lastNumberOfData == null) {
			lastNumberOfData = number;
		}
	}

	/**
	 * used to store the last average price
	 * 
	 * @author Suen Tsz Wing
	 * @param price - current average price to store in current variable
	 */
	public void LastAvgPriceChange(String price) {
		lastAvgPrice = currAvgPrice;
		currAvgPrice = price;
		if (lastAvgPrice == null) {
			lastAvgPrice = price;
		}
	}

	/**
	 * used to store the last minimum price
	 * 
	 * @author Suen Tsz Wing
	 * @param min - current minimum price to store in current variable
	 */
	public void LastMinChange(String min) {
		lastLowestPrice = currLowestPrice;
		currLowestPrice = min;
		if (lastLowestPrice == null) {
			lastLowestPrice = min;
		}
	}

	/**
	 * used to store the last latest post date with URL
	 * 
	 * @author Suen Tsz Wing
	 * @param latest - current latest posted with URL to store in current variable
	 */
	public void LastLatestPostChange(String latest) {
		lastLatestPost = currLatestPost;
		currLatestPost = latest;
		if (lastLatestPost == null) {
			lastLatestPost = currLatestPost;
		}
	}

	/**
	 * It is used to change lastSearchWord and CurrentSearchWord. record to use when
	 * user request last searching
	 * 
	 * @author Suen Tsz Wing
	 * @param newWord The user input new word to search.
	 */
	public void LastSearchChange(String newWord) {
		lastSearchWord = currentSearchWord;
		currentSearchWord = newWord;
		if (lastSearchWord == null)
			lastSearchWord = currentSearchWord;
	}

	/**
	 * Assemble item object to string
	 * 
	 * @author Wong Man Long Anson
	 * @param output String being used as concatenation
	 * @param item   Item object to be converted to string
	 * @return string of items to console
	 */
	public String outputLogic(String output, Item item) {
		output += item.getTitle() + "\t" + item.getPrice() + "\t" + item.getUrl().getText() + "\t"
				+ item.getPostedDate() + "\n";
		return output;
	}

	/**
	 * This function for task 3 show how many pages has already been scraped during
	 * the process.
	 * 
	 * @author Leung Chun Ting
	 * @param ScrapedPages - the number of scraped page(s)
	 */
	@FXML
	public void printScrapedPages(int ScrapedPages) {
		textAreaConsole.setText(ScrapedPages + " page(s) scraped");
	}

	/**
	 * Show number of rows after scraping data and filled the summary text, $0 items
	 * are excluded to UI
	 * 
	 * @author	Wong Man Long Anson
	 * @param result	Data scraped after searching
	 * @return count	Number of rows in the console
	 */
	private int rowCount(List<Item> result) {
		int count = rowCountLogic(result);
		labelCount.setText(Integer.toString(count));
		return count;
	}

	/**
	 * Logic of showing number of rows after scraping data and filled the summary
	 * text, $0 items are excluded
	 * 
	 * @author Wong Man Long Anson
	 * @param result	Data scraped after searching
	 * @return count	Number of rows in the console
	 */
	public int rowCountLogic(List<Item> result) {
		int count = 0;
		for (Item item : result) {
			// exclude 0 price items
			if (item.getPrice() > 0)
				count++;
		}
		return count;
	}

	/**
	 * Show average selling price after scraping data and filled the summary text,
	 * $0 items are excluded "-" to Average selling price, lowest selling price and
	 * latest post for result not found to UI
	 * 
	 * @author	Wong Man Long Anson
	 * @param result	Data scraped after searching
	 * @param rowcount	Number of rows scraped
	 * @return Nothing for this function, simply output average selling price to
	 *         console
	 */
	private void avgPrice(List<Item> result, int rowcount) {
		if (result == null || rowcount <= 0)
			labelPrice.setText("-");
		double average = avgPriceLogic(result, rowcount);
		labelPrice.setText(Double.toString(average));
	}

	/**
	 * Logic of showing average selling price after scraping data and filled the
	 * summary tab, $0 items are excluded "-" to Average selling price if result not found
	 * @author Wong Man Long Anson
	 * @param result   Data scraped after searching
	 * @param rowcount Number of rows scraped
	 * @return average	average selling price
	 */
	public double avgPriceLogic(List<Item> result, int rowcount) {
		double total = 0, average = 0;
		for (Item item : result) {
			// exclude 0 price items
			if (item.getPrice() > 0)
				total += item.getPrice();
		}
		average = total / rowcount;
		return average;
	}

	/**
	 * Show lowest selling price after scraping data and filled the summary tab, $0
	 * items are excluded "-" to lowest selling price if result is not found
	 * Otherwise, a clickable URL will be placed in the field Lowest selling price
	 * 
	 * @author Wong Man Long Anson
	 * @return Nothing for this function, simply output lowest selling price to
	 *         console
	 * @param result   Data scraped after searching
	 * @param rowcount Number of rows scraped
	 * @exception e
	 */
	private void lowestPrice(List<Item> result, int rowcount) {
		Vector<Item> data = new Vector<Item>(rowcount);
		if (result == null || rowcount <= 0)
			labelPrice.setText("-");

		data = excludeItemLogic(result, data);
		final int minIndex = findLowestPriceIndexLogic(data);

		labelMin.setOnAction((event) -> {
			try {
				HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
				hostServices.showDocument(result.get(minIndex).getUrl().getText());
			} catch (Exception e) {
			}
		});
		labelMin.setText(Double.toString(data.get(0).getPrice()));
	}

	/**
	 * Logic of excluding item with price less than or equal to $0
	 * 
	 * @author Wong Man Long Anson
	 * @param result Data scraped after searching
	 * @param data   Vector used to store the new item list
	 * @return data	 Vector used to store the item
	 */
	public Vector<Item> excludeItemLogic(List<Item> result, Vector<Item> data) {
		for (Item item : result) {
			if (item.getPrice() > 0)
				data.add(item);
		}
		return data;
	}

	/**
	 * Logic of finding lowest selling price index from vector
	 * 
	 * @author Wong Man Long Anson
	 * @param data		Vector used to store the new item list
	 * @return index	vector index for indicating the lowest selling price
	 */
	public int findLowestPriceIndexLogic(Vector<Item> data) {
		double min = data.get(0).getPrice();
		int index = 0;
		for (int i = 1; i < data.size(); i++) {
			if (min > data.get(i).getPrice() && data.get(i).getPrice() > 0) {
				min = data.get(i).getPrice();
				index = i;
			}
		}
		return index;
	}

	/**
	 * Show latest post in the summary tab, $0 items are excluded "-" will be used
	 * if latest post for result is not found Otherwise, a clickable URL will be
	 * placed in the field Latest Post
	 * 
	 * @author Wong Man Long Anson
	 * @param result   Data scraped after searching
	 * @param rowcount Number of rows scraped
	 * @exception e
	 * @return Nothing for this function, simply output lowest selling price to
	 *         console
	 */
	private void latestPost(List<Item> result, int rowcount) {
		if (result == null || rowcount <= 0)
			labelPrice.setText("-");
		Item latestItem = latestPostLogic(result);
		labelLatest.setOnAction((event) -> {
			try {
				HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
				hostServices.showDocument(latestItem.getUrl().getText());
			} catch (Exception e) {
			}
		});
		labelLatest.setText(latestItem.getUrl().getText());
	}

	/**
	 * Logic of showing latest post in the summary tab, $0 items are excluded "-"
	 * will be used if latest post for result is not found
	 * 
	 * @author Wong Man Long Anson
	 * @param result Data scraped after searching
	 * @return latestItem Latest Item according to the post date
	 */
	public Item latestPostLogic(List<Item> result) {
		Item latestItem = null;
		for (Item item : result) {
			if (item.getPrice() > 0) {
				if (latestItem == null)
					latestItem = item;
				if (item.getPostedDate().compareTo(latestItem.getPostedDate()) > 0)
					latestItem = item;
			}
		}
		return latestItem;
	}

	/**
	 * It is used to fill searching items in table view.
	 * 
	 * @author Suen Tsz Wing
	 * @param data The items already stored in ObservableList<Item>.
	 */
	@FXML
	private void actionTable(ObservableList<Item> data) {
		Title.setCellValueFactory(new PropertyValueFactory<Item, String>("title"));
		Price.setCellValueFactory(new PropertyValueFactory<Item, String>("price"));
		URL.setCellValueFactory(new PropertyValueFactory<Item, Hyperlink>("url"));
		PostedDate.setCellValueFactory(new PropertyValueFactory<Item, String>("postedDate"));
		myTable.setItems(data);
	}

	/**
	 * When the refine button is clicked, filter the searched data and refill
	 * summary tab and keep those items with their titles containing the keywords
	 * typed in the text area
	 * 
	 * @author Wong Man Long Anson
	 * @return Nothing for this function, simply output data scraped to console
	 */
	@FXML
	private void actionRefine() {
		if (textFieldKeyword.getText().trim().length() > 0) {
			String output = filterConsoleTextToOutputLogic(textAreaConsole.getText(), textFieldKeyword.getText());

			textAreaConsole.setText(output);
			if (output == "")
				return;

			List<Item> result = covertConsoleTextToItemLogic(textAreaConsole.getText());
			if (result.isEmpty() == true)
				return;
			int rowcount = rowCount(result);
			avgPrice(result, rowcount);
			lowestPrice(result, rowcount);
			latestPost(result, rowcount);

			BooleanBinding disable = go.disableProperty().not();
			refine.disableProperty().bind(disable);

			ObservableList<Item> data = TableList(output, textFieldKeyword.getText());
			actionTable(data);
		}
	}

	/**
	 * It puts TextAreaConsole into this function to get list of table to full table
	 * value.
	 * 
	 * @author Suen Tsz Wing
	 * @param output  - the output of TextAreaConsole
	 * @param keyword - what keyword is being searching now
	 * @return data used to insert table values
	 */
	public ObservableList<Item> TableList(String output, String keyword) {
		ObservableList<Item> data = FXCollections.observableArrayList();
		List<String> list = new ArrayList<String>();
		for (String line : output.split("\\n")) {
			if (line.indexOf(keyword) > -1)
				list.add(line);
		}
		for (String line : list) {
			String[] ItemData = line.split("\t");
			Item item = new Item();
			item.setTitle(ItemData[0]);
			item.setPrice(Double.parseDouble(ItemData[1]));
			item.setUrl(ItemData[2]);
			item.setPostedDate(ItemData[3]);
			data.add(item);
		}
		return data;
	}

	/**
	 * Logic of converting console text to item object when the refine button is
	 * clicked
	 * 
	 * @author Wong Man Long Anson
	 * @param consoleString string showed in the console area
	 * @return result item object after conversion from string
	 */
	public List<Item> covertConsoleTextToItemLogic(String consoleString) {
		List<String> list = new ArrayList<String>();
		List<Item> result = new ArrayList<Item>();
		for (String line : consoleString.split("\\n"))
			list.add(consoleString);

		for (String line : list) {
			String[] ItemData = line.split("\t");
			Item item = new Item();
			item.setTitle(ItemData[0]);
			item.setPrice(Double.parseDouble(ItemData[1]));
			item.setUrl(ItemData[2]);
			item.setPostedDate(ItemData[3]);
			result.add(item);
		}
		return result;
	}

	/**
	 * Logic of converting console text to refined output when the refine button is
	 * clicked
	 * 
	 * @author Wong Man Long Anson
	 * @param consoleString string showed in the console area
	 * @param searchString  keywords to be searched from the consoleString
	 * @return output new refined text
	 */
	public String filterConsoleTextToOutputLogic(String consoleString, String searchString) {
		String output = "";
		for (String line : consoleString.split("\\n")) {
			if (line.indexOf(searchString) > -1)
				output += line + "\n";
		}
		return output;
	}

	/**
	 * when the user click on menu item Last Search it will revert the search to the
	 * previous search.
	 * 
	 * @author Suen Tsz Wing
	 */
	@FXML
	private void actionLastSearch() {
		textFieldKeyword.setText(lastSearchWord);
		actionTable(lastData);
		labelCount.setText(lastNumberOfData);
		labelPrice.setText(lastAvgPrice);
		labelMin.setText(lastLowestPrice);
		labelLatest.setText(lastLatestPost);
		textAreaConsole.setText(lastTextConsole);

		currentSearchWord = lastSearchWord;
		currData = lastData;
		currNumberOfData = lastNumberOfData;
		currAvgPrice = lastAvgPrice;
		currLowestPrice = lastLowestPrice;
		currLatestPost = lastLatestPost;
		currTextConsole = lastTextConsole;
		LastSearch.setDisable(true);
	}

	/**
	 * when the user click on menu item About Your Team it will show a new simple
	 * dialog that shows all your team members name, itsc account, and github
	 * account.
	 * 
	 * @author Suen Tsz Wing
	 */
	@FXML
	private void actionAboutTeam() {

		Alert dialog = new Alert(AlertType.INFORMATION);
		dialog.setTitle("About Your Team");
		dialog.setGraphic(null);
		dialog.setHeaderText(null);
		dialog.setContentText("Name: Leung Chun Ting\n" + "ITSC account: ctleungad@connect.ust.hk\n"
				+ "Github account: nickchunt\n\n" + "Name: Wong Man Long Anson\n"
				+ "ITSC account: mlawong@connect.ust.hk\n" + "Github account: wmla419\n\n" + "Name: Suen Tsz Wing\n"
				+ "ITSC account: twsuen@conect.ust.hk\n" + "Github account: suentsz96wing\n");
		dialog.showAndWait();
	}

	/**
	 * when the user click on menu item Quit this action will exit the program and
	 * close all connections.
	 * 
	 * @author Suen Tsz Wing
	 */
	@FXML
	private void actionQuit() {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * when the user click on menu item Close, The GUI will clear the current search
	 * record and initialize all tabs on the right to their initial state.
	 * 
	 * @author Suen Tsz Wing
	 */
	@FXML
	private void actionClose() {
		textFieldKeyword.clear();
		textAreaConsole.clear();
		labelCount.setText("<total>");
		labelPrice.setText("<AvgPrice>");
		labelMin.setText("<Lowest>");
		labelLatest.setText("<Latest>");
		for (int i = 0; i < myTable.getItems().size(); i++) {
			myTable.getItems().clear();
		}
		// distribution & trend....
	}
}
