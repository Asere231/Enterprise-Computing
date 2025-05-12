import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;


@SuppressWarnings("serial")
public class SuppliersInsertServlet extends HttpServlet {
	private MysqlDataSource dataSource;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    int rowsAffected = 0; // Initialize rowsAffected variable
	    String errorMessage = null; // Initialize errorMessage variable
	    String message = null; // Initialize message variable
	    
	    if (request.getParameter("clearResult") != null) {
	        // Clear the attributes holding the result
	        request.removeAttribute("rows");
	        request.removeAttribute("columnNames");
	    }
	    else {

		    String snum = request.getParameter("snum");
		    String sname = request.getParameter("sname");
		    String status = request.getParameter("status");
		    String city = request.getParameter("city");
	
		    Properties userProperties = new Properties();
		    InputStream filein = getClass().getClassLoader().getResourceAsStream("../lib/dataentryuser.properties");
		    userProperties.load(filein);
	
		    Properties databaseProperties = new Properties();
		    InputStream databaseFile = getClass().getClassLoader().getResourceAsStream("../lib/project4DB.properties");
		    databaseProperties.load(databaseFile);
	
		    try {
		        dataSource = new MysqlDataSource();
	
		        dataSource.setURL(databaseProperties.getProperty("MYSQL_DB_URL"));
		        dataSource.setUser(userProperties.getProperty("MYSQL_DB_USERNAME"));
		        dataSource.setPassword(userProperties.getProperty("MYSQL_DB_PASSWORD"));
	
		        connection = dataSource.getConnection();
	
		        if (connection != null) {
		            // Connection successful
		            System.out.println("Connection successful!");
	
		            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	
	
		            String sql = "insert into suppliers values ('"+ snum +"','"+ sname +"','"+ status +"','"+ city +"')";
	                rowsAffected = statement.executeUpdate(sql);
	                
	                message = "New suppliers record: ("+ snum +", "+ sname +", "+ status +", "+ city +") - successfully entered into database. ";
	                
	                request.setAttribute("message", message);
	                request.setAttribute("rowsAffected", rowsAffected);
		           
		        } else {
		            System.out.println("Connection failed!");
		        }
	
		    } catch (SQLException sqlException) {
		    	errorMessage = sqlException.getMessage(); // Get error message
	            sqlException.printStackTrace();
	            request.setAttribute("errorMessage", errorMessage);
		    } finally {
		        // Close all connections to database
		        try {
		            if (resultSet != null) {
		                resultSet.close();
		            }
		            if (statement != null) {
		                statement.close();
		            }
		            if (connection != null) {
		                connection.close();
		            }
		        } catch (SQLException sqlException) {
		            sqlException.printStackTrace();
		        }
		    }
	    }
	    

	    // Forward the request to the JSP
	    RequestDispatcher dispatcher = request.getRequestDispatcher("/data-entry.jsp");
	    dispatcher.forward(request, response);
	}
}
