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
import java.sql.*;

@WebServlet(name = "CreateMovieServlet", urlPatterns = "/create-movie")
public class CreateMovieServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String title = request.getParameter("title");
        Integer year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String starname = request.getParameter("star");
        String genre = request.getParameter("genre");
        String newStarId = "", newMovieId = "";

        if (starname.contains(","))
            starname = starname.replace(",", "");
        JsonObject responseJsonObject = new JsonObject();
        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb_write");
            Connection connection = ds.getConnection();

            String query = "select max(id) as mid from stars;";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            String sql_id = "";
            if (rs.next())
                sql_id = rs.getString("mid");
            if (!sql_id.equals("")) {
                String[] id_suff_list = sql_id.split("nm");
                String id_suff_str = id_suff_list[1];
                Integer id_suff_int = Integer.parseInt(id_suff_str);
                id_suff_int = id_suff_int + 1;
                newStarId = "nm" + id_suff_int;
            }

            String query2 = "select max(id) as mid from movies;";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ResultSet rs2 = ps2.executeQuery();
            String sql_id2 = "";
            if (rs2.next())
                sql_id2 = rs2.getString("mid");
            if (!sql_id2.equals("")) {
                String[] id_suff_list = sql_id2.split("tt");
                String id_suff_str = id_suff_list[1];
                Integer id_suff_int = Integer.parseInt(id_suff_str);
                id_suff_int = id_suff_int + 1;
                newMovieId = "tt" + id_suff_int;
            }

            System.out.println("newStarId = " + newStarId);
            System.out.println("newMovieId = " + newMovieId);
            System.out.println("title = " + title);
            System.out.println("year = " + year);
            System.out.println("director = " + director);
            System.out.println("starname = " + starname);
            System.out.println("genre = " + genre);

            String query3 = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = connection.prepareCall(query3);
            stmt.setString(1, newMovieId);
            stmt.setString(2, title);
            stmt.setInt(3, year);
            stmt.setString(4, director);
            stmt.setString(5, starname);
            stmt.setString(6, newStarId);
            stmt.setString(7, genre);
            ResultSet rs3 = stmt.executeQuery();
            if (rs3.next()) {
                String resp = rs3.getString("response");
                if (resp.equals("Movie created successfully."))
                    responseJsonObject.addProperty("status", "success");
                else
                    responseJsonObject.addProperty("status", "failure");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
