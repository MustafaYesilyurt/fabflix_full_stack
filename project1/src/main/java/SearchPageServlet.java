package main.java;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Random;

@WebServlet(name = "SearchPageServlet", urlPatterns = "/search")
public class SearchPageServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    private String buildWhereClause(boolean titleb, boolean yearb, boolean directorb, boolean starb, String orderby, String orderby2, String dir, String dir2) {
        String WHERE = "WHERE 1=1";
        if(titleb)
            WHERE += " and (match(title) against(? in boolean mode) or title like ?)"; //WHERE += " and match(title) against(? in boolean mode)"; //WHERE += " and title like ?"; // "%" + title_p + "%"
        if(yearb)
            WHERE += " and year = ?"; // year_p
        if(directorb)
            WHERE += " and director like ?"; // "%" + director_p + "%"
        if(starb)
            WHERE += " and star like ?"; // "%" + star_p + "%"
        WHERE += "\norder by " + orderby + " " + dir + ", " + orderby2 + " " + dir2 +
                "\nlimit ? offset ?;";
        return WHERE;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long elapsedTimeTJ;
        long startTimeTS = System.nanoTime();
        String write_title = "";
        System.out.println("getLocalPort(): " + request.getLocalPort());
        System.out.println("getRemotePort(): " + request.getRemotePort());
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/cs122b-spring20-project1/";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Random rand = new Random();
        out.println("<html>");
        out.println("<head><title>Fabflix - Search results</title>\n" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Fabflix - Search results</title>\n" +
//                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                "    \n" +
//                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                "          integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                "    \n" +
//                "    <link rel=\"stylesheet\" href=\"style.css\"></head>");
        try {
//            Connection connection = dataSource.getConnection();
            Connection connection;
            if (request.getParameter("no_pooling") == null) {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
                connection = ds.getConnection();
            }
            else {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                String loginUser = "mytestuser";
                String loginPasswd = "mypassword";
                String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
                connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            }

            String title_p = request.getParameter("title");
            String year_p = request.getParameter("year");
            String director_p = request.getParameter("director");
            String star_p = request.getParameter("star");
            String orderby_p = request.getParameter("order");
            String dir_p = request.getParameter("dir");
            String orderby, orderby2, dir, dir2;
            String dir2_p = request.getParameter("dir2");
            Integer limit;
            if (request.getParameter("limit") == null)
                limit = 10;
            else
                limit = Integer.parseInt(request.getParameter("limit"));
            Integer offset_p;
            if (request.getParameter("offset") == null)
                offset_p = 0;
            else
                offset_p = Integer.parseInt(request.getParameter("offset"));
            Integer offset = offset_p * limit;
            boolean titleb = true, yearb = true, directorb = true, starb = true;

            if (title_p.equals(""))
                titleb = false;
            if (year_p == null || year_p.equals(""))
                yearb = false;
            if (director_p == null || director_p.equals(""))
                directorb = false;
            if (star_p == null || star_p.equals(""))
                starb = false;

            if (orderby_p == null || (!orderby_p.equals("title") && !orderby_p.equals("rating"))) {
                System.out.println("order by parameter null");
                orderby = "rating";
                orderby2 = "title";
            }else {
                orderby = orderby_p;
                if (orderby.equals("title"))
                    orderby2 = "rating";
                else
                    orderby2 = "title";
            }
            if (dir_p == null || (!dir_p.equals("desc") && !dir_p.equals("asc"))){
                System.out.println("direction parameter null");
                dir = "asc";
            }else
                dir = dir_p;
            if (dir2_p == null || (!dir2_p.equals("desc") && !dir2_p.equals("asc"))){
                System.out.println("direction2 parameter null");
                dir2 = "asc";
            }else
                dir2 = dir2_p;

            long startTimeTJ = System.nanoTime();
            String query = "select * from list_table\n" + buildWhereClause(titleb, yearb, directorb, starb, orderby, orderby2, dir, dir2);
            PreparedStatement ps = connection.prepareStatement(query);
            int x = 1;
            if(titleb) {
                String [] title_words = title_p.split(" ");
                String new_title = "";
                for (String w : title_words) {
                    if (!w.equals(" ") && !w.equals("")) {
                        write_title += w + " ";
                        new_title += w + "* ";
                    }
                }
                //ps.setString(x, title_p + "*"); // for fulltext search
                ps.setString(x, new_title.substring(0, new_title.length()-1));
                write_title = write_title.substring(0, write_title.length()-1);
                x++;
                ps.setString(x, "%" + title_p + "%"); // for like keyword
                x++;
            }
            if(yearb) {
                ps.setString(x, year_p);
                x++;
            }
            if(directorb) {
                ps.setString(x, "%" + director_p + "%");
                x++;
            }
            if(starb) {
                ps.setString(x, "%" + star_p + "%");
                x++;
            }
            ps.setInt(x, limit);
            ps.setInt(x+1, offset);

            System.out.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            long endTimeTJ = System.nanoTime();
            elapsedTimeTJ = endTimeTJ - startTimeTJ;
            out.println("<body>");
            out.println("<h1>MovieDB: Search</h1>");
            out.println("<button onclick=\"redirectMainPage()\">Back to Main Page</button>\n" +
                    "<script>\n" +
                    "function redirectMainPage() {\n" +
                    "   window.location.replace(\"index.html\");\n" +
                    "}\n" +
                    "</script><br>\n" +
                    "<form ACTION=\"shopping-cart\" id=\"checkout\" METHOD=\"get\">\n" +
                    "    <input type=\"submit\" value=\"Go to Checkout Page\">\n" +
                    "</form><br><br>");
            out.println("<label>Enter your search parameters<br></label>\n" +
                    "<form id=\"search_form\" method=\"get\" action=\"search\">\n" +
                    "    <label>Title</label>\n" +
                    "    <label>\n" +
                    "        <input id=\"title\" name=\"title\" placeholder=\"Enter movie title\" type=\"text\">\n" +
                    "    </label>\n" +
                    "    <br>\n" +
                    "    <label>Year</label>\n" +
                    "    <label>\n" +
                    "        <input id=\"year\" name=\"year\" placeholder=\"Enter year\" type=\"number\">\n" +
                    "    </label>\n" +
                    "    <br>\n" +
                    "    <label>Director</label>\n" +
                    "    <label>\n" +
                    "        <input id=\"director\" name=\"director\" placeholder=\"Enter director's name\" type=\"text\">\n" +
                    "    </label>\n" +
                    "    <br>\n" +
                    "    <label>Star</label>\n" +
                    "    <label>\n" +
                    "        <input id=\"star\" name=\"star\" placeholder=\"Enter a star's name\" type=\"text\">\n" +
                    "    </label>\n" +
                    "    <br>\n" +
                    "    <select id=\"order\" name=\"order\">\n" +
                    "        <option value=\"rating\">Sort by Rating first, then Title</option>\n" +
                    "        <option value=\"title\">Sort by Title first, then Rating</option>\n" +
                    "    </select>\n" +
                    "    <select id=\"dir\" name=\"dir\">\n" +
                    "        <option value=\"asc\">1st parameter - ascending order</option>\n" +
                    "        <option value=\"desc\">1st parameter - descending order</option>\n" +
                    "    </select>\n" +
                    "    <select id=\"dir2\" name=\"dir2\">\n" +
                    "        <option value=\"asc\">2nd parameter - ascending order</option>\n" +
                    "        <option value=\"desc\">2nd parameter - descending order</option>\n" +
                    "    </select>\n" +
                    "    <select id=\"limit\" name=\"limit\">\n" +
                    "        <option value=\"10\">10 items per page</option>\n" +
                    "        <option value=\"25\">25 items per page</option>\n" +
                    "        <option value=\"50\">50 items per page</option>\n" +
                    "        <option value=\"100\">100 items per page</option>\n" +
                    "    </select>\n" +
                    "    <select id=\"offset\" name=\"offset\">\n" +
                    "        <option value=\"0\">1st page</option>\n" +
                    "        <option value=\"1\">2nd page</option>\n" +
                    "        <option value=\"2\">3rd page</option>\n" +
                    "        <option value=\"3\">4th page</option>\n" +
                    "    </select>\n" +
                    "    <br>\n" +
                    "    <input type=\"submit\" value=\"Search\">\n" +
                    "</form>");

            String query_str = request.getQueryString();
            String list_query = "&" + query_str;
            String new_query = "search?" + query_str.substring(0, query_str.indexOf("offset=")+7);

            out.println("<table border>");
            out.println("<tr>");
            out.println("<td style=\"text-align:center\">Movie Title</td>");
            out.println("<td style=\"text-align:center\">Year released</td>");
            out.println("<td style=\"text-align:center\">Director</td>");
            out.println("<td style=\"text-align:center\">Rating</td>");
            out.println("<td style=\"text-align:center\">Genre(s)</td>");
            out.println("<td style=\"text-align:center\">Stars</td>");
            out.println("<td></td>");
            out.println("</tr>");
            int rowcount = 0;
            while (rs.next()) {
                rowcount++;
                // get a star from result set
                String id = rs.getString("id");
                String title = rs.getString("title");
                Integer year = rs.getInt("year");
                String director = rs.getString("director");
                Float rating = rs.getFloat("rating");
                String genre = rs.getString("genre");
                String star = rs.getString("star");
                String starId = rs.getString("starId");

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
                String[] genres = genre.split(", ");
                String genre1, genre2, genre3, genre1_url, genre2_url, genre3_url;

                String[] stars = star.split(", ");
                String star1, star2, star3, star1_url, star2_url, star3_url;

                String[] starIds = starId.split(", ");
                String starId1, starId2, starId3;

                out.println("<tr>");
                out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-movie?id=" + id + "&mtitle=" + title_url + list_query + "\">" + title + "</a></td>");
                out.println("<td style=\"text-align:center\">" + year + "</td>");
                out.println("<td style=\"text-align:center\">" + director + "</td>");
                out.println("<td style=\"text-align:center\">" + rating + "</td>");

                // handle different numbers of genres
                if (genres.length == 3) {
                    genre1 = genres[0];
                    genre2 = genres[1];
                    genre3 = genres[2];
                    genre1_url = genre1.replace(' ', '+');
                    genre2_url = genre2.replace(' ', '+');
                    genre3_url = genre3.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "\">" + genre1 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre2_url + "\">" + genre2 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre3_url + "\">" + genre3 + "</a></td>");
                }
                else if (genres.length == 2) {
                    genre1 = genres[0];
                    genre2 = genres[1];
                    genre1_url = genre1.replace(' ', '+');
                    genre2_url = genre2.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "\">" + genre1 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre2_url + "\">" + genre2 + "</a></td>");
                }
                else {
                    genre1 = genres[0];
                    genre1_url = genre1.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "\">" + genre1 + "</a></td>");
                }

                // ---------------------------------------------------------------------------------------------------------------------

                //handle different numbers of stars
                if (stars.length == 3) {
                    star1 = stars[0];
                    star2 = stars[1];
                    star3 = stars[2];
                    starId1 = starIds[0];
                    starId2 = starIds[1];
                    starId3 = starIds[2];
                    star1_url = star1.replace(' ', '+');
                    star2_url = star2.replace(' ', '+');
                    star3_url = star3.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-star?id=" + starId1 + "&name=" + star1_url + list_query + "\">" + star1 + "</a>, " +
                                    "<a href=\"" + url + "single-star?id=" + starId2 + "&name=" + star2_url + list_query + "\">" + star2 + "</a>, " +
                                    "<a href=\"" + url + "single-star?id=" + starId3 + "&name=" + star3_url + list_query + "\">" + star3 + "</a></td>");
                }
                else if (stars.length == 2) {
                    star1 = stars[0];
                    star2 = stars[1];
                    starId1 = starIds[0];
                    starId2 = starIds[1];
                    star1_url = star1.replace(' ', '+');
                    star2_url = star2.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-star?id=" + starId1 + "&name=" + star1_url + list_query + "\">" + star1 + "</a>, " +
                                    "<a href=\"" + url + "single-star?id=" + starId2 + "&name=" + star2_url + list_query + "\">" + star2 + "</a></td>");
                }
                else {
                    star1 = stars[0];
                    starId1 = starIds[0];
                    star1_url = star1.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-star?id=" + starId1 + "&name=" + star1_url + list_query + "\">" + star1 + "</a></td>");
                }

                // add shopping cart button
                //out.println("<td><button onclick=\"addToCart('" + title_url + "', " + rand.nextInt(11) + 10 +")\">Add to Cart</button></td>");
