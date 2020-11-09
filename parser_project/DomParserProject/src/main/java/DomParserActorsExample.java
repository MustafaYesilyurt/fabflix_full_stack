import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomParserActorsExample {

    HashMap<String, Actor> actors;
    Document actors_dom;

    public DomParserActorsExample() {
        //create a list to hold the employee objects
        actors = new HashMap<String, Actor>();
    }

    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            //dom = db.parse("hitchcock.xml");
            actors_dom = db.parse("actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        //get the root elememt
        Element docEle = actors_dom.getDocumentElement();

        //get a nodelist of <actor> elements
        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) { // iterate through the list of <actor>s

                Node actor = nl.item(i);
                NodeList al = actor.getChildNodes();
                if (al != null && al.getLength() > 0) {
                    String name = null, dob = null;
                    for (int j = 0; j < al.getLength(); j++) {
                        Node actor_node = al.item(j);
                        //System.out.println(actor_node.getNodeName());
                        if (actor_node.getNodeName().equals("stagename")) {
                            String res = actor_node.getTextContent().replace('~', ' ');
                            res = res.replace("\\", "");
                            res = res.replace("\'", "`").replace("\"", "`");
                            name = res;
                        }
                        if (actor_node.getNodeName().equals("dob") && !actor_node.getTextContent().equals("")) {
                            dob = actor_node.getTextContent().replace("~", "");
                        }
                    }
                    Actor a = new Actor(name, dob);
                    actors.put(name, a);
                }
            }
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    private void printData() {
        for (String name : actors.keySet()) {
            System.out.println("\t" + actors.get(name).toString());
        }
    }

//    public static void main(String[] args) {
//        //create an instance
//        DomParserActorsExample dpe = new DomParserActorsExample();
//
//        //call run example
//        dpe.runExample();
//    }

}
