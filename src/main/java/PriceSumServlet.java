import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet(urlPatterns={"/price-sum"})
public class PriceSumServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DbConfig config = new DbConfig();
        String pattern = req.getParameter("pattern");

        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter writer = resp.getWriter();

        Base.open(config.driver, config.url, config.name, config.password);

        List<Product> prds;
        prds = Product.findAll();

        LazyList<Product> products = (LazyList<Product>) prds;

        ArrayList<Integer> prices = new ArrayList<>();
        Integer priceSum = 0;
        for (Product prd: prds) {

           priceSum += (Integer) prd.get("price");
        }

        writer.println("<price-sum>" + priceSum +"</price-sum>");

        Base.close();

    }
}