//                out.println("<td><form method=\"post\" action=\"#\" class=\"addcart\">\n" +
//                        "   <input type=\"hidden\" name=\"title\" value=\"" + title + "\">\n" +
//                        "   <input type=\"hidden\" name=\"price\" value=\"" + (rand.nextInt(11) + 10) + "\">\n" +
//                        "   <input type=\"submit\" value=\"Add to Cart\">\n" +
//                        "</form><td>");
                out.println("<td style=\"text-align:center\"><button onclick=\"addToCart2('" + id + "', '" + title + "', " + (rand.nextInt(11) + 10) +")\">Add to Cart</button></td>");

                out.println("</tr>");
            }

            out.println("</table>");

            if (rowcount < limit && offset != 0)
                out.println("<a href=\"" + url + new_query + (offset_p-1) + "\">Previous Page</a><br>\n");
            else if (rowcount < limit && offset == 0)
                ;
            else if (offset == 0)
                out.println("<a href=\"" + url + new_query + (offset_p+1) + "\">Next Page</a><br>\n");
            else
                out.println("<a href=\"" + url + new_query + (offset_p-1) + "\">Previous Page</a><p>   </p><a href=\"" + url + new_query + (offset_p+1) + "\">Next Page</a><br>\n");

//            out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
            out.println("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery.devbridge-autocomplete/1.4.7/jquery.autocomplete.min.js\"></script>\n" +
                        "<script type='text/javascript' src='search.js'></script>" +
                        "<script type='text/javascript' src='cart.js'></script>\n" +
                        "<script type='text/javascript' src='checkout.js'></script>");

//            out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>\n" +
//                        "<script type='text/javascript' src='search.js'></script>" +
//                        "<script type='text/javascript' src='cart.js'></script>\n" +
//                        "<script type='text/javascript' src='checkout.js'></script>");
            out.println("</body>");

            rs.close();
            long endTimeTS = System.nanoTime();
            long elapsedTimeTS = endTimeTS - startTimeTS;

//            System.out.println("getContextPath(): " + getServletContext().getContextPath());
//            System.out.println("getRealPath(\"/\"): " + getServletContext().getRealPath("/"));
//            System.out.println("getRealPath(\"/WEB-INF\"): " + getServletContext().getRealPath("/WEB-INF"));

            // Now write the data to file (title, TJ time, TS time
            BufferedWriter outfile = new BufferedWriter(
                    new FileWriter(getServletContext().getRealPath("/") + "time_data.txt", true));
            outfile.write(write_title + "," + elapsedTimeTJ + "," + elapsedTimeTS + "\n");
            outfile.close();

            connection.close();
        }catch (Exception e) {
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
            out.println("Exception in search: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }
    }
}