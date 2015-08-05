import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seimonq on 23.07.15.
 */
@WebServlet(name = "webgate")
public class webgate extends HttpServlet {

        //controls various sessions
    Map<String,Controller> session_tracker = new HashMap<>();

        //done by servlet container
    public void init() throws ServletException
    {
        // Do required initialization
    }
        //called by service() method from servlet container
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/html");

        String user_info = "unknown";
        String username = request.getParameter("username");
            //if user is known, play next round
        if(session_tracker.containsKey(username)) {
            session_tracker.get(username).user_action(request.getParameter("new_module"));
            session_tracker.get(username).process_modules();
            System.out.println("modul? "+request.getParameter("new_module"));

            session_tracker.get(username).events();
          // user_info = session_tracker.get(username).pull_user_info();
            user_info = session_tracker.get(username).pull_user_info_json();
            System.out.println("userinfo: "+user_info);
            session_tracker.get(username).state.increment_round();

        }
            //if user is unknown, create new game
        else {
            session_tracker.put(username, new Controller(username));
        }


        PrintWriter out = response.getWriter();
        //out.println("<h3> Tag: " + session_tracker.get(username).state.round + "</h3>");

        out.println(user_info);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
        //done by servlet container
    public void destroy() {}
}
