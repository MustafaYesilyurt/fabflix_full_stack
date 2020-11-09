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

@WebServlet(name = "MovieListServlet", urlPatterns = "/movie-list")
public class MovieListServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("getLocalPort(): " + request.getLocalPort());
        System.out.println("getRemotePort(): " + request.getRemotePort());
        //String url = "http://" + request.getServerName() + ":8080/cs122b-spring20-project1.war/";
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/cs122b-spring20-project1/";

        // set response mime type
        response.setContentType("text/html");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix - Movie List</title></head>" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Fabflix - Movie List</title></head>" +
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


            // declare statement
//            Statement statement0 = connection.createStatement();
//            Statement statement1 = connection.createStatement();
//            Statement statement2 = connection.createStatement();
//            Statement statement3 = connection.createStatement();

            String orderby_p = request.getParameter("order");
            String dir_p = request.getParameter("dir");
            String dir2_p = request.getParameter("dir2");
            String limit_ps = request.getParameter("limit");
            String offset_ps = request.getParameter("offset");
            Integer limit_p, offset_p;
            if (limit_ps == null) {
                System.out.println("limit parameter null");
                limit_p = 10;
            }else
                limit_p = Integer.parseInt(limit_ps);
            if (offset_ps == null) {
                System.out.println("offset parameter null");
                offset_p = 0;
            }else
                offset_p = Integer.parseInt(offset_ps);

            String orderby, orderby_s, dir, dir2;
            Integer limit, offset;

            if (orderby_p == null || (!orderby_p.equals("title") && !orderby_p.equals("rating"))) {
                System.out.println("order by parameter null");
                orderby = "rating";
                orderby_s = "title";
            }else {
                orderby = orderby_p;
                if (orderby.equals("title"))
                    orderby_s = "rating";
                else
                    orderby_s = "title";
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
            if (limit_p == null || (limit_p != 10 && limit_p != 25 && limit_p != 50 && limit_p != 100))
                limit = 10;
            else
                limit = limit_p;
            if (offset_p == null)
                offset = 0;
            else
                offset = offset_p * limit;

            String list_query = "&" + request.getQueryString();
            //System.out.println("list_query: " + list_query);
            // prepare query

//            String query0 = "create table if not exists tmp_core_list as\n" +
//                    "(\n" +
//                    "select t1.id as id, t1.title as title, year, director, rating, rowid_rating, rowid_title\n" +
//                    "from (\n" +
//                    "select m.id, m.title, m.year, m.director, r.rating, row_number() over () as rowid_rating\n" +
//                    "from movies as m\n" +
//                    "inner join ratings as r\n" +
//                    "on r.movieId = m.id\n" +
//                    "order by r.rating desc) as t1\n" +
//                    "inner join (\n" +
//                    "    select m2.id, m2.title, row_number() over () as rowid_title\n" +
//                    "    from movies as m2\n" +
//                    "    order by m2.title\n" +
//                    "    ) as t2\n" +
//                    "on t1.id = t2.id\n" +
//                    "    );";
//
//            String query1 = "create table if not exists tmp_sm_count (\n" +
//                    "select sm.*, s.name, s.count\n" +
//                    "from stars_in_movies as sm\n" +
//                    "inner join\n" +
//                    "     (\n" +
//                    "         select sm.starId as 'id', s.name as 'name', count(sm.movieId) as 'count'\n" +
//                    "         from stars_in_movies as sm\n" +
//                    "         inner join stars as s\n" +
//                    "         on sm.starId = s.id\n" +
//                    "         group by sm.starId, s.name\n" +
//                    "        order by count(sm.movieId) desc, s.name\n" +
//                    "     ) as s\n" +
//                    "on sm.starId = s.id\n" +
//                    "group by sm.starId, sm.movieId);";
//
//            String query2 = "create table if not exists tmp_rid_table (\n" +
//                    "select movieId, starId, name, count, row_number() over (partition by movieId order by count desc, movieId, name) as rowid\n" +
//                    "from tmp_sm_count\n" +
//                    "order by count desc);";

            // order by rowid_rating asc starts from HIGHEST rated movies and goes down.
            // order by rowid_title asc starts from earliest punctuation, numbers, and letters and goes forward to the end of the alphabet
//            String query3 = "select t.title as 'title', t.year as 'year', t.director as 'director', t.rating as 'rating', group_concat(distinct g.name order by g.name separator ', ') as 'genre',\n" +
//                    "group_concat(distinct trt.name order by trt.count desc, trt.name separator ', ') as 'star', t.rowid_rating, t.rowid_title\n" +
//                    "from tmp_core_list as t\n" +
//                    "inner join genres_in_movies as gm\n" +
//                    "on movieId = t.id\n" +
//                    "inner join genres g\n" +
//                    "on gm.genreId = g.id\n" +
//                    "inner join tmp_rid_table as trt\n" +
//                    "on t.id = trt.movieId\n" +
//                    "where trt.rowid <= 3\n" +
//                    "group by t.rowid_rating, t.rowid_title\n" +
//                    "order by t.rowid_" + orderby + " " + dir + "\n" +
//                    "limit " + limit + " offset " + offset + ";";

            String query = "select * from list_table order by " + orderby + " " + dir + ", " + orderby_s + " " + dir2 + " limit ? offset ?;";
            // execute query
//            statement0.executeUpdate(query0);
//            statement1.executeUpdate(query1);
//            statement2.executeUpdate(query2);
//            ResultSet rs = statement3.executeQuery(query3);
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            System.out.println(ps.toString());
            ResultSet rs = ps.executeQuery();

            out.println("<body>");
            out.println("<h1>MovieDB: Movie List</h1>");
            out.println("<button onclick=\"redirectMainPage()\">Back to Main Page</button>\n" +
                        "<script>\n" +
                        "function redirectMainPage() {\n" +
                        "   window.location.replace(\"index.html\");\n" +
                        "}\n" +
                        "</script><br>\n" +
                        "<form ACTION=\"shopping-cart\" id=\"checkout\" METHOD=\"get\">\n" +
                        "    <input type=\"submit\" value=\"Go to Checkout Page\">\n" +
                        "</form><br><br>");
            out.println("<form ACTION=\"movie-list\" id=\"mlist\" METHOD=\"get\">\n" +
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
                    "    <input type=\"submit\" value=\"Go to Movie List\">\n" +
                    "</form><br>");

            String query_str = request.getQueryString();
            String new_query = "movie-list?" + query_str.substring(0, query_str.indexOf("offset=")+7);

            out.println("<table border>");
            out.println("<tr>");
            out.println("<td style=\"text-align:center\">Movie Title</td>");
            out.println("<td style=\"text-align:center\">Year released</td>");
            out.println("<td style=\"text-align:center\">Director</td>");
            out.println("<td style=\"text-align:center\">Rating</td>");
            out.println("<td style=\"text-align:center\">Genre(s)</td>");
            out.println("<td style=\"text-align:center\">Stars</td>");
            out.println("<td style=\"text-align:center\"></td>");
            out.println("</tr>");
            int rowcount = 0;
            // add a row for every star result
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
                out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-movie?id=" + id + "&title=" + title_url + list_query + "\">" + title + "</a></td>");
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
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=25&offset=0\">" + genre1 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=25&offset=0\">" + genre2 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre3_url + "&limit=25&offset=0\">" + genre3 + "</a></td>");
                }
                else if (genres.length == 2) {
                    genre1 = genres[0];
                    genre2 = genres[1];
                    genre1_url = genre1.replace(' ', '+');
                    genre2_url = genre2.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=25&offset=0\">" + genre1 + "</a>, " +
                                    "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=25&offset=0\">" + genre2 + "</a></td>");
                }
                else {
                    genre1 = genres[0];
                    genre1_url = genre1.replace(' ', '+');
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=25&offset=0\">" + genre1 + "</a></td>");
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
//                            "   <input type=\"hidden\" name=\"title\" value=\"" + title + "\">\n" +
//                            "   <input type=\"hidden\" name=\"price\" value=\"" + (rand.nextInt(11) + 10) + "\">\n" +
//                            "   <input type=\"submit\" value=\"Add to Cart\">\n" +
//                            "</form><td>");
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

            out.println("</body>");

            rs.close();
//            statement0.close();
//            statement1.close();
//            statement2.close();
//            statement3.close();
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
            out.println("Exception in getMovieList: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }
//        out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>\n" +
        out.println("<script type='text/javascript' src='movies.js'></script>\n" +
                    "<script type='text/javascript' src='cart.js'></script>\n" +
                    "<script type='text/javascript' src='checkout.js'></script>");
        out.println("</html>");
        out.close();

    }
}
