package request;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 *	A class responsible for sending request through the API 
 *	to the server and get xml response.
 */
public class RequestSender {
	
	
	/**
	 * @param id 
	 * @return article information including article title and id
	 * 
	 * A method used to send request(including article id) to server and get xml response
	 * user SAXReader to read xml response and retrieve article title and id
	 */
	public Map<String, String> getArticleInfo(String baseURL, String id) {
		
		try {
			
			//initialize a URL object by the API and the specific article id we want to find
			URL url = new URL(baseURL+id);
			
			//Open connection and set the request method to "GET"
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			//Receive the input stream by the connection with the server
			InputStream input = connection.getInputStream();
			
			//Initialize SAXReader object to read the XML response and store in a Document object
			SAXReader reader = new SAXReader();
			Document document =  reader.read(input);
			
			//extract the article id and article title from the document
			Node idTag = document.selectSingleNode("//Id");
			Node titleTag = document.selectSingleNode("//Item[@Name=\"Title\"]");
			
			//store the article information in a HashMap and return 
			Map<String, String> articleInfo = new HashMap<String, String>();
			articleInfo.put("id", idTag.getText());
			articleInfo.put("title", titleTag.getText());
			
			return articleInfo;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get article information");
		}
		
	}
	
	
	/**
	 * @param url 
	 * @return
	 * 
	 * Sending request to server through API which includes the article title from 
	 * the assignment file, to get the list of ids
	 */
	public List<String> getArticleIds(String url){
		//initialize an ArrayList Object to store the ids 
		List<String> resultIdList = new ArrayList<String>();
		
		try {
			//initialize a URL object to open connection and set the request method to "GET"
			URL requestURL = new URL(url);
			HttpURLConnection connection =  (HttpURLConnection) requestURL.openConnection();
			connection.setRequestMethod("GET");
			
			//initialize a input stream and use SAXReader to read the xml response from the server
			InputStream input = connection.getInputStream();
			SAXReader reader = new SAXReader();
			//store the response in a document object
			Document document = reader.read(input);
			
			//select all the id tag from the document
			List<Node> idList = document.selectNodes("//Id");
			System.out.println(idList.size());
			
			//use iterator the loop through the id node list
			Iterator<Node> iter = idList.iterator();
			
			while(iter.hasNext()) {
				//extract the text in the id element
				String id = iter.next().getText();
				//add the id to the id list
				resultIdList.add(id);
			}
			
			return resultIdList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get article ids from the url");
		}
	}
	
}
