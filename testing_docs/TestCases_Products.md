# Products Test Cases Documentation

This document contains the test cases for the **Products** module of the Simple Grocery Store API, covering both happy path scenarios and boundary/validation checks.

## Products Test Cases Table

<table>
  <thead>
    <tr>
      <th width="8%">Test ID</th>
      <th width="24%">Title</th>
      <th width="8%">Type</th>
      <th width="12%">Requirement Traceability</th>
      <th width="13%">Pre-conditions</th>
      <th width="17%">Test Execution Steps</th>
      <th width="13%">Expected Result</th>
      <th width="5%">Risk Level</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>TC_PROD_001</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and only in-stock products when <code>available</code> query parameter is set to <code>true</code></td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Param: <code>available=true</code></td>
      <td>Products exist in the inventory with both <code>inStock=true</code> and <code>inStock=false</code>.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>available</code> query parameter set to <code>true</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Iterate through each product in the response array and check its <code>inStock</code> status.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a JSON array of products.<br>
        • Every product in the array has <code>"inStock": true</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_002</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and only out-of-stock products when <code>available</code> query parameter is set to <code>false</code></td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Param: <code>available=false</code></td>
      <td>Products exist in the inventory with both <code>inStock=true</code> and <code>inStock=false</code>.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>available</code> query parameter set to <code>false</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Iterate through each product in the response array and check its <code>inStock</code> status.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a JSON array of products.<br>
        • Every product in the array has <code>"inStock": false</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_003</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and products belonging to the specified category when <code>category</code> query parameter is provided</td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Param: <code>category</code></td>
      <td>Products exist in the inventory for all valid categories (<code>meat-seafood</code>, <code>fresh-produce</code>, <code>candy</code>, <code>bread-bakery</code>, <code>dairy</code>, <code>eggs</code>, <code>coffee</code>).</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>category</code> query parameter set to a valid category (e.g., <code>coffee</code>).<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Iterate through each product in the response and verify its category.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a JSON array of products.<br>
        • Every product in the array has its <code>category</code> property match the requested category.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_004</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and limits the number of products to the value of <code>results</code> query parameter when <code>results</code> parameter is between 1 and 20</td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Param: <code>results</code></td>
      <td>At least 5 products exist in the inventory.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>results</code> query parameter set to <code>5</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Count the number of products in the returned array.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a JSON array of products.<br>
        • The size of the returned array is less than or equal to <code>5</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_005</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and filters products by category, availability, and limit when <code>category</code>, <code>available</code>, and <code>results</code> query parameters are provided</td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Params: <code>category</code>, <code>available</code>, <code>results</code></td>
      <td>Products exist in the inventory matching the category and availability filters.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with query parameters: <code>category=fresh-produce</code>, <code>available=true</code>, and <code>results=3</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Verify the size of the returned array and check the attributes of each product.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a JSON array of products.<br>
        • The size of the returned array is less than or equal to <code>3</code>.<br>
        • Every product in the array has <code>"category": "fresh-produce"</code> and <code>"inStock": true</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_006</b></td>
      <td>Verify that <code>GET /products</code> returns <code>200 OK</code> and zero products when <code>results</code> query parameter is set to <code>0</code></td>
      <td>Positive</td>
      <td><code>GET /products</code><br>Query Param: <code>results=0</code></td>
      <td>Products exist in the inventory.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>results</code> query parameter set to <code>0</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Verify the size of the returned array.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains an empty JSON array <code>[]</code>.
      </td>
      <td><b>Minor</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_007</b></td>
      <td>Verify that <code>GET /products</code> returns <code>400 Bad Request</code> and validation error when an invalid <code>category</code> query parameter is provided</td>
      <td>Negative</td>
      <td><code>GET /products</code><br>Query Param: <code>category</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>category</code> query parameter set to <code>"invalid-category"</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid value for query parameter 'category'"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_008</b></td>
      <td>Verify that <code>GET /products</code> returns <code>400 Bad Request</code> and validation error when <code>results</code> query parameter is below 0 (e.g., <code>-1</code>)</td>
      <td>Negative</td>
      <td><code>GET /products</code><br>Query Param: <code>results</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>results</code> query parameter set to <code>-1</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid value for query parameter 'results'"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_009</b></td>
      <td>Verify that <code>GET /products</code> returns <code>400 Bad Request</code> and validation error when <code>results</code> query parameter exceeds 20 (e.g., <code>1000</code>)</td>
      <td>Negative</td>
      <td><code>GET /products</code><br>Query Param: <code>results</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/products</code> with <code>results</code> query parameter set to <code>1000</code>.<br>
        2. Retrieve the response.<br>
        3. Verify the HTTP status code.<br>
        4. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid value for query parameter 'results'"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_010</b></td>
      <td>Verify that <code>GET /products/{productId}</code> returns <code>200 OK</code> and product details when a valid product ID is requested</td>
      <td>Positive</td>
      <td><code>GET /products/:productId</code><br>Path Param: <code>productId</code></td>
      <td>A valid product exists in the inventory.</td>
      <td>
        1. Retrieve a valid product from the inventory to obtain its ID.<br>
        2. Send a <code>GET</code> request to <code>/products/{productId}</code> using that valid ID.<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code.<br>
        5. Deserialize the response and compare the fields (<code>id</code>, <code>category</code>, <code>name</code>, <code>inStock</code>) with the expected product details.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains a single product object matching the requested product ID.<br>
        • The product object has correct <code>id</code>, <code>category</code>, <code>name</code>, and <code>inStock</code> details.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_PROD_011</b></td>
      <td>Verify that <code>GET /products/{productId}</code> returns <code>404 Not Found</code> and error message when a non-existent product ID is requested</td>
      <td>Negative</td>
      <td><code>GET /products/:productId</code><br>Path Param: <code>productId</code></td>
      <td>None.</td>
      <td>
        1. Define a non-existent product ID (e.g., <code>9999</code>).<br>
        2. Send a <code>GET</code> request to <code>/products/9999</code>.<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code.<br>
        5. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No product with id 9999"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
  </tbody>
</table>
