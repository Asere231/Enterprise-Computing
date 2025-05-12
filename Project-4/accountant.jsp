<%@ page contentType="text/html;charset=UTF-8" language="java" %> 
<%@ page import="java.util.List" %> 
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Accountant Page</title>
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

      .select {
        margin-bottom: 20px;
      }

      .select input[type="radio"] {
        margin-bottom: 20px;
      }

      .select label {
        display: block;
        margin-bottom: 2px;
      }

      #buttons {
        display: flex;
        justify-content: center;
      }

      #labels {
        font-weight: bold;
        color: rgb(255, 112, 30);
      }

      #returns {
        font-weight: 300;
        color: #333;
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
        margin-bottom: 25px;
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

      h2 {
        display: flex;
        justify-content: center;
        color: tomato;
        text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
      }
    </style>
  </head>
  <body>
    <h2>Accountant User Page</h2>
    <div class="container">
      <form action="/Project-4/accountantUser" method="POST">
        <h3>
          Please select the operation you would like to perform from the list
          below:
        </h3>
        <div class="select">
          <label id="labels">
            <input
              type="radio"
              name="option"
              id="option1"
              value="Get The Maximum Status Value Of All Suppliers"
            />
            Get The Maximum Status Value Of All Suppliers
            <a id="returns">(Returns a maximum value)</a>
          </label>
          <label id="labels">
            <input
              type="radio"
              name="option"
              id="option2"
              value="Get The Total Weight Of All Parts"
            />
            Get The Total Weight Of All Parts
            <a id="returns">(Returns a sum)</a>
          </label>
          <label id="labels">
            <input
              type="radio"
              name="option"
              id="option3"
              value="Get The Total Number of Shipments"
            />
            Get The Total Number of Shipments
            <a id="returns"
              >(Returns the current number of shipments in total)</a
            >
          </label>
          <label id="labels">
            <input
              type="radio"
              name="option"
              id="option4"
              value="Get The Name And Number Of Workers Of The Job With The Most Workers"
            />
            Get The Name And Number Of Workers Of The Job With The Most Workers
            <a id="returns">(Returns two values)</a>
          </label>
          <label id="labels">
            <input
              type="radio"
              name="option"
              id="option4"
              value="List The Name and Status Of Every Supplier"
            />
            List The Name and Status Of Every Supplier
            <a id="returns">(Returns a list of supplier names with status)</a>
          </label>
        </div>
        <div id="buttons">
          <button type="submit">Execute Command</button>
          <button type="submit" name="clearResult" value="true">Clear Result</button>
        </div>
      </form>

      <div id="executionResult">
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

        <%-- Display number of rows affected --%>
        <% if (request.getAttribute("rowsAffected") != null) { %>
            <div style="padding: 20px;background-color: #ccffcc;">
                <h2>Rows Affected:</h2>
                <p><%= request.getAttribute("rowsAffected") %> row(s) affected.</p>
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
