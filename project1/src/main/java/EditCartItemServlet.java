package main.java;

import javafx.util.Pair;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "EditCartItemServlet", urlPatterns = "/api/edit-item")
public class EditCartItemServlet  extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        System.out.println("\tgetting title");
        String title = request.getParameter("title");
        System.out.println("\tgot title: " + title + "\ngetting op");
        Integer op = Integer.parseInt(request.getParameter("op"));
        System.out.println("\tgot and parsed op: " + op);
        HttpSession session = request.getSession();
        System.out.println("\tgetting previousItems");
        // <id, <title, <price, quantity>>>; to get quantity, previousItems.get(id).getValue().getValue();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        System.out.println("\tgot previousItems");
        switch (op) {
            case 0:
                System.out.println("\tcase 0: deleting item");
                previousItems.remove(id);
                break;
            case 1:
                System.out.println("\tcase 1: incrementing quantity");
                Pair<String, Pair<Integer, Integer>> p = previousItems.get(id);
                previousItems.put(id, new Pair<String, Pair<Integer, Integer>>(p.getKey(), new Pair<Integer, Integer>(p.getValue().getKey(), p.getValue().getValue() + 1)));
                break;
            case 2:
                System.out.println("\tcase 2: decrementing quantity");
                Pair<String, Pair<Integer, Integer>> p1 = previousItems.get(id);
                if (p1.getValue().getValue() == 1)
                    previousItems.remove(id);
                else
                    previousItems.put(id, new Pair<String, Pair<Integer, Integer>>(p1.getKey(), new Pair<Integer, Integer>(p1.getValue().getKey(), p1.getValue().getValue() - 1)));
                break;
            default:
        }
        System.out.println("\tsetting previousItems");
        session.setAttribute("previousItems", previousItems);
        System.out.println("\tpreviousItems set");
        //response.sendRedirect("/shopping-cart");
    }
}
