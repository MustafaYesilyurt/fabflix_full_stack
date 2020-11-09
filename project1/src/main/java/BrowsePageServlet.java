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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

@WebServlet(name = "BrowsePageServlet", urlPatterns = "/browse")
public class BrowsePageServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    private String buildNotLikeClause() {
        String WHERE = "where title not like '0%' and title not like '1%' and title not like '2%' and title not like '3%' and " +
                       "title not like '4%' and title not like '5%' and title not like '6%' and title not like '7%' and " +
                       "title not like '8%' and title not like '9%' and title not like 'A%' and title not like 'B%' and " +
                       "title not like 'C%' and title not like 'D%' and title not like 'E%' and title not like 'F%' and " +
                       "title not like 'G%' and title not like 'H%' and title not like 'I%' and title not like 'J%' and " +
                       "title not like 'K%' and title not like 'L%' and title not like 'M%' and title not like 'N%' and " +
                       "title not like 'O%' and title not like 'P%' and title not like 'Q%' and title not like 'R%' and " +
                       "title not like 'S%' and title not like 'T%' and title not like 'U%' and title not like 'V%' and " +
                       "title not like 'W%' and title not like 'X%' and title not like 'Y%' and title not like 'Z%'";
        return WHERE;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/cs122b-spring20-project1/";
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Random rand = new Random();
        out.println("<html>");
        out.println("<head><title>Fabflix - Browse</title></head>\n" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Fabflix - Browse</title></head>\n" +
//                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                "    integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                "\n" +
//                "        <link rel=\"stylesheet\" href=\"style.css\">");
        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();
            out.println("<body>\n" +
                        "<h1>MovieDB: Browse</h1>\n" +
                        "<br>\n");
            out.println("<button onclick=\"redirectMainPage()\">Back to Main Page</button>\n" +
                        "<script>\n" +
                        "function redirectMainPage() {\n" +
                        "   window.location.replace(\"index.html\");\n" +
                        "}\n" +
                        "</script><br>\n" +
                        "<form ACTION=\"shopping-cart\" id=\"checkout\" METHOD=\"get\">\n" +
                        "    <input type=\"submit\" value=\"Go to Checkout Page\">\n" +
                        "</form><br><br>");
            out.println("<div class=\"row\">\n" +
                        "    <div class=\"column\">\n" +
                        "        <h2>Browse by title</h2><br>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=*&limit=25&offset=0\">*</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=0&limit=25&offset=0\">0</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=1&limit=25&offset=0\">1</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=2&limit=25&offset=0\">2</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=3&limit=25&offset=0\">3</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=4&limit=25&offset=0\">4</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=5&limit=25&offset=0\">5</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=6&limit=25&offset=0\">6</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=7&limit=25&offset=0\">7</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=8&limit=25&offset=0\">8</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=9&limit=25&offset=0\">9</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=A&limit=25&offset=0\">A</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=B&limit=25&offset=0\">B</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=C&limit=25&offset=0\">C</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=D&limit=25&offset=0\">D</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=E&limit=25&offset=0\">E</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=F&limit=25&offset=0\">F</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=G&limit=25&offset=0\">G</a></label><br>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=H&limit=25&offset=0\">H</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=I&limit=25&offset=0\">I</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=J&limit=25&offset=0\">J</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=K&limit=25&offset=0\">K</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=L&limit=25&offset=0\">L</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=M&limit=25&offset=0\">M</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=N&limit=25&offset=0\">N</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=O&limit=25&offset=0\">O</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=P&limit=25&offset=0\">P</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=Q&limit=25&offset=0\">Q</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=R&limit=25&offset=0\">R</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=S&limit=25&offset=0\">S</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=T&limit=25&offset=0\">T</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=U&limit=25&offset=0\">U</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=V&limit=25&offset=0\">V</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=W&limit=25&offset=0\">W</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=X&limit=25&offset=0\">X</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=Y&limit=25&offset=0\">Y</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?btitle=Z&limit=25&offset=0\">Z</a></label>\n" +
                        "    </div>\n" +
                        "    <div class=\"column\">\n" +
                        "        <h2>Browse by genre</h2><br>");

            String genre_query = "select * from genres order by name;";
            PreparedStatement gps = connection.prepareStatement(genre_query);
            ResultSet genre_list = gps.executeQuery();
            int x = 0;
            String res = "";
            while (genre_list.next()) {
                String genre = genre_list.getString("name");
                if (x == 12) {
                    x = 0;
                    res += "        <label><a href=\"" + url + "browse?genre=" + genre + "&limit=25&offset=0\">" + genre + "</a></label><br>\n";
                }
                else
                    res += "        <label><a href=\"" + url + "browse?genre=" + genre + "&limit=25&offset=0\">" + genre + "</a></label>\n";
            }
            res += "</div></div>";
            out.println(res);
            /*out.println("        <label><a href=\"" + url + "browse?genre=Action&limit=25&offset=0\">Action</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Adult&limit=25&offset=0\">Adult</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Adventure&limit=25&offset=0\">Adventure</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Animation&limit=25&offset=0\">Animation</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Biography&limit=25&offset=0\">Biography</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Comedy&limit=25&offset=0\">Comedy</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Crime&limit=25&offset=0\">Crime</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Documentary&limit=25&offset=0\">Documentary</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Drama&limit=25&offset=0\">Drama</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Family&limit=25&offset=0\">Family</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Fantasy&limit=25&offset=0\">Fantasy</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=History&limit=25&offset=0\">History</a></label><br>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Horror&limit=25&offset=0\">Horror</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Music&limit=25&offset=0\">Music</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Musical&limit=25&offset=0\">Musical</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Mystery&limit=25&offset=0\">Mystery</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Reality-TV&limit=25&offset=0\">Reality-TV</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Romance&limit=25&offset=0\">Romance</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Sci-Fi&limit=25&offset=0\">Sci-Fi</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Sport&limit=25&offset=0\">Sport</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Thriller&limit=25&offset=0\">Thriller</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=War&limit=25&offset=0\">War</a></label>\n" +
                        "        <label><a href=\"" + url + "browse?genre=Western&limit=25&offset=0\">Western</a></label>\n" +
                        "    </div>\n" +
                        "</div>");*/
            String title_p = request.getParameter("btitle");
            String genre_p = request.getParameter("genre");
            String limit_p = request.getParameter("limit");
            String offset_p = request.getParameter("offset");
            Integer limit = -1, offset = -1;

            if (limit_p != null)
                limit = Integer.parseInt(limit_p);
            if (offset_p != null)
                offset = Integer.parseInt(offset_p);


            PreparedStatement ps;
            String query;
            if (title_p != null) {
                if (title_p.equals("*"))
                    query = "select * from list_table\n" + buildNotLikeClause() + "\n" +
                            "order by title asc, rating desc\n";
                else
                    query = "select * from list_table\n" +
                            "where title like ?\n" + //title like title_p+"%"
                            "order by title asc, rating desc\n";
                if (limit != -1)
                    query += "limit " + limit;
                if (offset != -1)
                    query += " offset " + (offset*limit);
                query += ";";
                ResultSet rs;
                ps = connection.prepareStatement(query);
                if (!title_p.equals("*"))
                    ps.setString(1, title_p + "%");
                rs = ps.executeQuery();
                System.out.println(query);
                String query_str = request.getQueryString();
                String list_query = "&bbit=1&" + query_str;
                String new_query = "browse?" + query_str.substring(0, query_str.indexOf("offset=")+7);
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
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-movie?title=" + title_url + list_query + "\">" + title + "</a></td>");
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
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre2 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre3_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre3 + "</a></td>");
                    }
                    else if (genres.length == 2) {
                        genre1 = genres[0];
                        genre2 = genres[1];
                        genre1_url = genre1.replace(' ', '+');
                        genre2_url = genre2.replace(' ', '+');
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre2 + "</a></td>");
                    }
                    else {
                        genre1 = genres[0];
                        genre1_url = genre1.replace(' ', '+');
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a></td>");
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
//                    out.println("<td><form method=\"post\" action=\"#\" class=\"addcart\">\n" +
//                                    "   <input type=\"hidden\" name=\"title\" value=\"" + title + "\">\n" +
//                                    "   <input type=\"hidden\" name=\"price\" value=\"" + (rand.nextInt(11) + 10) + "\">\n" +
//                                    "   <input type=\"submit\" value=\"Add to Cart\">\n" +
//                                    "</form><td>");
                    out.println("<td style=\"text-align:center\"><button onclick=\"addToCart2('" + id + "', '" + title + "', " + (rand.nextInt(11) + 10) +")\">Add to Cart</button></td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                if (rowcount < limit && offset != 0)
                    out.println("<a href=\"" + url + new_query + (offset-1) + "\">Previous Page</a><br>\n");
                else if (rowcount < limit && offset == 0)
                    ;
                else if (offset == 0)
                    out.println("<a href=\"" + url + new_query + (offset+1) + "\">Next Page</a><br>\n");
                else
                    out.println("<a href=\"" + url + new_query + (offset-1) + "\">Previous Page</a><p>   </p><a href=\"" + url + new_query + (offset+1) + "\">Next Page</a><br>\n");
            }
            if (genre_p != null) {
                query = "select * from list_table\n" +
                        "where genre like ?\n"; //'%" + genre_p + "%'
                if (limit != -1)
                    query += "limit " + limit;
                if (offset != -1)
                    query += " offset " + (offset*limit);
                query += ";";
                ps = connection.prepareStatement(query);
                ps.setString(1, "%"+genre_p+"%");
                System.out.println(ps.toString());
                ResultSet rs = ps.executeQuery();
                String query_str = request.getQueryString();
                String list_query = "&bbit=1&" + query_str;
                String new_query = "browse?" + query_str.substring(0, query_str.indexOf("offset=")+7);
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
                // add a row for every star result
                while (rs.next()) {
                    rowcount++;
                    // get a star from result set
                    String movieId = rs.getString("id");
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
                    out.println("<td style=\"text-align:center\"><a href=\"" + url + "single-movie?id=" + movieId + "&title=" + title_url + list_query + "\">" + title + "</a></td>");
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
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre2 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre3_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre3 + "</a></td>");
                    }
                    else if (genres.length == 2) {
                        genre1 = genres[0];
                        genre2 = genres[1];
                        genre1_url = genre1.replace(' ', '+');
                        genre2_url = genre2.replace(' ', '+');
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a>, " +
                                        "<a href=\"" + url + "browse?genre=" + genre2_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre2 + "</a></td>");
                    }
                    else {
                        genre1 = genres[0];
                        genre1_url = genre1.replace(' ', '+');
                        out.println("<td style=\"text-align:center\"><a href=\"" + url + "browse?genre=" + genre1_url + "&limit=" + limit + "&offset=" + offset + "\">" + genre1 + "</a></td>");
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
//                    out.println("<td><form method=\"post\" action=\"#\" class=\"addcart\">\n" +
//                                    "   <input type=\"hidden\" name=\"title\" value=\"" + title + "\">\n" +
//                                    "   <input type=\"hidden\" name=\"price\" value=\"" + (rand.nextInt(11) + 10) + "\">\n" +
//                                    "   <input type=\"submit\" value=\"Add to Cart\">\n" +
//                                    "</form><td>");
                    out.println("<td style=\"text-align:center\"><button onclick=\"addToCart2('" + movieId + "', '" + title + "', " + (rand.nextInt(11) + 10) +")\">Add to Cart</button></td>");
                    out.println("</tr>");
                }

                out.println("</table>");

                if (rowcount < limit && offset != 0)
                    out.println("<a href=\"" + url + new_query + (offset-1) + "\">Previous Page</a><br>\n");
                else if (rowcount < limit && offset == 0)
                    ;
                else if (offset == 0)
                    out.println("<a href=\"" + url + new_query + (offset+1) + "\">Next Page</a><br>\n");
                else
                    out.println("<a href=\"" + url + new_query + (offset-1) + "\">Previous Page</a><p>   </p><a href=\"" + url + new_query + (offset+1) + "\">Next Page</a><br>\n");
            }

            //out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>\n" +
            out.println("<script type='text/javascript' src='cart.js'></script>\n" +
                        "<script type='text/javascript' src='checkout.js'></script>");
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