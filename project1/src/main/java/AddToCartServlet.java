package main.java;

import javafx.util.Pair;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/add-to-cart")
public class AddToCartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("\tin doPost");
        String id = request.getParameter("id");
        String title = request.getParameter("title");
        System.out.println("\tgot title: " + title + "; will get price: " + request.getParameter("price"));
        Integer price = Integer.parseInt(request.getParameter("price"));
        System.out.println("\ntitle: " + title + "\tprice: " + price);
        HttpSession session = request.getSession();
        HashMap<String, Pair<String, Pair<Integer, Integer>>> previousItems = (HashMap<String, Pair<String, Pair<Integer, Integer>>>)session.getAttribute("previousItems");
        System.out.println("\tgot previousItems");
        // get the previous items in a ArrayList
        //ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            System.out.println("\tpreviousItems is null; initializing");
            previousItems = new HashMap<String, Pair<String, Pair<Integer, Integer>>>();
            System.out.println("\tpreviousItems is null; initialized");
            previousItems.put(id, new Pair<String, Pair<Integer, Integer>>(title, new Pair<Integer, Integer>(price, 1)));
            System.out.println("\tpreviousItems is null; put first item");
            //session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            System.out.println("\tpreviousItems is not null; attempting addition to cart");
            synchronized (previousItems) {
                System.out.println("\tinside 'synchronized'");
                Pair<String, Pair<Integer, Integer>> p = previousItems.get(id);
                System.out.println("\tgot existing pair");
                if (p == null)
                    previousItems.put(id, new Pair<String, Pair<Integer, Integer>>(title, new Pair<Integer, Integer>(price, 1)));
                else
                    previousItems.put(id, new Pair<String, Pair<Integer, Integer>>(p.getKey(), new Pair<Integer, Integer>(p.getValue().getKey(), p.getValue().getValue()+1)));
                System.out.println("\tincremented quantity");
            }
        }
        System.out.println("\tsetting previousItems");
        session.setAttribute("previousItems", previousItems);
        System.out.println("\tpreviousItems is set");

        System.out.println("id, title, price, quantity");
        for (String i : previousItems.keySet())
          System.out.println(i + ", " + previousItems.get(i).getKey() + ", " + previousItems.get(i).getValue().getKey() + ", " + previousItems.get(i).getValue().getValue());
    }
}
