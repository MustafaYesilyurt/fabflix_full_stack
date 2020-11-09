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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "DescribeTableServlet", urlPatterns = "/describe")
public class DescribeTableServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String table = request.getParameter("table");
        if (!table.equals("movies") && !table.equals("ratings") && !table.equals("stars") && !table.equals("stars_in_movies") && !table.equals("genres")
                && !table.equals("genres_in_movies") && !table.equals("sales") && !table.equals("creditcards") && !table.equals("customers") && !table.equals("employees")) {
            table = "movies";
        }

        String query = "describe " + table + ";";

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
//        out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>");

        out.println("<head><title>Table Metadata - " + table + " </title></head>\n" +
                "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Table Metadata - " + table + " </title></head>\n" +
//                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                "    integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                "\n" +
//                "        <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("<body>");
        out.println("<h2>MovieDB: Metadata for " + table + "</h2>");
        out.println("<button onclick=\"dashboardRedirect()\">Back to Dashboard</button>\n" +
                "<script>\n" +
                "function dashboardRedirect() {\n" +
                "   window.location.replace(\"dashboard.html\");\n" +
                "}\n" +
                "</script><br>");

        out.println("<table border>");
        out.println("<tr>");
        out.println("<td>Field</td>");
        out.println("<td>Type</td>");
        out.println("<td>Null</td>");
        out.println("<td>Key</td>");
        out.println("<td>Default</td>");
        out.println("<td>Extra</td>");
        out.println("</tr>");

        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            PreparedStatement ps = connection.prepareStatement(query);
            System.out.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String is_null = rs.getString("Null");
                String key = rs.getString("Key");
                String def_val = rs.getString("Default");
                String extra = rs.getString("Extra");
                out.println("<tr>");
                out.println("<td>" + field + "</td>");
                out.println("<td>" + type + "</td>");
                out.println("<td>" + is_null + "</td>");
                out.println("<td>" + key + "</td>");
                out.println("<td>" + def_val + "</td>");
                out.println("<td>" + extra + "</td>");
                out.println("</tr>");
            }
            rs.close();
            connection.close();
            out.println("</table>");
            out.println("</body>");

        }
        catch (Exception e) {
            e.printStackTrace();
            out.println("<body>");
            out.println("<p>");
            out.println("Exception in describe: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }
//        out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>");
        out.println("</html>");
        out.close();
    }
}
