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

public class DomParserCastsExample {

    HashMap<String, ArrayList<String>> cast_lists;
    Document casts_dom;

    public DomParserCastsExample() {
        //create a list to hold the employee objects
        cast_lists = new HashMap<String, ArrayList<String>>();
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
            casts_dom = db.parse("casts124.xml");

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
        Element docEle = casts_dom.getDocumentElement();

        //get a nodelist of <dirfilms> elements
        NodeList nl = docEle.getElementsByTagName("dirfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the dirfilms element
                Element el = (Element) nl.item(i);
                NodeList filmc_list = el.getElementsByTagName("filmc"); //get <filmc> tags; each contains an <m> tag
                if (filmc_list != null && filmc_list.getLength() > 0) {
                    for (int j = 0; j < filmc_list.getLength(); j++) {
                        NodeList filmc_tag = filmc_list.item(j).getChildNodes();
                        if (filmc_tag != null && filmc_tag.getLength() > 0) {
                            String id = null;
                            ArrayList<String> names = new ArrayList<String>();
                            for (int k = 0; k < filmc_tag.getLength(); k++) {
                                NodeList filmc_nodes = filmc_tag.item(k).getChildNodes();
                                if (filmc_nodes != null && filmc_nodes.getLength() > 0) {
                                    for (int l = 0; l < filmc_nodes.getLength(); l++) {
                                        Node filmc_n = filmc_nodes.item(l);
                                        if (filmc_n.getNodeName().equals("f")) {
                                            id = filmc_n.getTextContent();
                                        }
                                        if (filmc_n.getNodeName().equals("a") && !filmc_n.getTextContent().equals("s a") && !filmc_n.getTextContent().equals("no\\_actor") && !filmc_n.getTextContent().equals("midget actor")) {
                                            String res = filmc_n.getTextContent().replace('~', ' ').replace("\\", "").replace("\'", "`");
                                            res = res.replace("\'", "`").replace("\"", "`");
                                            res = res.replace("{", "").replace("}", "");
                                            names.add(res);
                                        }
                                    }
                                }
                            }
                            if (names.size() > 0)
                                cast_lists.put(id, names);
                        }
                    }
                }
            }
        }
    }

    /**
     * I take an employee element and read the values in, create
     * an Employee object and return it
     *
     * @param empEl
     * @return
     */
    private Employee getEmployee(Element empEl) {

        //for each <employee> element get text or int values of
        //name ,id, age and name
        String name = getTextValue(empEl, "Name");
        int id = getIntValue(empEl, "Id");
        int age = getIntValue(empEl, "Age");

        String type = empEl.getAttribute("type");

        //Create a new Employee with the value read from the xml nodes
        Employee e = new Employee(name, id, age, type);

        return e;
    }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John
     *
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     *
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {
        for (String id : cast_lists.keySet()) {
            if (id != null) {
                ArrayList<String> actors = cast_lists.get(id);
                System.out.print("id: " + id + ", Stars: ");
                for (int i = 0; i < actors.size(); i++) {
                    if (i != actors.size()-1)
                        System.out.print(actors.get(i) + ", ");
                    else
                        System.out.print(actors.get(i));
                }
                System.out.println();
            }
        }
    }

//    public static void main(String[] args) {
//        //create an instance
//        DomParserCastsExample dpe = new DomParserCastsExample();
//
//        //call run example
//        dpe.runExample();
//    }

}
