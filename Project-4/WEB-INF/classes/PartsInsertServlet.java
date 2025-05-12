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
public class PartsInsertServlet extends HttpServlet {
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

		    String pnum = request.getParameter("pnum");
		    String pname = request.getParameter("pname");
		    String color = request.getParameter("color");
		    String weight = request.getParameter("weight");
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
	
	
		            String sql = "insert into parts values ('"+ pnum +"','"+ pname +"','"+ color +"',"+ weight +",'"+ city +"')";
	                rowsAffected = statement.executeUpdate(sql);
	                
	                message = "New parts record: ("+ pnum +", "+ pname +", "+ color +", "+ weight +", "+ city +") - successfully entered into database. ";
	                
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
