package main.java;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet(name = "CompleteServlet", urlPatterns = "/complete")
public class CompleteServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        Integer userId = ((User)(request.getSession().getAttribute("user"))).getId();
        int total_price = 0; // <id, <title, <price, quantity>>>; to get quantity, previousItems.get(id).getValue().getValue();
        int size = 0;
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        for (String i : previousItems.keySet()) {
            total_price += previousItems.get(i).getValue().getKey() * previousItems.get(i).getValue().getValue();
            size++;
        }
        ArrayList<Integer> saleIdsBackwards = new ArrayList<Integer>();
        ArrayList<Integer> saleIds = new ArrayList<Integer>();
        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();
            String query = "select * from sales where customerId = ? order by id desc limit ?;"; //userId, size
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, size);
            System.out.println(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Integer id = rs.getInt("id");
                saleIdsBackwards.add(id);
            }
            for (int i = saleIdsBackwards.size()-1; i >= 0; i--)
                saleIds.add(saleIdsBackwards.get(i));
            int x = 0;

            out.println("<html>");
            out.println("<head><title>Fabflix - Order completed!</title></head>\n" +
                        "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                        "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                        "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                        "<link rel=\"stylesheet\" href=\"style.css\">");
//            out.println("<head><title>Fabflix - Order completed!</title></head>\n" +
//                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                    "\n" +
//                    "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                    "    integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                    "\n" +
//                    "        <link rel=\"stylesheet\" href=\"style.css\">");
            out.println("<body>");
            out.println("<h1>Order completed!</h1><br>");
            out.println("<button onclick=\"redirectMainPage()\">Back to Main Page</button>\n" +
                    "<script>\n" +
                    "function redirectMainPage() {\n" +
                    "   window.location.replace(\"index.html\");\n" +
                    "}\n" +
                    "</script><br><br>");
            out.println("<p>Total price: $" + total_price + ".00</p><br>");
            out.println("<table border>");
            out.println("<tr>");
            out.println("<td>Sale ID</td>");
            out.println("<td>Movie title</td>");
            out.println("<td>Price</td>");
            out.println("<td>Quantity purchased</td>");
            out.println("</tr>");
            for (String i : previousItems.keySet()) {
                out.println("<tr>");
                out.println("<td style=\"text-align:center\">" + saleIds.get(x) + "</td>");
                out.println("<td style=\"text-align:center\">" + previousItems.get(i).getKey() + "</td>");
                out.println("<td style=\"text-align:center\">$" + previousItems.get(i).getValue().getKey() + ".00</td>");
                out.println("<td style=\"text-align:center\">" + previousItems.get(i).getValue().getValue() + "</td>");
                out.println("</tr>");
                x++;
            }
            rs.close();
            connection.close();
            out.println("</table>");
            out.println("</body>");

        }catch (Exception e) {
            e.printStackTrace();
            out.println("<body>");
            out.println("<p>");
            out.println("Exception in complete: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");

        }
        session.setAttribute("previousItems", null);
//        out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>");
        out.println("</html>");
        out.close();
    }
}
