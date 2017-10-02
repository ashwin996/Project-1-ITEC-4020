package xmlParse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlParser {
	
	FileInputStream input;
	
	//Initialize the parser with a file input for parsing 
	public XmlParser(FileInputStream input) {
		this.input = input;
	}
	
	
	/**
	 * @param articleTitle
	 *        the articleTitle tag you want to find in the xml file
	 * 
	 * @return a String of the article title for send the request to the server
	 */
	public String extractElementText(String articleTitle) {
		
		//SAXReader to read xml file
		SAXReader reader = new SAXReader();
		
		//use xPath to identify the node to find
		String xPathArticleTitle = "//"+articleTitle;
		
		try {
			//a document object will be created after reading the file
			Document document = reader.read(input);
			
			//extract the journal title from the assignment file
			String title = document.selectSingleNode(xPathArticleTitle).getText();
			
			return title;

		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Parse Failed");
		}
	}
	
	
	

	

}
