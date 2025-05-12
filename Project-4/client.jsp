<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ page import="java.util.List" %> 
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Client Page</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 0;
      }

      .container {
        max-width: 800px;
        margin: 20px auto;
        padding: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }

      .textarea-container {
        margin-bottom: 20px;
      }

      textarea {
        width: calc(100% - 20px);
        height: 100px;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        resize: none;
        font-size: 16px;
      }

      #buttons {
        display: flex;
        justify-content: center;
      }

      button {
        padding: 10px 20px;
        background-color: #007bff;
        color: #fff;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 16px;
        margin-right: 10px;
      }

      button:hover {
        background-color: #0056b3;
      }

      button:last-child {
        margin-right: 0;
      }

      h2,
      h3 {
        color: #333;
        margin-bottom: 15px;
        display: flex;
        justify-content: center;
      }

      table {
        width: 100%;
        border-collapse: collapse;
      }

      th,
      td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
      }

      th {
        background-color: #f2f2f2;
      }

      h3 {
        display: flex;
        justify-content: center;
        color: tomato;
        text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
      }
    </style>
  </head>
  <body>
    <h3>Client User Page</h3>
    <div class="container">
      <form action="/Project-4/clientUser" method="POST">
        <div class="textarea-container">
          <textarea
            name="mysqlCommand"
            placeholder="Enter MySQL command..."
          ></textarea>
        </div>

        <div id="buttons">
          <button type="submit">Execute Command</button>
          <button type="reset">Reset Form</button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>

      <div id="executionResult">
        <h2>Execution Result:</h2>
        <%-- Check if there are rows in the result set --%>
        <% if (request.getAttribute("rows") != null && !((List<Map<String, String>>)request.getAttribute("rows")).isEmpty()) { %>
            <%-- Display result set in a table --%>
            <table>
                <thead>
                    <tr>
                        <%-- Iterate over column names --%>
                        <% for (String columnName : (List<String>)request.getAttribute("columnNames")) { %>
                            <th><%= columnName %></th>
                        <% } %>
                    </tr>
                </thead>
                <tbody>
                    <%-- Iterate over rows --%>
                    <% for (Map<String, String> row : (List<Map<String, String>>)request.getAttribute("rows")) { %>
                        <tr>
                            <%-- Iterate over columns --%>
                            <% for (String columnName : (List<String>)request.getAttribute("columnNames")) { %>
                                <td><%= row.get(columnName) %></td>
                            <% } %>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>

        <%-- Display number of rows affected and the status of Business logic as a message --%>
        <% if (request.getAttribute("rowsAffected") != null) { %>
            <div style="text-align: center; padding: 10px; background-color: #ccffcc;">
                <p><%= request.getAttribute("rowsAffected") %> row(s) affected.</p>
                <% if (request.getAttribute("message") != null) { %>
                  <p><%= request.getAttribute("message") %></p>
                <% } %>
            </div>
        <% } %>
        
        <%-- Check if there's an error message --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div style="padding: 20px;background-color: #ffcccc;">
                <h2>Error:</h2>
                <p><%= request.getAttribute("errorMessage") %></p>
            </div>
        <% } %>       
      </div>
    </div>
  </body>
</html>
