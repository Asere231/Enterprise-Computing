import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;

@SuppressWarnings("serial")
public class RootUserServlet extends HttpServlet {
	private MysqlDataSource dataSource;
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    int rowsAffected = 0; // Initialize rowsAffected variable
	    int rowsAffectedBusinessLogic = 0; // Initialize rowsAffectedBusinessLogic variable
	    String errorMessage = null; // Initialize errorMessage variable
	    String message = null; // Initialize message variable
	    
	    if (request.getParameter("clearResult") != null) {
	        // Clear the attributes holding the result
	        request.removeAttribute("rows");
	        request.removeAttribute("columnNames");
	    }
	    else {
	    
		    String sql = request.getParameter("mysqlCommand").trim();
		    
		    System.out.println("sql trimmed: " + sql);
	
		    Properties userProperties = new Properties();
		    InputStream filein = getClass().getClassLoader().getResourceAsStream("../lib/root.properties");
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
	
		            String[] commandArray = sql.split("[^a-zA-Z0-9]+");
		            String keyword = commandArray[0].trim();
		            
		            System.out.println("commandArray: " + Arrays.toString(commandArray));
		            System.out.println("keyword: " + keyword);
		            
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
	                	
	                	
	                	
	                	if(sql.contains("shipments") && keyword.equals("insert")) {
	                		
	                		message = "Business Logic Detected!! - Updating Supplier's Status\n";
	                		rowsAffected = statement.executeUpdate(sql);
	                	    
	                	    String snum = commandArray[4].trim();
	                	    int quantity = Integer.parseInt(commandArray[7].trim());
	                	    
	                	    System.out.println("snum: " + snum);
	                	    System.out.println("Quantity: " + quantity);
	                		
	                		if (quantity >= 100) {		 	               
		 	                	
		 	                    // Update the status in the suppliers table by adding 5 to the current value
		 	                    String updateSql = "UPDATE suppliers SET status = status + 5 WHERE snum = '" + snum + "'";
		 	                    rowsAffectedBusinessLogic = statement.executeUpdate(updateSql);
		 	                    
	 	                        message += "Business Logic updated " + rowsAffectedBusinessLogic + " supplier(s) status mark";	                        	          
		 	                }
	                		else {
	                			
	                			message += "Business Logic updated 0 supplier status mark";	                        	          		 	                
	                		}
		 	                	                			               
	                	}
	                	else if (sql.contains("shipments") && keyword.equals("update")) {
	                		message = "Business Logic Detected!! - Updating Supplier's Status\n";
	                		
	                		String[] beforeUpdateStatements = {
	                			    "DROP TABLE IF EXISTS beforeShipments;",
	                			    "CREATE TABLE beforeShipments LIKE shipments;",
	                			    "INSERT INTO beforeShipments SELECT * FROM shipments;"
	                			};

                			for (String beforeUpdates : beforeUpdateStatements) {
                			    statement.execute(beforeUpdates);
                			}
	                		
	                		int whereIndex = 0;

	                		for (int i = 0; i < commandArray.length; i++) {
	                		    if (commandArray[i].equals("where")) {
	                		        whereIndex = i;
	                		        break;
	                		    }
	                		}
	                		
	                		rowsAffected = statement.executeUpdate(sql);

	                		if (whereIndex != 0) {
//	                		    String updateSql = "UPDATE suppliers SET status = status + 5 WHERE snum IN (SELECT snum FROM shipments WHERE quantity >= 100 AND " + commandArray[whereIndex + 1] + " = '" + commandArray[whereIndex + 2] + "')";
	                		    
	                			String updateSql = "UPDATE suppliers SET status = status + 5 "
	                                    + "WHERE suppliers.snum IN ("
	                                    + "    SELECT DISTINCT snum FROM shipments "
	                                    + "    WHERE quantity >= 100 AND NOT EXISTS ("
	                                    + "        SELECT * FROM beforeShipments "
	                                    + "        WHERE shipments.snum = beforeShipments.snum AND shipments.pnum = beforeShipments.pnum AND beforeShipments.quantity >= 100"
	                                    + "    )"
	                                    + ");";

	                			rowsAffectedBusinessLogic = statement.executeUpdate(updateSql);


	                		    message += "Business Logic updated " + rowsAffectedBusinessLogic + " supplier(s) status mark";
	                		    
	                		    statement.execute("DROP TABLE beforeShipments;");
	                		}

	                	}
	                	else {	                
	 	                	message = "Business Logic Not Triggered!!!";
	 	                	rowsAffected = statement.executeUpdate(sql);
	 	                }	                		                		             
	                	
//	                	rowsAffected = statement.executeUpdate(sql);
	                	request.setAttribute("message", message);
	                	
	                	if (rowsAffected > 0) {
	                		request.setAttribute("rowsAffected", rowsAffected);
	                	}
	                	else {
	                		request.setAttribute("rowsAffected", rowsAffectedBusinessLogic);
	                	}
	                
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
	    RequestDispatcher dispatcher = request.getRequestDispatcher("/root.jsp");
	    dispatcher.forward(request, response);
	}

}
