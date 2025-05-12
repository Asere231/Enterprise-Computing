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
public class AuthenticationServlet extends HttpServlet {
	private MysqlDataSource dataSource;
    private Connection connection;
    private Statement statement;
    
	String username;
	String password;
	
	final String incorrectCredentialsPage = 
	        "<html>" +
	        "<head>" +
	        "<title>Incorrect Password</title>" +
	        "<style>" +
	        "body {" +
	        "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
	        "    background-color: #f8f9fa;" +
	        "    color: #333;" +
	        "    text-align: center;" +
	        "    padding: 50px;" +
	        "}" +
	        ".container {" +
	        "    max-width: 600px;" +
	        "    margin: 0 auto;" +
	        "}" +
	        "h1 {" +
	        "    color: #dc3545;" +
	        "    font-size: 2.5rem;" +
	        "    margin-bottom: 30px;" +
	        "}" +
	        "p {" +
	        "    font-size: 1.2rem;" +
	        "}" +
	        "a {" +
	        "    color: #007bff;" +
	        "    text-decoration: none;" +
	        "}" +
	        "</style>" +
	        "</head>" +
	        "<body>" +
	        "<div class='container'>" +
	        "<h1>Oops!</h1>" +
	        "<p>It seems you've entered the wrong credentials. Please go back and try again.</p>" +
	        "</div>" +
	        "</body>" +
	        "</html>";

	
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException  {
		this.username = request.getParameter("username").trim();
		this.password = request.getParameter("password").trim();
				
        response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
		
		Properties userProperties = new Properties();
        InputStream filein = getClass().getClassLoader().getResourceAsStream("../lib/root.properties");
        userProperties.load(filein);
        
            
    	Properties databaseProperties = new Properties();
        InputStream databaseFile = getClass().getClassLoader().getResourceAsStream("../lib/credentialsDB.properties");
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
                
                String checkCredentials = "SELECT * FROM usercredentials WHERE login_username = '" + username + "' AND login_password = '" + password + "'";
                
                ResultSet resultSet = statement.executeQuery(checkCredentials);
                
                if (resultSet.next()) {
                    // Login successful, redirect based on user
                	switch (username) {
	                    case "root":
	                    	response.sendRedirect("root.jsp");
	                        break;
	                    case "client":
	                    	response.sendRedirect("client.jsp");
	                        break;
	                    case "dataentryuser":
	                    	response.sendRedirect("data-entry.jsp");
	                        break;
	                    case "theaccountant":
	                    	response.sendRedirect("accountant.jsp");
	                        break;
	                      
                	}
                	
                } else {
                    // Login failed
                    out.println(incorrectCredentialsPage);
                }
                
            }
            else {
                System.out.println("Connection failed!");

            }
            

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        finally {
        	// Close all connections to database
        	try {
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
}
