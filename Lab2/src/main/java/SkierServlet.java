import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();

        // check for URL
        if (null == urlPath || urlPath.isEmpty()){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        // url should be valid here, validate it and return response status code.
        String[] urlParts = urlPath.split("/");

        if(!isUrlValid(urlParts)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("It works!");
        }
    }

    private boolean isUrlValid(String[] urlPath){
        // TODO: validate requests
        return true;
    }
}
