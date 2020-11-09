package main.java;

import com.google.gson.JsonObject;
import main.java.User;
import org.jasypt.util.password.StrongPasswordEncryptor;

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
import java.sql.ResultSet;
import java.sql.PreparedStatement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getSession().getAttribute("employee") != null)
            request.getSession().setAttribute("employee", null);
        if (request.getSession().getAttribute("user") != null)
            request.getSession().setAttribute("user", null);
        String android = request.getParameter("android");
        String recaptcha_bypass = null; //request.getParameter("recaptcha_bypass");
        if (android == null && recaptcha_bypass == null) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
            if (gRecaptchaResponse == "") {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Please enter the reCAPTCHA");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
            // Verify reCAPTCHA
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<head><title>Error</title></head>");
                out.println("<body>");
                out.println("<p>recaptcha verification error</p>");
                out.println("<p>" + e.getMessage() + "</p>");
                out.println("</body>");
                out.println("</html>");

                out.close();
                return;
            }

            // done with reCAPTCHA
        }
        String u_email = request.getParameter("email");
        String u_password = request.getParameter("password");
        String email = "", e_password = "";
        Integer id = -1;
        boolean found = false;
        boolean success = false;

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify email/password
        */
        JsonObject responseJsonObject = new JsonObject();
        try {
            //Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String query = "select id, email, password\n" +
                        "from customers\n" +
                        "where email like ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, u_email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
                email = rs.getString("email");
                e_password = rs.getString("password");
                found = true;
                success = new StrongPasswordEncryptor().checkPassword(u_password, e_password);
            }
            if (found && email.equals(u_email) && success) {
                // Login success:
                User u = new User(id, u_email);
                // set this user into the session
                request.getSession().setAttribute("user", u);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!found) {
                    responseJsonObject.addProperty("message", "User with email " + u_email + " does not exist.");
                } else {
                    responseJsonObject.addProperty("message", "Incorrect password.");
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
