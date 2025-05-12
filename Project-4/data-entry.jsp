<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ page import="java.util.List" %> 
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Suppliers Record Insert</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 0;
        display: grid;
        grid-template-columns: repeat(
          auto-fit,
          minmax(300px, 1fr)
        ); /* Adjust size here */
        grid-gap: 20px;
      }

      .container {
        padding: 10px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }

      table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 5px;
      }

      th,
      td {
        border: 1px solid #ddd;
        padding: 4px; /* Adjust size here */
        text-align: left;
      }

      th {
        background-color: #f2f2f2;
        text-align: center;
      }

      input[type="text"] {
        width: calc(100% - 16px); /* Adjust size here */
        padding: 6px; /* Adjust size here */
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px; /* Adjust size here */
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

      h2,
      h3 {
        display: flex;
        justify-content: center;
      }

      h2 {
        color: tomato;
        text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
      }

      button:hover {
        background-color: #0056b3;
      }

      button:last-child {
        margin-right: 0;
      }

      .execution-result {
        grid-column: 1 / -1;
      }

      .title-container {
        grid-column: 1 / -1;
      }
    </style>
  </head>
  <body>
    <div class="title-container">
      <h2>Data Entry Page</h2>
    </div>

    <div class="container">
      <form action="/Project-4/suppliers" method="POST">
        <h3>Suppliers Record Insert</h3>
        <table>
          <thead>
            <tr>
              <th>snum</th>
              <th>sname</th>
              <th>status</th>
              <th>city</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" name="snum" /></td>
              <td><input type="text" name="sname" /></td>
              <td><input type="text" name="status" /></td>
              <td><input type="text" name="city" /></td>
            </tr>
          </tbody>
        </table>
        <div id="buttons">
          <button type="submit">Enter Supplier Record Into Database</button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>
    </div>

    <div class="container">
      <form action="/Project-4/parts" method="POST">
        <h3>Parts Record Insert</h3>
        <table>
          <thead>
            <tr>
              <th>pnum</th>
              <th>pname</th>
              <th>color</th>
              <th>weight</th>
              <th>city</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" name="pnum" /></td>
              <td><input type="text" name="pname" /></td>
              <td><input type="text" name="color" /></td>
              <td><input type="text" name="weight" /></td>
              <td><input type="text" name="city" /></td>
            </tr>
          </tbody>
        </table>
        <div id="buttons">
          <button onclick="enterRecord()">
            Enter Part Record Into Database
          </button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>
    </div>

    <div class="container">
      <form action="/Project-4/jobs" method="POST">
        <h3>Jobs Record Insert</h3>
        <table>
          <thead>
            <tr>
              <th>jnum</th>
              <th>jname</th>
              <th>numworkers</th>
              <th>city</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" name="jnum" /></td>
              <td><input type="text" name="jname" /></td>
              <td><input type="text" name="numworkers" /></td>
              <td><input type="text" name="city" /></td>
            </tr>
          </tbody>
        </table>
        <div id="buttons">
          <button onclick="enterRecord()">
            Enter Job Record Into Database
          </button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>
    </div>

    <div class="container">
      <form action="/Project-4/shipments" method="POST">
        <h3>Shipments Record Insert</h3>
        <table>
          <thead>
            <tr>
              <th>snum</th>
              <th>pnum</th>
              <th>jnum</th>
              <th>quantity</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" name="snum" /></td>
              <td><input type="text" name="pnum" /></td>
              <td><input type="text" name="jnum" /></td>
              <td><input type="text" name="quantity" /></td>
            </tr>
          </tbody>
        </table>
        <div id="buttons">
          <button onclick="enterRecord()">
            Enter Job Record Into Database
          </button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>
    </div>

    <div class="container execution-result">
      <h3>Execution Result:</h3>
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
  </body>
</html>
