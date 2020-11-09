package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet("/movie-suggestion")
public class MovieSuggestionServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonArray jsonArray = new JsonArray();

        // get the query string from parameter
        String query = request.getParameter("query");

        // return the empty json array if query is null or empty
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write(jsonArray.toString());
            return;
        }

        query = query.trim();

        HttpSession session = request.getSession();
        HashMap<String, ArrayList<Pair<String, String>>> cache = (HashMap<String, ArrayList<Pair<String, String>>>)session.getAttribute("cache");
        if (cache == null)
            cache = new HashMap<String, ArrayList<Pair<String, String>>>();
        ArrayList<Pair<String, String>> stored_query_results = cache.get(query);
        if (stored_query_results == null) {
            try {
//                Connection connection = dataSource.getConnection();
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
                Connection connection = ds.getConnection();

                String list_table_query = "select id, title from list_table where match(title) against(? in boolean mode) or title like ? limit 10;";
                PreparedStatement ps = connection.prepareStatement(list_table_query);
                String[] title_words = query.split(" ");
                String new_title = "";
                for (String w : title_words) {
                    if (!w.equals(" ") && !w.equals(""))
                        new_title += w + "* ";
                }
                ps.setString(1, new_title.substring(0, new_title.length() - 1));
                ps.setString(2, "%" + query + "%");
                ResultSet rs = ps.executeQuery();
                ArrayList<Pair<String, String>> query_results = new ArrayList<Pair<String, String>>();
                while (rs.next()) {
                    query_results.add(new Pair<String, String>(rs.getString("id"), rs.getString("title")));
                }
                cache.put(query, query_results);

                for (Pair<String, String> p : query_results) {
                    jsonArray.add(generateJsonObject(p.getKey(), p.getValue()));
                }
                response.getWriter().write(jsonArray.toString());
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500, e.getMessage());
            }
        }
        else {
            for (Pair<String, String> p : stored_query_results) {
                jsonArray.add(generateJsonObject(p.getKey(), p.getValue()));
            }
            response.getWriter().write(jsonArray.toString());
        }
    }

    private static JsonObject generateJsonObject(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", id);
        additionalDataJsonObject.addProperty("title", title.replace(' ', '+').replace('&', '_'));

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}
