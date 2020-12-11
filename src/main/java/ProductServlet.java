import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

//@WebServlet("/products")
@WebServlet(urlPatterns={"/products/*"})
public class ProductServlet extends HttpServlet {
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

        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            // return list of product
            // TODO: сделать пагинацию!
            List<Product> prds = Product.findAll();
            PrintWriter writer = response.getWriter();
            writer.println(((LazyList<Product>) prds).toXml(true, true));

        } else {
            // return product with id

            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}


            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Product> prds = Product.where("id = ?", id.get());
                PrintWriter writer = response.getWriter();
                if (!prds.isEmpty()){
                    writer.println(prds.get(0).toXml(true, true));
                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(404);
        } else {
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}
            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Product> prds = Product.where("id = ?", id.get());
                if (!prds.isEmpty()){
                    prds.get(0).delete();
                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Base.open("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/base", "daniil", "1");
        response.setContentType("text/xml;charset=UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(404);
        } else {
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1]; // {id}
            Optional<Integer> id;

            try {
                Integer idI = Integer.valueOf(idStr);
                id = Optional.of(idI);
            }
            catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                id = Optional.empty();
            }
            if (id.isPresent()) {
                List<Product> prds = Product.where("id = ?", id.get());
                if (!prds.isEmpty()){

                    String name = request.getParameter("name");
                    String x = request.getParameter("x");
                    String y = request.getParameter("y");
                    String price = request.getParameter("price");
                    String unitofmeasure = request.getParameter("unitofmeasure");
                    String manufacturer = request.getParameter("manufacturer");

                    prds.get(0).update(name, x, y, price, unitofmeasure, manufacturer);

                } else {
                    response.setStatus(404);
                }
            } else {
                response.setStatus(422);
            }
        }
        Base.close();
    }
}
