package program;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import request.RequestSender;
import xmlParse.XmlParser;

/**
 * 
 *	Main program that send request to 
 */
public class MainProgram {
	
	//The base API link for retrieving article information based on article id
	private final String articleIdAPIURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=";
	
	//The base API link for retrieving the list of article id based on the article title
	private final String articleInfoAPIURL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=";
	
	//starting position for the article id we want to get
	private int retStart = 0;
	//how many ids we want to get each time.
	private final int retmax = 1000;


	public static void main(String[] args) throws Exception {
		//indicate the datasets file and initialize a File input stream
		//initialize a XmlParser object to parse the the xml
		XmlParser parser = new XmlParser(new FileInputStream("4020a1-datasets.xml"));
		
		/*
		 * the parser object extract the title element and return the text information 
		 * within the title element. Then, replace the " "(empty space) with "+"
		 */
		String title = parser.extractElementText("Title").replace(" ", "+");
		
		/*	Initialize a MainProgram object, start to send the title
		 * 	to the server and get xml response.
		 * 	Write each response with article id and titles to the file 
		 * 	called titles.xml
		 */
		MainProgram program = new MainProgram();
		FileOutputStream out = new FileOutputStream("titles.xml");
		program.writeXML(title, out);
	}
	
	
	
	/**
	 * @param title 
	 * the title we want to send to the server to receive a list of article ids
	 * @param out
	 * the file output destination for the xml response
	 * 
	 * This method is responsible for sending request to the server through API
	 * 
	 */
	public void writeXML(String title, FileOutputStream out) {
		//declare a writer object
		XMLWriter writer = null;
		
		try {
			//construct the API link for sending the request to the server
			String idsURL = articleInfoAPIURL+title+"&retstart="+retStart+"&retmax="+retmax+"&usehistory=y";
			
			/*
			 * initialize a sender object for sending request to the server
			 * through a server API, we get a response of a list of article ids
			 */
			RequestSender sender = new RequestSender();
			List<String> idList = sender.getArticleIds(idsURL);
			
			//create a document object to store the xml response 
			Document document = DocumentHelper.createDocument();
			
			//adding the root element to the document
			Element root = document.addElement("PubmedArticleSet");
			
			//initialize the xml writer, and set the output format to pretty format
			writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
			
			//a loop to loop through the whole list of article id we receive from the server
			//the process should continue util getting the whole list of ids
			while(!idList.isEmpty()) {
				
				//while looping through the whole list of ids
				for(String id : idList) {
					
					//send request to the server by the article id through the server API
					Map<String, String> articleInfo = sender.getArticleInfo(articleIdAPIURL, id);
					//add the article information to the document as a article element
					addElement(root, articleInfo);
				}
				
				/*
				 * after finish a batch of article id processing
				 * increment the starting position
				 * send new request to the server and get a new list of article id to process 
				 */
				retStart += retmax;
				idsURL = articleInfoAPIURL+title+"&retstart="+retStart+"&retmax="+retmax+"&usehistory=y";
				idList = sender.getArticleIds(idsURL);
			}
			
			//finally write the document object to the xml file.
			writer.write(document);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//close the writer and release resources
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//indicate the end of the xml writing
		System.out.println("Finished");
	}
	

	
	/**
	 * @param root
	 * root element that we want to add element into
	 * @param articleInfo
	 * the article information we are going we extract
	 * @return
	 * 
	 * This method is responsible for parsing the article information received 
	 * from the server and convert them to two elements in the xml document
	 * 
	 * each time this method get called a xml element will be created and insert 
	 * into the root element of the xml file.
	 * 
	 * The xml element structure is like this:
	 * <PubmedArticle>
	 * 	<PMID>(The id of the article)</PMID>
	 * 	<ArticleTitle>(The title of the article)</ArticleTitle>
	 * </PubmedArticle>
	 */
	public Element addElement(Element root, Map<String, String> articleInfo) {
		//add a <PubmedArticle></PubmedArticle> tag with in the root element in the document
		Element pumbedArticle = root.addElement("PubmedArticle");
		
		//add <PMID></PMID><ArticleTitle></ArticleTitle> tag within the <PubmedArticle></PubmedArticle> tag
		Element pmid = pumbedArticle.addElement("PMID");
		Element title = pumbedArticle.addElement("ArticleTitle");
		
		//
		pmid.setText(articleInfo.get("id"));
		title.setText(articleInfo.get("title"));
		
		return pumbedArticle;
	}

}
