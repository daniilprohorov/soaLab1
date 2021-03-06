import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(urlPatterns={"/products-start-with"})
public class ProductsStartsWithServlet extends HttpServlet {
    public String sqlRequest(String pattern) {
        return "select * from products where name Like '" + pattern + "%'";
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pattern = req.getParameter("pattern");

        resp.setContentType("text/xml;charset=UTF-8");
        PrintWriter writer = resp.getWriter();

        DbConfig config = new DbConfig();
        Base.open(config.driver, config.url, config.name, config.password);

        List<Product> prds;
        prds = Product.findBySQL(sqlRequest(pattern));

        LazyList<Product> products = (LazyList<Product>) prds;

        writer.println(products.toXml(true, true));

        Base.close();

    }
}
