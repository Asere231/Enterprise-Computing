import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;


@SuppressWarnings("serial")
public class ClientUserServlet extends HttpServlet {
	private MysqlDataSource dataSource;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    int rowsAffected = 0; // Initialize rowsAffected variable
	    String errorMessage = null; // Initialize errorMessage variable
	    
	    if (request.getParameter("clearResult") != null) {
	        // Clear the attributes holding the result
	        request.removeAttribute("rows");
	        request.removeAttribute("columnNames");
	    }
	    else {
	    
		    String sql = request.getParameter("mysqlCommand").trim();
		    
		    System.out.println("sql trimmed: " + sql);
	
		    Properties userProperties = new Properties();
		    InputStream filein = getClass().getClassLoader().getResourceAsStream("../lib/client.properties");
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
	
		            String[] commandArray = sql.split("\\s+");
		            String keyword = commandArray[0];
	
		            if (keyword.equals("select")) {
		                resultSet = statement.executeQuery(sql);
		                
		             // Process result set
	                    List<String> columnNames = new ArrayList<>();
	                    ResultSetMetaData rsmd = resultSet.getMetaData();
	                    int columnCount = rsmd.getColumnCount();
	                    for (int i = 1; i <= columnCount; i++) {
	                        columnNames.add(rsmd.getColumnName(i));
	                    }
	                    List<Map<String, String>> rows = new ArrayList<>();
	                    while (resultSet.next()) {
	                        Map<String, String> row = new HashMap<>();
	                        for (int i = 1; i <= columnCount; i++) {
	                            row.put(rsmd.getColumnName(i), resultSet.getString(i));
	                        }
	                        rows.add(row);
	                    }
	                    request.setAttribute("rows", rows);
	                    request.setAttribute("columnNames", columnNames);
	                } else {
	                	
	                    rowsAffected = statement.executeUpdate(sql);
	                    request.setAttribute("rowsAffected", rowsAffected);
	                
		            }
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
	    RequestDispatcher dispatcher = request.getRequestDispatcher("/client.jsp");
	    dispatcher.forward(request, response);
	}
}
