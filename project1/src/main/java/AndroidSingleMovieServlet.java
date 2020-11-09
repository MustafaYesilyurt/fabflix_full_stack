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

@WebServlet(name = "AndroidSingleMovieServlet", urlPatterns = "/api/android-single-movie")
public class AndroidSingleMovieServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        JsonObject responseJsonObject = new JsonObject();

        if (id == null) {
            System.out.println("ANDROID_ERROR(ListViewActivity): null parameter - id");
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
            String query0 = "select title, year, director, rating from movies_ratings where id = ?;";

            String query1 = "select name from movies_genres where movieId = ?;";

            String query2 = "select name, starId from sm_count_movie_info where movieId = ? order by count desc, name;";

            PreparedStatement ps0 = connection.prepareStatement(query0);
            PreparedStatement ps1 = connection.prepareStatement(query1);
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps0.setString(1, id);
            ps1.setString(1, id);
            ps2.setString(1, id);

            ResultSet rs0 = ps0.executeQuery();
            ResultSet rs1 = ps1.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            JsonObject rs1JsonObject = new JsonObject();
            JsonObject rs2JsonObject = new JsonObject();

            if (rs0.next()) {
                responseJsonObject.addProperty("title", rs0.getString("title"));
                responseJsonObject.addProperty("year", "" + rs0.getInt("year"));
                responseJsonObject.addProperty("director", rs0.getString("director"));
                responseJsonObject.addProperty("rating", rs0.getFloat("rating"));
            }
            System.out.println("\nrs0JsonObject:\n" + responseJsonObject.toString() + "\n");

            int x = 0;
            while (rs1.next()) {
                rs1JsonObject.addProperty("genre" + x, rs1.getString("name"));
                x++;
            }
            System.out.println("\nrs1JsonObject:\n" + rs1JsonObject.toString() + "\n");
            responseJsonObject.add("genres", rs1JsonObject);

            x = 0;
            while (rs2.next()) {
                rs2JsonObject.addProperty("stars" + x, rs2.getString("name"));
                x++;
            }
            System.out.println("\nrs2JsonObject:\n" + rs2JsonObject.toString() + "\n");
            responseJsonObject.add("stars", rs2JsonObject);

            System.out.println("\nresponseJsonObject:\n" + responseJsonObject.toString() + "\n\n");
            response.getWriter().write(responseJsonObject.toString());

            rs0.close();
            rs1.close();
            rs2.close();
            ps0.close();
            ps1.close();
            ps2.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
