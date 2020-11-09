package main.java;

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
import java.sql.*;
import java.util.Random;

@WebServlet("/single-star")
public class SingleStarServlet extends HttpServlet{
    private static final long serialVersionUID = 3L;

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        //String url = "http://" + request.getServerName() + ":8080/cs122b-spring20-project1.war/";
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/cs122b-spring20-project1/";
        String name2 = request.getParameter("name");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        if (name == null)
            name = "";
        else
            name = name.replace('+', ' ');
        String sbit_p = request.getParameter("sbit");
        boolean sb, bb;
        String bbit_p = request.getParameter("bbit");
        if (sbit_p == null)
            sb = false;
        else
            sb = true;

        if (bbit_p == null)
            bb = false;
        else
            bb = true;
//id=" + id + "&
        // set response mime type
        response.setContentType("text/html");
        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix - " + name + "</title></head>" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Fabflix - " + name + "</title></head>" +
//                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                "          integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"style.css\">");
        Random rand = new Random();
        try {
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            //Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            // prepare query
            String query0 = "select s.name, s.birthYear from stars as s where s.id = ?;"; //id for both

            String query1 = "select title, movieId from movies_stars where starId = ? order by year desc;";

            String query_str = request.getQueryString();
            String list_params;
            if (sb)
                list_params = query_str.substring(query_str.indexOf("sbit="));
            else if (bb)
                list_params = query_str.substring(query_str.indexOf("bbit="));
            else
                list_params = query_str.substring(query_str.indexOf("order="));

            PreparedStatement ps0 = connection.prepareStatement(query0);
            PreparedStatement ps1 = connection.prepareStatement(query1);
            ps0.setString(1, id);
            ps1.setString(1, id);
            System.out.println(ps0.toString());
            System.out.println(ps1.toString());
            // execute query
            ResultSet rs0 = ps0.executeQuery();
            ResultSet rs1 = ps1.executeQuery();


            out.println("<body>");
            out.println("<h1>MovieDB: Entry for "+ name + "</h1>");
            out.println("<br>");
            if (sb)
                out.println("<a href=\"" + url + "search?" + list_params + "\">Back to Search Results</a>");
            else if (bb)
                out.println("<a href=\"" + url + "browse?" + query_str.substring(query_str.indexOf("bbit=")+7) + "\">Back to Browse Page</a>");
            else
                out.println("<a href=\"" + url + "movie-list?" + list_params + "\">Back to Movie List</a>");
            out.println("<br>");
            out.println("<form ACTION=\"#\" id=\"checkout\" METHOD=\"get\">\n" +
                        "    <input type=\"submit\" value=\"Go to Checkout Page\">\n" +
                        "</form><br><br>");
            out.println("<table border>");

            // add table header row
            out.println("<tr>");
            out.println("<td>name</td>");
            out.println("<td>birth year</td>");
            out.println("</tr>");

            // add a row for every star result
            while (rs0.next()) {
                // get a star from result set
                String starName = rs0.getString("s.name");
                String birthYear = rs0.getString("s.birthyear");
                if (birthYear == null)
                    birthYear = "N/A";

                out.println("<tr>");
                out.println("<td>" + starName + "</td>");
                out.println("<td>" + birthYear + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<br>");

            out.println("<table border>");

            // add table header row
            out.println("<tr>");
            out.println("<td>Movies in which this star appears</td>");
            out.println("<td></td>");
            out.println("</tr>");

            // add a row for every star result
            while (rs1.next()) {
                // get a star from result set
                String movieId = rs1.getString("movieId");
                String title = rs1.getString("title");
                String title_url = title.replace(' ', '+');
                if (title_url.contains("&")) {
                    while (title_url.indexOf("&") != -1) {
                        int in = title_url.indexOf("&");
                        String title_url_front = title_url.substring(0, in);
                        title_url_front += "_" + title_url.substring(in + 1);
                        title_url = title_url_front;
                        //System.out.println(title_url);
                    }
                }
                out.println("<tr>");
                out.println("<td><a href=\"" + url + "single-movie?id=" + movieId + "&title=" + title_url + "&" + list_params + "\">" + title + "</a></td>");
                //out.println("<td><button onclick=\"addToCart('" + title_url + "', " + rand.nextInt(11) + 10 +")\">Add to Cart</button></td>");
//                out.println("<td><form method=\"post\" action=\"#\" class=\"addcart\">\n" +
//                                "   <input type=\"hidden\" name=\"title\" value=\"" + title + "\">\n" +
//                                "   <input type=\"hidden\" name=\"price\" value=\"" + (rand.nextInt(11) + 10) + "\">\n" +
//                                "   <input type=\"submit\" value=\"Add to Cart\">\n" +
//                                "</form><td>");
                out.println("<td><button onclick=\"addToCart2('" + movieId + "', '" + title + "', " + (rand.nextInt(11) + 10) +")\">Add to Cart</button></td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");
//            out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>\n" +
            out.println("<script type='text/javascript' src='cart.js'></script>\n" +
                        "<script type='text/javascript' src='checkout.js'></script>");

            rs0.close();
            rs1.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            e.printStackTrace();

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in getSingleStar: " + e.getMessage());
            out.println("<br>");
            System.out.println(name2);
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();

    }
}
