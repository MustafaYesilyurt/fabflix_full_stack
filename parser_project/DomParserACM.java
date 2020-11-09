import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DomParserACM {

    HashMap<String, Integer> genre_map;

    HashMap<String, Actor> stars_map;

    HashMap<String, Movie> movie_map;
    Document movies_dom;

    HashMap<String, ArrayList<String>> cast_lists;
    Document casts_dom;

    HashMap<String, Actor> actors;
    Document actors_dom;

    public DomParserACM() {
        //create a list to hold the employee objects
        genre_map = new HashMap<String, Integer>();
        stars_map = new HashMap<String, Actor>();
        movie_map = new HashMap<String, Movie>();
        actors = new HashMap<String, Actor>();
        cast_lists = new HashMap<String, ArrayList<String>>();
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

    public void runMoviesExample() {
        parseMoviesXmlFile();
        parseMoviesDocument();
    }

    public void runActorsExample() {
        parseActorsXmlFile();
        parseActorsDocument();
    }

    public void runCastsExample() {
        parseCastsXmlFile();
        parseCastsDocument();
    }

    public void parseMoviesXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            movies_dom = db.parse("mains243.xml");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void parseActorsXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            actors_dom = db.parse("actors63.xml");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void parseCastsXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            casts_dom = db.parse("casts124.xml");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void getCat() {
        parseMoviesXmlFile();
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
                            ArrayList<String> genres = new ArrayList<String>();
                            for (int k = 0; k < film.getLength(); k++) {  // iterate through the nodes of that film object
                                Node film_node = film.item(k);
                                if (film_node.getNodeName().equals("fid") || film_node.getNodeName().equals("filmed")) {
                                    id = film_node.getTextContent();
                                }
                                if (film_node.getNodeName().equals("t")) {
                                    title = film_node.getTextContent();
                                }
                                if (film_node.getNodeName().equals("cats")) {
                                    NodeList cats = film_node.getChildNodes();
                                    if (cats != null && cats.getLength() > 0) {
                                        for (int l = 0; l < cats.getLength(); l++) {
                                            Node cat = cats.item(l);
                                            System.out.println(cat.getTextContent().trim());
                                            //System.out.println("Genre: " + cat.getTextContent() + ", ID: " + id + ", Title: " + title);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void parseMoviesDocument() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query;
            PreparedStatement ps;
            ResultSet rs;

            // get max genre id
            Integer gid_seed = -1;
            //query = "select max(id) as gid from new_genres;";
//            query = "SELECT AUTO_INCREMENT\n" +
//                    "FROM information_schema.TABLES\n" +
//                    "WHERE TABLE_SCHEMA = 'moviedb'\n" +
//                    "AND TABLE_NAME = 'new_genres';";
            query = "SELECT AUTO_INCREMENT\n" +
                    "FROM information_schema.TABLES\n" +
                    "WHERE TABLE_SCHEMA = 'moviedb'\n" +
                    "AND TABLE_NAME = 'genres';";
            ps = connection.prepareStatement(query); // returns the NEXT id that machine will assign to the table
            rs = ps.executeQuery();
            if (rs.next())
                gid_seed = rs.getInt("AUTO_INCREMENT");


            FileWriter fw = new FileWriter("inconsistencies_movies.txt");
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
                                                String gen = cat.getTextContent().replace("'", "").trim();
                                                if (gen.length() > 0) {
                                                    if (genre_map.get(gen) == null) {
                                                        genre_map.put(gen, gid_seed);
                                                        gid_seed++;
                                                    }
                                                    if (!genres.contains(gen))
                                                        genres.add(gen);
                                                }
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
                                if (director == null)
                                    fw.write("Inconsistency in mains243.xml - null director value: " + id + ", " + director + "\n");
                                if (title == null)
                                    fw.write("Inconsistency in mains243.xml - null title value: " + id + ", " + title + "\n");
                                if (year == null)
                                    fw.write("Inconsistency in mains243.xml - null year value: " + id + ", " + year + "\n");
                                if (id != null && director != null && title != null && year != null && !id.equals(" ") && !id.equals("") && movie_map.get(id) == null) {
                                    director = director.replace('~', ' ').replace("\\", "").replace("\'", "`").replace("\"", "`").replace("{", "").replace("}", "").trim();
                                    title = title.replace('~', ' ').replace("\\", "").replace("\'", "`").replace("\"", "`").replace("{", "").replace("}", "").trim();
                                    year = year.replace("~", "").replace("+", "").trim();
                                    try {
                                        Integer ryear = Integer.parseInt(year);
                                    } catch (NumberFormatException e) {
                                        //System.out.println("Inconsistency in mains243.xml - bad year value: " + id + ", " + title + ", " + year);
                                        fw.write("Inconsistency in mains243.xml - bad year value: " + id + ", " + title + ", " + year + "\n");
                                        year = null;
                                    }
                                    if (year != null) {
                                        Movie m = new Movie(id, title, year, director, directorId, genres);
                                        movie_map.put(id, m);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            fw.close();
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    public void parseActorsDocument() {
        try {
            FileWriter fw = new FileWriter("inconsistencies_actors.txt");
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
                                res = res.replace("{", "").replace("}", "");
                                name = res.trim();
                            }
                            if (actor_node.getNodeName().equals("dob") && !actor_node.getTextContent().equals("")) {
                                dob = actor_node.getTextContent().replace("~", "").replace("+", "").trim();
                                try {
                                    Integer byear = Integer.parseInt(dob);
                                } catch (NumberFormatException e) {
                                    //System.out.println("Inconsistency in actors63.xml - bad year value: " + name + ", " + dob);
                                    fw.write("Inconsistency in actors63.xml - bad year value: " + name + ", " + dob + "\n");
                                    dob = "bad year";
                                }
                            }
                        }
                        if (dob == null || !dob.equals("bad year")) {
                            Actor a = new Actor(name, dob);
                            actors.put(name, a);
                        }
                    }
                }
            }
            fw.close();
        } catch (IOException a) {
            a.printStackTrace();
        }
    }

    public void parseCastsDocument() {
        try {
            FileWriter fw = new FileWriter("inconsistencies_casts.txt");
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
                                            if (filmc_n.getNodeName().equals("a") && !filmc_n.getTextContent().equals("sa") && !filmc_n.getTextContent().equals("s a") && !filmc_n.getTextContent().equals("no\\_actor") && !filmc_n.getTextContent().equals("midget actor")) {
                                                String res = filmc_n.getTextContent().replace('~', ' ').replace("\\", "").replace("\'", "`");
                                                res = res.replace("\'", "`").replace("\"", "`");
                                                res = res.replace("{", "").replace("}", "").trim();
                                                if (!names.contains(res))
                                                    names.add(res);
                                            }
                                            if (filmc_n.getTextContent().equals("sa") || filmc_n.getTextContent().equals("s a") || filmc_n.getTextContent().equals("no\\_actor") || filmc_n.getTextContent().equals("midget actor")) {
                                                //System.out.println("Inconsistency in casts124.xml - bad actor name value: " + filmc_n.getTextContent());
                                                fw.write("Inconsistency in casts124.xml - bad actor name value: " + filmc_n.getTextContent() + "\n");
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
            fw.close();
        }catch (IOException a) {
            a.printStackTrace();
        }
    }

    private void printMoviesData() {
        for (String key : movie_map.keySet()) {
            System.out.println("\t" + movie_map.get(key).toString());
        }
    }

    private void printActorsData() {
        for (String name : actors.keySet()) {
            System.out.println("\t" + actors.get(name).toString());
        }
    }

    private void printCastsData() {
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

    public void popluateNewStarsInMovies() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String get_query = "select starId, movieId from stars_in_movies;";
            PreparedStatement s = connection.prepareStatement(get_query);
            ResultSet rs = s.executeQuery();
            String set_query = "insert into new_stars_in_movies(starId, movieId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(set_query);
            int x = 0;
            while (rs.next()) {
                x++;
                ps.setString(1, rs.getString("starId"));
                ps.setString(2, rs.getString("movieId"));
                ps.addBatch();
                if (x > 2000) {
                    x = 0;
                    ps.executeBatch();
                    ps = connection.prepareStatement(set_query);
                }
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertStars() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String query;
            PreparedStatement ps;
            ResultSet rs;

            // insert new stars
            Integer id_suff_int = -1;
//            query = "select max(id) as mid from new_stars;";
            query = "select max(id) as mid from stars;";
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            String max_id = "", starId = "";
            if (rs.next())
                max_id = rs.getString("mid");
            if (!max_id.equals("")) {
                String[] id_suff_list = max_id.split("nm");
                String id_suff_str = id_suff_list[1];
                id_suff_int = Integer.parseInt(id_suff_str);
                id_suff_int = id_suff_int + 1;
                starId = "nm" + id_suff_int;
            }

            int x = 1;

//            query = "insert into new_stars(id, name, birthYear) values (?, ?, ?)";
            query = "insert into stars(id, name, birthYear) values (?, ?, ?)";
            ps = connection.prepareStatement(query);
            for (String name : actors.keySet()) {
                ps.setString(1, starId);
                ps.setString(2, name);
                if (actors.get(name).getBirthyear() == null)
                    ps.setObject(3, null, Types.INTEGER);
                else
                    ps.setObject(3, Integer.parseInt(actors.get(name).getBirthyear()), Types.INTEGER);
                ps.addBatch();
                stars_map.put(name, new Actor(name, actors.get(name).getBirthyear(), starId));
                id_suff_int = id_suff_int + 1;
                starId = "nm" + id_suff_int;
                if (x > 2000) {
                    ps.executeBatch();
                    x = 0;
                }
                x++;
            }
            ps.executeBatch();

            x = 1;
//            String query2 = "insert into new_stars(id, name, birthYear) values (?, ?, null)";
            String query2 = "insert into stars(id, name, birthYear) values (?, ?, null)";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ArrayList<String> cast;
            for (String xmlId : cast_lists.keySet()) {
                cast = cast_lists.get(xmlId);
                for (String cast_member : cast) {
                    if (stars_map.get(cast_member) == null) {
                        ps2.setString(1, starId);
                        ps2.setString(2, cast_member);
                        ps2.addBatch();
                        stars_map.put(cast_member, new Actor(cast_member, null, starId));
                        id_suff_int = id_suff_int + 1;
                        starId = "nm" + id_suff_int;
                        if (x > 2000) { // there may be long gaps between unknown stars; these gaps may cause a timeout if not accounted for.
                            ps2.executeBatch();
                            x = 0;
                        }
                        x++;
                    }
                }
                if (x > 2000) { // there may be long gaps between unknown stars; these gaps may cause a timeout if not accounted for.
                    ps2.executeBatch();
                    x = 1;
                }
            }
            ps2.executeBatch();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertGenres() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            // insert new genres
//            String query = "insert into new_genres(name) values (?)";
            String query = "insert into genres(name) values (?)";
            PreparedStatement ps = connection.prepareStatement(query);
            for (String name : genre_map.keySet()) {
                ps.setString(1, name);
                ps.addBatch();
            }
            ps.executeBatch();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertMovies() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String max_id = "", movieId = "";
            Integer id_suff_int = -1;
//            String query = "select max(id) as mid from new_movies;";
            String query = "select max(id) as mid from movies;";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                max_id = rs.getString("mid");
            if (!max_id.equals("")) {
                String[] id_suff_list = max_id.split("tt");
                String id_suff_str = id_suff_list[1];
                id_suff_int = Integer.parseInt(id_suff_str);
                id_suff_int = id_suff_int + 1;
                movieId = "tt" + id_suff_int;
            }

//            String movies_query1 = "insert into new_movies (id, title, year, director) values (?, ?, ?, ?)";
//            String movies_query2 = "insert into new_movies_ratings (id, title, year, director, movieId, rating, numVotes) values (?, ?, ?, ?, ?, 0.0, 0)";
//            String movies_query3 = "insert into new_ratings (movieId, rating, numVotes) values (?, 0.0, 0)";
            String movies_query1 = "insert into movies (id, title, year, director) values (?, ?, ?, ?)";
            String movies_query2 = "insert into movies_ratings (id, title, year, director, movieId, rating, numVotes) values (?, ?, ?, ?, ?, 0.0, 0)";
            String movies_query3 = "insert into ratings (movieId, rating, numVotes) values (?, 0.0, 0)";
            PreparedStatement movies_ps1 = connection.prepareStatement(movies_query1);
            PreparedStatement movies_ps2 = connection.prepareStatement(movies_query2);
            PreparedStatement movies_ps3 = connection.prepareStatement(movies_query3);

            int ml_count = 1;
            for (String key : movie_map.keySet()) {
                Movie movie = movie_map.get(key);
                movie.setMovieId(movieId);
                movie_map.put(key, movie);
                //System.out.println(movieId + ", " + movie.getTitle() + ", " + movie.getYear() + ", " + movie.getDirector());

                movies_ps1.setString(1, movieId);
                movies_ps1.setString(2, movie.getTitle());
                movies_ps1.setInt(3, Integer.parseInt(movie.getYear()));
                movies_ps1.setString(4, movie.getDirector());
                movies_ps1.addBatch();

                movies_ps2.setString(1, movieId);
                movies_ps2.setString(2, movie.getTitle());
                movies_ps2.setInt(3, Integer.parseInt(movie.getYear()));
                movies_ps2.setString(4, movie.getDirector());
                movies_ps2.setString(5, movieId);
                movies_ps2.addBatch();

                movies_ps3.setString(1, movieId);
                movies_ps3.addBatch();

                if (ml_count > 2000) {
                    movies_ps1.executeBatch();
                    movies_ps2.executeBatch();
                    movies_ps3.executeBatch();
                    ml_count = 0;
                }
                ml_count++;

                id_suff_int = id_suff_int + 1;
                movieId = "tt" + id_suff_int;
            }
            movies_ps1.executeBatch();
            movies_ps2.executeBatch();
            movies_ps3.executeBatch();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertStarsMovies() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            int s_count = 1;
            //String stars_query1 = "insert into new_stars (name, birthYear) values (?, null)";
//            String stars_query2 = "insert into new_stars_in_movies (starId, movieId) values (?, ?)";
//            String stars_query3 = "insert into new_movies_stars (starId, movieId, name, title, year) values (?, ?, ?, ?, ?)";
//            String stars_query4 = "insert into new_sm_count (starId, movieId, name, count) values (?, ?, ?, 0)";
//            String stars_query5 = "insert into new_sm_count_movie_info (starId, movieId, name, count, title, year, director) values (?, ?, ?, 0, ?, ?, ?)";
            String stars_query2 = "insert into stars_in_movies (starId, movieId) values (?, ?)";
            String stars_query3 = "insert into movies_stars (starId, movieId, name, title, year) values (?, ?, ?, ?, ?)";
            String stars_query4 = "insert into sm_count (starId, movieId, name, count) values (?, ?, ?, 0)";
            String stars_query5 = "insert into sm_count_movie_info (starId, movieId, name, count, title, year, director) values (?, ?, ?, 0, ?, ?, ?)";
            //PreparedStatement stars_ps1 = connection.prepareStatement(stars_query1);
            PreparedStatement stars_ps2 = connection.prepareStatement(stars_query2);
            PreparedStatement stars_ps3 = connection.prepareStatement(stars_query3);
            PreparedStatement stars_ps4 = connection.prepareStatement(stars_query4);
            PreparedStatement stars_ps5 = connection.prepareStatement(stars_query5);

            String starId, movieId;
            Movie movie;

            for (String key : movie_map.keySet()) {
                ArrayList<String> stars = cast_lists.get(key);
                if (stars != null) {
                    movie = movie_map.get(key);
                    movieId = movie.getMovieId();
                    for (String star : stars) {
                        starId = stars_map.get(star).getId();
                        stars_ps2.setString(1, starId);
                        stars_ps2.setString(2, movieId);
                        stars_ps2.addBatch();

                        stars_ps3.setString(1, starId);
                        stars_ps3.setString(2, movieId);
                        stars_ps3.setString(3, star);
                        stars_ps3.setString(4, movie.getTitle());
                        stars_ps3.setInt(5, Integer.parseInt(movie.getYear()));
                        stars_ps3.addBatch();

                        stars_ps4.setString(1, starId);
                        stars_ps4.setString(2, movieId);
                        stars_ps4.setString(3, star);
                        stars_ps4.addBatch();

                        stars_ps5.setString(1, starId);
                        stars_ps5.setString(2, movieId);
                        stars_ps5.setString(3, star);
                        stars_ps5.setString(4, movie.getTitle());
                        stars_ps5.setInt(5, Integer.parseInt(movie.getYear()));
                        stars_ps5.setString(6, movie.getDirector());
                        stars_ps5.addBatch();

                        if (s_count > 2000) {
                            stars_ps2.executeBatch();
                            stars_ps3.executeBatch();
                            stars_ps4.executeBatch();
                            stars_ps5.executeBatch();
                            s_count = 0;
                        }
                        s_count++;
                    }
                }
            }
            stars_ps2.executeBatch();
            stars_ps3.executeBatch();
            stars_ps4.executeBatch();
            stars_ps5.executeBatch();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertGenresMovies() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            int g_count = 1;
            //String genres_query2 = "insert into stg_gm (genreId, movieId) values (?, ?)";
//            String genres_query2 = "insert into new_genres_in_movies (genreId, movieId) values (?, ?)";
//            String genres_query3 = "insert into new_movies_genres (genreId, movieId, name, title) values (?, ?, ?, ?)";
            String genres_query2 = "insert into genres_in_movies (genreId, movieId) values (?, ?)";
            String genres_query3 = "insert into movies_genres (genreId, movieId, name, title) values (?, ?, ?, ?)";
            PreparedStatement genres_ps2 = connection.prepareStatement(genres_query2);
            PreparedStatement genres_ps3 = connection.prepareStatement(genres_query3);

            String movieId;
            Movie movie;

            for (String key : movie_map.keySet()) {
                movie = movie_map.get(key);
                movieId = movie.getMovieId();
                ArrayList<String> genres = movie.getGenres();
                for (String genre : genres) {
                    Integer genreId = genre_map.get(genre);

                    genres_ps2.setInt(1, genreId);
                    genres_ps2.setString(2, movieId);
                    genres_ps2.addBatch();

                    genres_ps3.setInt(1, genreId);
                    genres_ps3.setString(2, movieId);
                    genres_ps3.setString(3, genre);
                    genres_ps3.setString(4, movie.getTitle());
                    genres_ps3.addBatch();

                    if (g_count > 2000) {
                        genres_ps2.executeBatch();
                        genres_ps3.executeBatch();
                        g_count = 0;
                    }
                    g_count++;
                }
            }
            genres_ps2.executeBatch();
            genres_ps3.executeBatch();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertListTable() {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

//            String query = "select max(rowid_rating) as mr, max(rowid_title) as mt from new_list_table;";
            String query = "select max(rowid_rating) as mr, max(rowid_title) as mt from list_table;";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            Integer mr = -1, mt = -1;
            if (rs.next()) {
                mr = rs.getInt("mr") + 1;
                mt = rs.getInt("mt") + 1;
            }

            int l_count = 1;
//            String list_table_query = "insert into new_list_table(id, title, year, director, rating, genre, star, starId, rowid_rating, rowid_title) values (?, ?, ?, ?, 0.0, ?, ?, ?, ?, ?)";
            String list_table_query = "insert into list_table(id, title, year, director, rating, genre, star, starId, rowid_rating, rowid_title) values (?, ?, ?, ?, 0.0, ?, ?, ?, ?, ?)";
            PreparedStatement list_table_ps = connection.prepareStatement(list_table_query);


            String movieId;
            Movie movie;

            for (String key : movie_map.keySet()) {
                movie = movie_map.get(key);
                movieId = movie.getMovieId();
                ArrayList<String> stars = cast_lists.get(key);
                ArrayList<String> genres = movie.getGenres();
                String genre_concat = "", star_concat = "", starId_concat = "";

                if (stars != null) {
                    for (int s = 0; s < stars.size(); s++) {
                        if (s < 3) {
                            star_concat += stars.get(s) + ", ";
                            starId_concat += stars_map.get(stars.get(s)).getId() + ", ";
                        }
                    }
                }
                if (genres != null) {
                    for (int g = 0; g < genres.size(); g++) {
                        if (g < 3) {
                            genre_concat += genres.get(g) + ", ";
                        }
                    }
                }
                if (star_concat.length() > 2)
                    star_concat = star_concat.substring(0, star_concat.length()-2);
                if (starId_concat.length() > 2)
                    starId_concat = starId_concat.substring(0, starId_concat.length()-2);
                if (genre_concat.length() > 2)
                    genre_concat = genre_concat.substring(0, genre_concat.length()-2);

                list_table_ps.setString(1, movieId);
                list_table_ps.setString(2, movie.getTitle());
                list_table_ps.setInt(3, Integer.parseInt(movie.getYear()));
                list_table_ps.setString(4, movie.getDirector());
                list_table_ps.setString(5, genre_concat);
                list_table_ps.setString(6, star_concat);
                list_table_ps.setString(7, starId_concat);
                list_table_ps.setInt(8, mr);
                list_table_ps.setInt(9, mt);
                list_table_ps.addBatch();

                if (l_count > 2000) {
                    list_table_ps.executeBatch();
                    l_count = 0;
                }
                l_count++;
                mr++;
                mt++;
            }
            list_table_ps.executeBatch();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runSQLInsertStatements() {

        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true";
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            String max_id = "", movieId = "";
            Integer id_suff_int = -1;
            String query = "select max(id) as mid from new_movies;";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                max_id = rs.getString("mid");
            if (!max_id.equals("")) {
                String[] id_suff_list = max_id.split("tt");
                String id_suff_str = id_suff_list[1];
                id_suff_int = Integer.parseInt(id_suff_str);
                id_suff_int = id_suff_int + 1;
                movieId = "tt" + id_suff_int;
            }

            int ml_count = 1;
            String movies_query1 = "insert into new_movies (id, title, year, director) values (?, ?, ?, ?)";
            String movies_query2 = "insert into new_movies_ratings (id, title, year, director, rating, numVotes) values (?, ?, ?, ?, 0.0, 0)";
            String movies_query3 = "insert into new_ratings (id, rating, numVotes) values (?, 0.0, 0)";
            PreparedStatement movies_ps1 = connection.prepareStatement(movies_query1);
            PreparedStatement movies_ps2 = connection.prepareStatement(movies_query2);
            PreparedStatement movies_ps3 = connection.prepareStatement(movies_query3);

            int s_count = 1;
            //String stars_query1 = "insert into new_stars (name, birthYear) values (?, null)";
            String stars_query2 = "insert into new_stars_in_movies (starId, movieId) values (?, ?)";
            String stars_query3 = "insert into new_movies_stars (starId, movieId, name, title, year) values (?, ?, ?, ?, ?)";
            String stars_query4 = "insert into new_sm_count (starId, movieId, name, count) values (?, ?, ?, 0)";
            String stars_query5 = "insert into new_sm_count_movie_info (starId, movieId, name, count, title, year, director) values (?, ?, ?, 0, ?, ?, ?)";
            //PreparedStatement stars_ps1 = connection.prepareStatement(stars_query1);
            PreparedStatement stars_ps2 = connection.prepareStatement(stars_query2);
            PreparedStatement stars_ps3 = connection.prepareStatement(stars_query3);
            PreparedStatement stars_ps4 = connection.prepareStatement(stars_query4);
            PreparedStatement stars_ps5 = connection.prepareStatement(stars_query5);

            int g_count = 1;
            //String genres_query1 = "insert into new_genres (name) values (?)";
            String genres_query2 = "insert into new_genres_in_movies (genreId, movieId) values (?, ?)";
            String genres_query3 = "insert into new_movies_genres (genreId, movieId, name, title) values (?, ?, ?, ?)";
            //PreparedStatement genres_ps1 = connection.prepareStatement(genres_query1);
            PreparedStatement genres_ps2 = connection.prepareStatement(genres_query2);
            PreparedStatement genres_ps3 = connection.prepareStatement(genres_query3);

            String list_table_query = "insert into new_list_table(id, title, year, director, rating, genre, star, starId, rowid_rating, rowid_title) values (?, ?, ?, ?, 0.0, ?, ?, ?, null, null)";
            PreparedStatement list_table_ps = connection.prepareStatement(list_table_query);

            for (String key : movie_map.keySet()) {
                System.out.println("id: " + movieId);
                Movie movie = movie_map.get(key);

                movies_ps1.setString(1, movieId);
                movies_ps1.setString(2, movie.getTitle());
                movies_ps1.setInt(3, Integer.parseInt(movie.getYear()));
                movies_ps1.setString(4, movie.getDirector());
                movies_ps1.addBatch();

                movies_ps2.setString(1, movieId);
                movies_ps2.setString(2, movie.getTitle());
                movies_ps2.setInt(3, Integer.parseInt(movie.getYear()));
                movies_ps2.setString(4, movie.getDirector());
                movies_ps2.addBatch();

                movies_ps3.setString(1, movieId);
                movies_ps3.addBatch();

                ArrayList<String> stars = cast_lists.get(key);
                if (stars != null) {
                    String sid = "";
                    query = "select max(id) as sid from stars;";
                    ps = connection.prepareStatement(query);
                    rs = ps.executeQuery();
                    String max_Star_id = "";
                    Integer id_suff_star = -1;
                    if (rs.next())
                        max_Star_id = rs.getString("sid");
                    if (!max_Star_id.equals("")) {
                        String[] id_suff_list_star = max_Star_id.split("nm");
                        String id_suff_str_star = id_suff_list_star[1];
                        id_suff_star = Integer.parseInt(id_suff_str_star);
                        id_suff_star = id_suff_star + 1;
                        sid = "nm" + id_suff_star;
                    }

                    for (String star : stars) {
                        System.out.println("getting from known stars - id: " + movieId);
                        String starid = stars_map.get(star).getId();
                        System.out.println("\t\tstarid: " + starid);

//                    query = "insert into stars_in_movies (starId, movieId) values ('" + starid + "', '" + movieId + "');";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                        stars_ps2.setString(1, starid);
                        stars_ps2.setString(2, movieId);
                        stars_ps2.addBatch();

//                    query = "insert into movies_stars (starId, movieId, name, title, year) values ('" + starid + "', '" + movieId + "', '" + star + "', '" + movie.getTitle() + "', " + movie.getYear() + ");";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                        stars_ps3.setString(1, starid);
                        stars_ps3.setString(2, movieId);
                        stars_ps3.setString(3, star);
                        stars_ps3.setString(4, movie.getTitle());
                        stars_ps3.setInt(5, Integer.parseInt(movie.getYear()));
                        stars_ps3.addBatch();

//                    query = "insert into sm_count (starId, movieId, name, count) values ('" + starid + "', '" + movieId + "', '" + star + "', 0);";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                        stars_ps4.setString(1, starid);
                        stars_ps4.setString(2, movieId);
                        stars_ps4.setString(3, star);
                        stars_ps4.addBatch();

//                    query = "insert into sm_count_movie_info (starId, movieId, name, count, title, year, director) values ('" + starid + "', '" + movieId + "', '" + star + "', 0, '" + movie.getTitle() + "', " + movie.getYear() + ", '" + movie.getDirector() + "');";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                        stars_ps5.setString(1, starid);
                        stars_ps5.setString(2, movieId);
                        stars_ps5.setString(3, star);
                        stars_ps5.setString(4, movie.getTitle());
                        stars_ps5.setInt(5, Integer.parseInt(movie.getYear()));
                        stars_ps5.setString(6, movie.getDirector());
                        stars_ps5.addBatch();

                        if (s_count > 2000) {
                            if (ml_count > 0) {
                                System.out.println("ml_count = " + ml_count);
                                System.out.println("\t" + movieId + ", " + movie.getTitle() + ", " + movie.getYear() + ", " + movie.getDirector());
                                movies_ps1.executeBatch();
                                movies_ps2.executeBatch();
                                movies_ps3.executeBatch();
                                list_table_ps.executeBatch();
                                ml_count = 0;
                            }
                            stars_ps2.executeBatch();
                            stars_ps3.executeBatch();
                            stars_ps4.executeBatch();
                            stars_ps5.executeBatch();
                            s_count = 0;
                        }
                        s_count++;
//                    if (known_stars.get(star) == null) {
//                        String y = null;
//                        if (actors.get(star) != null)
//                            y = actors.get(star).getBirthyear();
//                        known_stars.put(star, new Actor(star, y, sid));
//                        System.out.println(" known stars is null for id: " + movieId);
////                        query = "insert into stars (name, birthYear) values ('" + star + "', null);";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps1.setString(1, star);
//                        stars_ps1.addBatch();
//
////                        query = "insert into stars_in_movies (starId, movieId) values ('" + sid + "', '" + movieId + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps2.setString(1, sid);
//                        stars_ps2.setString(2, movieId);
//                        stars_ps2.addBatch();
//
////                        query = "insert into movies_stars (starId, movieId, name, title, year) values ('" + sid + "', '" + movieId + "', '" + star + "', '" + movie.getTitle() + "', " + movie.getYear() + ");";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps3.setString(1, sid);
//                        stars_ps3.setString(2, movieId);
//                        stars_ps3.setString(3, star);
//                        stars_ps3.setString(4, movie.getTitle());
//                        stars_ps3.setObject(5, Integer.parseInt(movie.getYear()), Types.INTEGER);
//                        stars_ps3.addBatch();
//
////                        query = "insert into sm_count (starId, movieId, name, count) values ('" + sid + "', '" + movieId + "', '" + star + "', 0);";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps4.setString(1, sid);
//                        stars_ps4.setString(2, movieId);
//                        stars_ps4.setString(3, star);
//                        stars_ps4.addBatch();
//
////                        query = "insert into sm_count_movie_info (starId, movieId, name, count, title, year, director) values ('" + sid + "', '" + movieId + "', '" + star + "', 0, '" + movie.getTitle() + "', " + movie.getYear() + ", '" + movie.getDirector() + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps5.setString(1, sid);
//                        stars_ps5.setString(2, movieId);
//                        stars_ps5.setString(3, star);
//                        stars_ps5.setString(4, movie.getTitle());
//                        stars_ps5.setObject(5, Integer.parseInt(movie.getYear()), Types.INTEGER);
//                        stars_ps5.setString(6, movie.getDirector());
//                        stars_ps5.addBatch();
//
//                        String[] id_suff_list_star = sid.split("nm");
//                        String id_suff_str_star = id_suff_list_star[1];
//                        id_suff_star = Integer.parseInt(id_suff_str_star);
//                        id_suff_star = id_suff_star + 1;
//                        sid = "nm" + id_suff_star;
//                    }
//                    else {
//                        System.out.println("getting from known stars - id: " + movieId);
//                        String starid = known_stars.get(star).getId();
//                        System.out.println("\t\tstarid: " + starid);
//
////                        query = "insert into stars_in_movies (starId, movieId) values ('" + starid + "', '" + movieId + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps2.setString(1, starid);
//                        stars_ps2.setString(2, movieId);
//                        stars_ps2.addBatch();
//
////                        query = "insert into movies_stars (starId, movieId, name, title, year) values ('" + starid + "', '" + movieId + "', '" + star + "', '" + movie.getTitle() + "', " + movie.getYear() + ");";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps3.setString(1, starid);
//                        stars_ps3.setString(2, movieId);
//                        stars_ps3.setString(3, star);
//                        stars_ps3.setString(4, movie.getTitle());
//                        stars_ps3.setObject(5, Integer.parseInt(movie.getYear()), Types.INTEGER);
//                        stars_ps3.addBatch();
//
////                        query = "insert into sm_count (starId, movieId, name, count) values ('" + starid + "', '" + movieId + "', '" + star + "', 0);";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps4.setString(1, starid);
//                        stars_ps4.setString(2, movieId);
//                        stars_ps4.setString(3, star);
//                        stars_ps4.addBatch();
//
////                        query = "insert into sm_count_movie_info (starId, movieId, name, count, title, year, director) values ('" + starid + "', '" + movieId + "', '" + star + "', 0, '" + movie.getTitle() + "', " + movie.getYear() + ", '" + movie.getDirector() + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        stars_ps5.setString(1, starid);
//                        stars_ps5.setString(2, movieId);
//                        stars_ps5.setString(3, star);
//                        stars_ps5.setString(4, movie.getTitle());
//                        stars_ps5.setObject(5, Integer.parseInt(movie.getYear()), Types.INTEGER);
//                        stars_ps5.setString(6, movie.getDirector());
//                        stars_ps5.addBatch();
//                    }
                    }
                }

                ArrayList<String> genres = movie.getGenres();
//                Integer gid = -1; // our genre ID
//                query = "select max(id) as gid from genres;";
//                ps = connection.prepareStatement(query);
//                rs = ps.executeQuery();
//                if (rs.next())
//                    gid = rs.getInt("gid");
                for (String genre : genres) {
                    Integer genreid = genre_map.get(genre);

//                    query = "insert into genres_in_movies (genreId, movieId) values (" + genreid + ", '" + movieId + "');";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                    genres_ps2.setInt(1, genreid);
                    genres_ps2.setString(2, movieId);
                    genres_ps2.addBatch();

//                    query = "insert into movies_genres (genreId, movieId, name, title) values (" + genreid + ", '" + movieId + "', '" + genre + "', '" + movie.getTitle() + "');";
//                    ps = connection.prepareStatement(query);
//                    ps.executeUpdate();
                    genres_ps3.setInt(1, genreid);
                    genres_ps3.setString(2, movieId);
                    genres_ps3.setString(3, genre);
                    genres_ps3.setString(4, movie.getTitle());
                    genres_ps3.addBatch();

                    if (g_count > 2000) {
                        if (ml_count > 0) {
                            movies_ps1.executeBatch();
                            movies_ps2.executeBatch();
                            movies_ps3.executeBatch();
                            list_table_ps.executeBatch();
                            ml_count = 0;
                        }
                        genres_ps2.executeBatch();
                        genres_ps3.executeBatch();
                        g_count = 0;
                    }
                    g_count++;
//                    if (genre_map.get(genre) == null) {
//
//                        genre_map.put(genre, gid);
//
////                        query = "insert into genres (name) values ('" + genre + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        genres_ps1.setString(1, genre);
//                        genres_ps1.addBatch();
//
////                        query = "insert into genres_in_movies (genreId, movieId) values (" + gid + ", '" + movieId + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        genres_ps2.setInt(1, gid);
//                        genres_ps2.setString(2, movieId);
//                        genres_ps2.addBatch();
//
////                        query = "insert into movies_genres (genreId, movieId, name, title) values (" + gid + ", '" + movieId + "', '" + genre + "', '" + movie.getTitle() + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        genres_ps3.setInt(1, gid);
//                        genres_ps3.setString(2, movieId);
//                        genres_ps3.setString(3, genre);
//                        genres_ps3.setString(4, movie.getTitle());
//                        genres_ps3.addBatch();
//
//                        gid = gid + 1;
//                    }
//                    else {
//                        Integer genreid = genre_map.get(genre);
//
////                        query = "insert into genres_in_movies (genreId, movieId) values (" + genreid + ", '" + movieId + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        genres_ps2.setInt(1, genreid);
//                        genres_ps2.setString(2, movieId);
//                        genres_ps2.addBatch();
//
////                        query = "insert into movies_genres (genreId, movieId, name, title) values (" + genreid + ", '" + movieId + "', '" + genre + "', '" + movie.getTitle() + "');";
////                        ps = connection.prepareStatement(query);
////                        ps.executeUpdate();
//                        genres_ps3.setInt(1, genreid);
//                        genres_ps3.setString(2, movieId);
//                        genres_ps3.setString(3, genre);
//                        genres_ps3.setString(4, movie.getTitle());
//                        genres_ps3.addBatch();
//                    }
                }

                String genre_concat = "", star_concat = "", starId_concat = "";

                if (stars != null) {
                    for (int s = 0; s < stars.size(); s++) {
                        if (s < 3) {
                            star_concat += stars.get(s) + ", ";
                            starId_concat += stars_map.get(stars.get(s)).getId() + ", ";
                        }
                    }
                }
                if (genres != null) {
                    for (int g = 0; g < genres.size(); g++) {
                        if (g < 3) {
                            genre_concat += genres.get(g) + ", ";
                        }
                    }
                }
                if (star_concat.length() > 2)
                    star_concat = star_concat.substring(0, star_concat.length()-2);
                if (starId_concat.length() > 2)
                    starId_concat = starId_concat.substring(0, starId_concat.length()-2);
                if (genre_concat.length() > 2)
                    genre_concat = genre_concat.substring(0, genre_concat.length()-2);


//                query = "insert into list_table(id, title, year, director, rating, genre, star, starId, rowid_rating, rowid_title) " +
//                        "values ('" + movieId + "', '" + movie.getTitle() + "', " + movie.getYear() + ", '" + movie.getDirector() +
//                        "', 0.0,'" +  genre_concat + "', '" +  star_concat + "', '" +  starId_concat + "', null, null);";
//                ps = connection.prepareStatement(query);
//                ps.executeUpdate();
                list_table_ps.setString(1, movieId);
                list_table_ps.setString(2, movie.getTitle());
                list_table_ps.setInt(3, Integer.parseInt(movie.getYear()));
                list_table_ps.setString(4, movie.getDirector());
                list_table_ps.setString(5, genre_concat);
                list_table_ps.setString(6, star_concat);
                list_table_ps.setString(7, starId_concat);
                list_table_ps.addBatch();

                if (ml_count > 2000) {
                    movies_ps1.executeBatch();
                    movies_ps2.executeBatch();
                    movies_ps3.executeBatch();
                    list_table_ps.executeBatch();
                    ml_count = 0;
                }
                ml_count++;

                System.out.println("finished processing movieId " + movieId);
                id_suff_int = id_suff_int + 1;
                movieId = "tt" + id_suff_int;
                System.out.println("now processing movieId " + movieId);
            }
            movies_ps1.executeBatch();
            movies_ps2.executeBatch();
            movies_ps3.executeBatch();
            //stars_ps1.executeBatch();
            stars_ps2.executeBatch();
            stars_ps3.executeBatch();
            stars_ps4.executeBatch();
            stars_ps5.executeBatch();
            //genres_ps1.executeBatch();
            genres_ps2.executeBatch();
            genres_ps3.executeBatch();
            list_table_ps.executeBatch();

            rs.close();
            ps.close();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DomParserACM dpa = new DomParserACM();

        dpa.runMoviesExample();
        dpa.runActorsExample();
        dpa.runCastsExample();

        dpa.insertGenres();
        dpa.insertStars();
        dpa.insertMovies();
        dpa.insertStarsMovies();
        dpa.insertGenresMovies();
        dpa.insertListTable();
    }
}