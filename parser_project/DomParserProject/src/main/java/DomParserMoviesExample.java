
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

public class DomParserMoviesExample {

    HashMap<String, ArrayList<Movie>> directors_films;
    Document movies_dom;

    public DomParserMoviesExample() {
        //create a list to hold the employee objects
        directors_films = new HashMap<String, ArrayList<Movie>>();
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
            movies_dom = db.parse("mains243.xml");

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
        Element docEle = movies_dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the directorfilms element
                Element el = (Element) nl.item(i);
                NodeList childList = el.getChildNodes(); //has "director" and "films"

                //get the director child
                Element director_el = (Element) childList.item(0);
                Element films_el = (Element) childList.item(1);

                //get the director name from the dirname tag
                String director = getTextValue(director_el, "dirname");
                if (director == null) {
                    director = getTextValue(director_el, "dirn");
                    if (director == null) {
                        NodeList director_child_nodes = director_el.getChildNodes();
                        if (director_child_nodes != null && director_child_nodes.getLength() > 0) {
                            for (int j = 0; j < director_child_nodes.getLength(); j++) {
                                if (director_child_nodes.item(j).getNodeName().equals("dirname"))
                                    director = director_child_nodes.item(j).getTextContent();
                            }
                        }
                    }
                }
                String directorId = getTextValue(director_el, "dirid");
                if (directorId == null) {
                    NodeList director_child_nodes = director_el.getChildNodes();
                    if (director_child_nodes != null && director_child_nodes.getLength() > 0) {
                        for (int j = 0; j < director_child_nodes.getLength(); j++) {
                            if (director_child_nodes.item(j).getNodeName().equals("dirid"))
                                directorId = director_child_nodes.item(j).getTextContent();
                        }
                    }
                }

                NodeList films_list = films_el.getChildNodes();  // get a list of film nodes
                if (films_list != null && films_list.getLength() > 0) {
                    for (int j = 0; j < films_list.getLength(); j++) {  // iterate through that list of film objects
                        //System.out.println(films_list.item(j).getTextContent());
                        NodeList film = films_list.item(j).getChildNodes(); // get each individual film object
                        if (film != null && film.getLength() > 0) {
                            String id = null;
                            String title = null;
                            String year = null;
                            ArrayList<String> genres = new ArrayList<String>();
                            for (int k = 0; k < film.getLength(); k++) {  // iterate through the nodes of that film object
                                Node film_node = film.item(k);
                                if (film_node.getNodeName().equals("cats")) {
                                    NodeList cats = film_node.getChildNodes();
                                    if (cats != null && cats.getLength() > 0) {
                                        for (int l = 0; l < cats.getLength(); l++) {
                                            Node cat = cats.item(l);
                                            genres.add(cat.getTextContent().replace("'", ""));
                                        }
                                    }
                                }
                                if (film_node.getNodeName().equals("fid") || film_node.getNodeName().equals("filmed")) {
                                    id = film_node.getTextContent();
                                }
                                if (film_node.getNodeName().equals("t")) {
                                    title = film_node.getTextContent();
                                }
                                if (film_node.getNodeName().equals("year")) {
                                    year = film_node.getTextContent();
                                }
                            }
                            if (id != null && !id.equals(" ") && !id.equals("") && directors_films.get(id) == null) {
                                if (director != null)
                                    director = director.replace('~', ' ').replace("\\", "").replace("\'", "`").replace("\"", "`").replace("{", "").replace("}", "");
                                if (title != null)
                                    title = title.replace('~', ' ').replace("\\", "").replace("\'", "`").replace("\"", "`").replace("{", "").replace("}", "");
                                if (year != null)
                                    year = year.replace('~', ' ').replace("\\", "").replace("\'", "`").replace("\"", "`").replace("{", "").replace("}", "");
                                Movie m = new Movie(id, title, year, director, directorId, genres);
                                ArrayList<Movie> movies = directors_films.get(id);
                                if (movies == null)
                                    movies = new ArrayList<Movie>();
                                movies.add(m);
                                directors_films.put(id, movies);
                            }
                        }
                    }
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
        for (String key : directors_films.keySet()) {
            ArrayList<Movie> movies = directors_films.get(key);
            System.out.println("key: " + key);
            for (Movie m : movies)
            System.out.println("\t" + m.toString());
        }
    }

//    public static void main(String[] args) {
//        //create an instance
//        DomParserMoviesExample dpe = new DomParserMoviesExample();
//
//        //call run example
//        dpe.runExample();
//    }

}
