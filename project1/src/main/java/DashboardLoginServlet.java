package main.java;

import com.google.gson.JsonObject;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/fabflix/_dashboard")
public class DashboardLoginServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getSession().getAttribute("employee") != null)
            request.getSession().setAttribute("employee", null);
        if (request.getSession().getAttribute("user") != null)
            request.getSession().setAttribute("user", null);
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

        String u_email = request.getParameter("email");
        String u_password = request.getParameter("password");
        String email = "", e_password = "", name = "";
        boolean found = false, success = false;

        JsonObject responseJsonObject = new JsonObject();
        try {
//            Connection connection = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String query = "select email, password, fullname\n" +
                    "from employees\n" +
                    "where email like ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, u_email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                name = rs.getString("fullname");
                email = rs.getString("email");
                e_password = rs.getString("password");
                found = true;
                success = new StrongPasswordEncryptor().checkPassword(u_password, e_password);
            }
            if (found && email.equals(u_email) && success) {
                // Login success:
                Employee emp = new Employee(u_email, name);
                // set this user into the session
                request.getSession().setAttribute("employee", emp);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!found) {
                    responseJsonObject.addProperty("message", "Employee with email " + u_email + " does not exist.");
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
