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
public class ShipmentsInsertServlet extends HttpServlet {
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
		    String pnum = request.getParameter("pnum");
		    String jnum = request.getParameter("jnum");
		    String quantity = request.getParameter("quantity");
	
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
	
	
		            String sql = "insert into shipments values ('"+ snum +"','"+ pnum +"','"+ jnum +"',"+ quantity +");";
	                rowsAffected = statement.executeUpdate(sql);
	                
	                message = "New shipments record: ("+ snum +", "+ pnum +", "+ jnum +", "+ quantity +") - successfully entered into database. ";
	                
	                if (Integer.parseInt(quantity) > 100) {
	                	message += "Business Logic Triggered!!";
	                	
	                    // Update the status in the suppliers table by adding 5 to the current value
	                    String updateSql = "UPDATE suppliers SET status = status + 5 WHERE snum = '" + snum + "'";
	                    int rowsAffectedBusinessLogic = statement.executeUpdate(updateSql);
	                    
	                }
	                else {
	                	message += "Business Logic Not Triggered!!!";
	                }
	                	           
	                
	                request.setAttribute("rowsAffected", rowsAffected);
	                request.setAttribute("message", message);

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
