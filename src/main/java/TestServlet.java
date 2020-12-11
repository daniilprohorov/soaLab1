import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.javalite.activejdbc.Base;

@WebServlet("/product")
public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        String name = request.getParameter("name");
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String price = request.getParameter("price");
        String unitofmeasure = request.getParameter("unitofmeasure");
        String manufacturer = request.getParameter("manufacturer");

        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        Product prd1 = new Product();
        Boolean initialized = prd1.init(name, x, y, price, unitofmeasure, manufacturer);
        if (initialized) {
            writer.println(prd1.toXml(true, true));
        } else {
            response.setStatus(422);
        }
        Base.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/xml;charset=UTF-8");


        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");

        Product prd1 = new Product();
        prd1.setString("name", "prd1");
        prd1.set("x", 1);
        prd1.set("y", 0.5);
        prd1.setDate("creationdate", java.time.LocalDate.now().toString());
        prd1.setInteger("price", 100);
        prd1.setString("unitofmeasure", "kilograms");
        prd1.set("manufacturer", Organization.findFirst("name = ?", "org1").getId());
        prd1.saveIt();

        PrintWriter writer = response.getWriter();
        writer.println(prd1.toXml(true, true));

        Base.close();



    }
}
