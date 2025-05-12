import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.CallableStatement;
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
public class AccountantUserServlet extends HttpServlet {
	private MysqlDataSource dataSource;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String errorMessage = null; // Initialize errorMessage variable
	    
	    if (request.getParameter("clearResult") != null) {
	        // Clear the attributes holding the result
	        request.removeAttribute("rows");
	        request.removeAttribute("columnNames");
	    }
	    else {
	    	
	    	String selectedOption = request.getParameter("option");
	 	    String procedure = "";
	 	    
	 	    if (selectedOption.equals("Get The Maximum Status Value Of All Suppliers")) {
	 	    	procedure = "Get_The_Maximum_Status_Of_All_Suppliers()";
	 	    }
	 	    else if (selectedOption.equals("Get The Total Weight Of All Parts")) {
	 	    	procedure = "Get_The_Sum_Of_All_Parts_Weights()";
	 	    }
	 	    else if (selectedOption.equals("Get The Total Number of Shipments")) {
	 	    	procedure = "Get_The_Total_Number_Of_Shipments()";
	 	    }
	 	    else if (selectedOption.equals("Get The Name And Number Of Workers Of The Job With The Most Workers")) {
	 	    	procedure = "Get_The_Name_Of_The_Job_With_The_Most_Workers()";
	 	    }
	 	    else if (selectedOption.equals("List The Name and Status Of Every Supplier")) {
	 	    	procedure = "List_The_Name_And_Status_Of_All_Suppliers()";
	 	    }

	 	    Properties userProperties = new Properties();
	 	    InputStream filein = getClass().getClassLoader().getResourceAsStream("../lib/theaccountant.properties");
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

	 	            // Call the stored procedure
	 	            CallableStatement callableStatement = connection.prepareCall("{call "+ procedure +"}");
	 	            boolean hasResultSet = callableStatement.execute();

	 	            // If the stored procedure returned a ResultSet
	 	            if (hasResultSet) {
	 	                ResultSet resultSet = callableStatement.getResultSet();
	 	                
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
	 	                // If the stored procedure didn't return a ResultSet
	 	                System.out.println("No ResultSet returned.");
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
	    RequestDispatcher dispatcher = request.getRequestDispatcher("/accountant.jsp");
	    dispatcher.forward(request, response);
	}
}
