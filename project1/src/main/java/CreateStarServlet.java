package main.java;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

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
import java.sql.Types;

@WebServlet(name = "CreateStarServlet", urlPatterns = "/create-star")
public class CreateStarServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starname = request.getParameter("name");
        String staryear = request.getParameter("byear");
        String newId = "";
        Integer birthyear = -1;

        System.out.println("staryear = " + staryear);

        if (starname.contains(","))
            starname = starname.replace(",", "");
        if (staryear == null || staryear.length() < 4) {
            birthyear = null;
            System.out.println("birthyear = null");
        }
        else {
            birthyear = Integer.parseInt(staryear);
            System.out.println("birthyear = " + birthyear);
        }
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
                newId = "nm" + id_suff_int;
            }
            System.out.println("newId = " + newId);
            System.out.println("starname = " + starname);
            String query2 = "insert into stars values (?, ?, ?);";
            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1, newId);
            ps2.setString(2, starname);
            ps2.setObject(3, birthyear, Types.INTEGER);
            ps2.executeUpdate();
            responseJsonObject.addProperty("status", "success");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
