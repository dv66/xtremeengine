import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLParser {

    public static void main(String argv[]) {

        try {

            File fXmlFile = new File("G:\\10101010101010\\L3T1\\MY COURSE\\DATABASE SESSIONAL\\XML DATASET\\DataScience\\Votes.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);



            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("row");
            System.out.println(nList.getLength());
            for(int i = 0 ;i  < nList.getLength() ; i++){

                Node nNode = nList.item( i );
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                }
            }

            //System.out.println("No. of total rows " + nList.getLength());

//            for (int i = 0; i < 1; i++) {
//
//                Node nNode = nList.item( i );
//
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}