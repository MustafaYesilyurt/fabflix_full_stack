package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "AndroidMovieListServlet", urlPatterns = "/api/android-movie-list")
public class AndroidMovieListServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String offset_p = request.getParameter("offset");
        String leda = request.getParameter("leda");
        Integer offset = 0;
        if (offset_p != null) {
            offset = Integer.parseInt(offset_p);
        }
        JsonObject responseJsonObject = new JsonObject();

        if (title == null) {
            System.out.println("ANDROID_ERROR(MainActivity): null parameter - title");
            responseJsonObject.addProperty("input", "null");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();
            String query;
            if (leda.equals("0"))
                query = "select * from list_table where title like ? or match(title) against(? in boolean mode) order by rating desc, title limit 20 offset ?;";
            else
                query = "select t.id, t.title, t.year, t.director, t.rating, t.genre, t.star from (select levenshtein(title, ?) as lev, id, title, year, director, rating, genre, star, starId from list_table) as t where t.lev <= ? or match(t.title) against(? in boolean mode) order by t.rating desc, t.title limit 20 offset ?;";

            PreparedStatement ps = connection.prepareStatement(query);

            String[] stop_array = {"a", "am", "an", "and", "are", "as", "at", "be", "by", "for", "in", "of", "the", "to"};
            ArrayList<String> stop_words = new ArrayList<String>();
            for (String w : stop_array)
                stop_words.add(w);
            String [] title_words = title.split(" ");
            String new_title = "";
            for (String w : title_words) {
                if (!w.equals(" ") && !w.equals("")) {
                    if (stop_words.contains(w.toLowerCase()))
                        new_title += w + " ";
                    else
                        new_title += w + "* ";
                }
            }
            if (leda.equals("0")) {
                ps.setString(1, title);
                ps.setString(2, new_title.substring(0, new_title.length() - 1));
                ps.setInt(3, offset);
            }
            else {
                Integer num;
                if (title.length() <= 8)
                    num = 2;
                else if (title.length() <= 16)
                    num = 4;
                else if (title.length() <= 24)
                    num = 7;
                else
                    num = title.length()/3;

                ps.setString(1, title);
                ps.setInt(2, num);
                ps.setString(3, new_title.substring(0, new_title.length() - 1));
                ps.setInt(4, offset);
            }
            ResultSet rs = ps.executeQuery();

            int x = 0;
            while (rs.next()) {
                JsonObject j = new JsonObject();
                j.addProperty("id", rs.getString("id"));
                j.addProperty("title", rs.getString("title"));
                j.addProperty("year", "" + rs.getInt("year"));
                j.addProperty("director", rs.getString("director"));
                j.addProperty("rating", rs.getFloat("rating"));
                j.addProperty("genres", rs.getString("genre"));
                j.addProperty("stars", rs.getString("star"));
                responseJsonObject.add("item" + x, j);
                x++;
            }
            System.out.println(responseJsonObject.toString());
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
