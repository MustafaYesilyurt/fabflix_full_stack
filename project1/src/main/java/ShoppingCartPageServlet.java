package main.java;

import javafx.util.Pair;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet(name = "ShoppingCartPageServlet", urlPatterns = "/shopping-cart")
public class ShoppingCartPageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Fabflix - Shopping Cart</title></head>" +
                    "<script src=\"https://code.jquery.com/jquery-3.5.1.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">\n" +
                    "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
                    "<link rel=\"stylesheet\" href=\"style.css\">");
//        out.println("<head><title>Fabflix - Shopping Cart</title></head>" +
//                "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\"></script>\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
//                "          integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
//                "\n" +
//                "    <link rel=\"stylesheet\" href=\"style.css\">");
        out.println("<body>");
        out.println("<button onclick=\"redirectMainPage()\">Back to Main Page</button>\n" +
                    "<script>\n" +
                    "function redirectMainPage() {\n" +
                    "   window.location.replace(\"index.html\");\n" +
                    "}\n" +
                    "</script><br>");
        if (previousItems == null) {
            out.println("<p>");
            out.println("No items in cart.");
            out.println("</p>");
        }
        else {
            out.println("<p id=\"total_price\"></p><br>");
            out.println("<table border>");
            out.println("<tr>");
            out.println("<td style=\"text-align:center\">Movie Title</td>");
            out.println("<td style=\"text-align:center\">Price</td>");
            out.println("<td style=\"text-align:center\">Quantity</td>");
            out.println("<td></td>");
            out.println("<td></td>");
            out.println("<td></td>");
            out.println("</tr>");
            for (String i : previousItems.keySet()) {
                out.println("<tr>");
                out.println("<td style=\"text-align:center\">" + previousItems.get(i).getKey() + "</td>");
                out.println("<td style=\"text-align:center\">$" + previousItems.get(i).getValue().getKey() + ".00</td>");
                out.println("<td style=\"text-align:center\">" + previousItems.get(i).getValue().getValue() + "</td>");
                out.println("<td style=\"text-align:center\"><button onclick=\"incrementQuantity('" + i + "', '" + previousItems.get(i).getKey() + "')\">Increment quantity</button></td>"); //increment quantity
                out.println("<td style=\"text-align:center\"><button onclick=\"decrementQuantity('" + i + "', '" + previousItems.get(i).getKey() + "')\">Decrement quantity</button></td>"); //decrement quantity
                out.println("<td style=\"text-align:center\"><button onclick=\"deleteItem('" + i + "', '" + previousItems.get(i).getKey() + "')\">Delete quantity</button></td>"); //delete item
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<button onclick=\"redirectPaymentPage()\">Proceed to Payment Page</button>\n" +
                    "<script>\n" +
                    "function redirectPaymentPage() {\n" +
                    "   window.location.replace(\"payment.html\");\n" +
                    "}\n" +
                    "</script><br>");
        }
        out.print("</body>");
        out.println("<script src='https://code.jquery.com/jquery-3.1.0.min.js'></script>\n" +
                    "<script type='text/javascript' src='cart_ops.js'></script>");
        out.println("</html>");
    }
}
