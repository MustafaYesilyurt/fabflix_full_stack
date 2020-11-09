package main.java;

import com.google.gson.JsonObject;
import javafx.util.Pair;
import main.java.User;
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
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        int total_price = 0; // <id, <title, <price, quantity>>>; to get quantity, previousItems.get(id).getValue().getValue();
        for (String i : previousItems.keySet())
            total_price += previousItems.get(i).getValue().getKey() * previousItems.get(i).getValue().getValue();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("total_price", total_price);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String card = request.getParameter("card"); //card number is varchar like fname and lname, so leave as String
        String exp = request.getParameter("exp"); //will use java.sql.Date. When inserting into sales table, use java.sql.Timestamp
        boolean found = false;
        HttpSession session = request.getSession();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        JsonObject responseJsonObject = new JsonObject();
        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb_write");
            Connection connection = ds.getConnection();

            String query = "select * from creditcards " +
                           "where id = ? and expiration = ? and firstName = ? and lastName = ?;";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, card);
            ps.setString(2, exp);
            ps.setString(3, fname);
            ps.setString(4, lname);
            System.out.println(ps.toString());
            ResultSet rs = ps.executeQuery();
            String id = "", first = "", last = "";
            Integer userId = -1;
            Date expiration = new Date(0);
            if (rs.next()) {
                id = rs.getString("id");
                first = rs.getString("firstName");
                last = rs.getString("lastName");
                expiration = rs.getDate("expiration");
                found = true;
            }

            if (found && fname.equals(first) && lname.equals(last) && card.equals(id) && exp.equals(expiration.toString())) {
                // set this user into the session
                System.out.println("\tgetting userId");
                userId = ((User)(request.getSession().getAttribute("user"))).getId();
                Date sd = new Date(Calendar.getInstance().getTime().getTime());
                String query2 = "insert into sales(customerId, movieId, saleDate)\n" +
                                "values ";
                for (String i : previousItems.keySet())
                    query2 += "\n(?, ?, ?),"; //userId, i, sd
                query2 = query2.substring(0, query2.length()-1) + ";";
                PreparedStatement ps2 = connection.prepareStatement(query2);
                int x = 1;
                for (String i : previousItems.keySet()) {
                    ps2.setInt(x, userId);
                    ps2.setString(x+1, i);
                    ps2.setDate(x+2, sd);
                    x = x+3;
                }
                System.out.println(ps2.toString());
                ps2.executeUpdate();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                responseJsonObject.addProperty("status", "fail");
                if (!found)
                    responseJsonObject.addProperty("message", "Credit card with number " + card + " does not exist.");
                else
                    responseJsonObject.addProperty("message", "Incorrect expiration date.");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
