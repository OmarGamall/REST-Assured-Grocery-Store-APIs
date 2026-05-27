# Orders Test Cases Documentation

This document contains the test cases for the **Orders** module of the Simple Grocery Store API, covering order creation, retrieval, listing, updates, deletion, client boundary validation, token authorization, and various edge cases.

## Orders Test Cases Table

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
      <td><b>TC_ORDER_001</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>201 Created</code> and <code>orderId</code> when creating an order with one cart item, and that the cart is deleted</td>
      <td>Positive</td>
      <td><code>POST /orders</code><br><code>GET /carts/:cartId</code><br><code>GET /orders/:orderId</code></td>
      <td>Valid authentication token, a cart exists with one item.</td>
      <td>
        1. Create a cart and add one random available product.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with the <code>cartId</code>, a random customer name, and comment using the bearer token.<br>
        3. Verify status code is 201 and retrieve the <code>orderId</code>.<br>
        4. Send a <code>GET</code> request to <code>/carts/{cartId}</code> and verify status code is 404.<br>
        5. Retrieve the order by ID and verify its fields match.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code> with <code>orderId</code> and <code>created=true</code>.<br>
        • HTTP Status for GET cart: <code>404 Not Found</code> (since cart is deleted upon order creation).<br>
        • HTTP Status for GET order: <code>200 OK</code> with correct details and items.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_002</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>201 Created</code> and <code>orderId</code> when creating an order with a cart containing multiple unique items, and that the cart is deleted</td>
      <td>Positive</td>
      <td><code>POST /orders</code><br><code>GET /carts/:cartId</code><br><code>GET /orders/:orderId</code></td>
      <td>Valid authentication token, a cart exists containing 5 unique items.</td>
      <td>
        1. Create a cart.<br>
        2. Add 5 unique available products to the cart.<br>
        3. Send a <code>POST</code> request to <code>/orders</code> with the <code>cartId</code>, customer name, and comment.<br>
        4. Verify status code is 201 and retrieve the <code>orderId</code>.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}</code> and verify status code is 404.<br>
        6. Retrieve the order by ID and verify it contains all 5 items with matching product IDs and quantities.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code> with <code>orderId</code>.<br>
        • HTTP Status for GET cart: <code>404 Not Found</code> (since cart is deleted upon order creation).<br>
        • HTTP Status for lookup: <code>200 OK</code> and the order items list matches the 5 unique items in the cart.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_003</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>401 Unauthorized</code> and validation error when an invalid bearer token is used</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>A cart exists with items.</td>
      <td>
        1. Create a cart and add one random available item.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with the <code>cartId</code> and customer name using an invalid token (e.g., <code>"invalid_token_12345"</code>).<br>
        3. Retrieve the response and verify status code is 401.<br>
        4. Verify the error response contains <code>"Invalid bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object: <code>{"error": "Invalid bearer token"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_004</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when attempting to place an order with an empty cart</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token, a valid empty cart exists.</td>
      <td>
        1. Create a new empty cart and retrieve <code>cartId</code>.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with the <code>cartId</code> and customer name using the valid token.<br>
        3. Retrieve the response and verify status code is 400.<br>
        4. Verify the error response contains <code>"cart is empty"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "cart is empty"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_005</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when placing an order with an invalid or non-existent cartId</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token.</td>
      <td>
        1. Define an invalid cart ID (e.g., <code>"non_existent_cart_id_12345"</code>).<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with the invalid cart ID and customer name using the valid token.<br>
        3. Retrieve the response and verify status code is 400.<br>
        4. Verify the error response contains <code>"cartId"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object containing <code>"cartId"</code> validation details.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_006</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when customerName parameter is missing or null</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token, a cart exists with one item.</td>
      <td>
        1. Create a cart and add one available item.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with <code>cartId</code> and <code>customerName</code> set to null using the valid token.<br>
        3. Retrieve the response and verify status code is 400.<br>
        4. Verify the error response contains <code>"customer name"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object containing <code>"customer name"</code> validation details.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_007</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when attempting to place a duplicate order using the same cartId</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token, a cart exists with one item.</td>
      <td>
        1. Create a cart and add an available product.<br>
        2. Send a <code>POST</code> request to place the first order. Verify status code is 201.<br>
        3. Attempt to place a second order using the exact same <code>cartId</code>.<br>
        4. Retrieve the response and verify status code is 400.<br>
        5. Verify the error response contains <code>"Invalid or missing cartId"</code>.
      </td>
      <td>
        • First order creation returns <code>201 Created</code>.<br>
        • Second order creation returns <code>400 Bad Request</code> with an error object containing <code>"Invalid or missing cartId"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_008</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when customerName is empty</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token, a cart exists with one item.</td>
      <td>
        1. Create a cart and add one available item.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with <code>cartId</code> and <code>customerName</code> set to <code>""</code> using the valid token.<br>
        3. Retrieve the response and verify status code is 400.<br>
        4. Verify the error response contains <code>"customer name"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object containing <code>"customer name"</code> validation details.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_009</b></td>
      <td>Verify that <code>POST /orders</code> returns <code>400 Bad Request</code> and validation error when the comment length is excessively long</td>
      <td>Negative</td>
      <td><code>POST /orders</code></td>
      <td>Valid authentication token, a cart exists with one item.</td>
      <td>
        1. Create a cart and add one available item.<br>
        2. Generate an excessively long comment string (e.g., 10000 characters).<br>
        3. Send a <code>POST</code> request to <code>/orders</code> with <code>cartId</code>, customer name, and the long comment.<br>
        4. Retrieve the response and verify status code is 400.<br>
        5. Verify the error response contains <code>"comment"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object containing <code>"comment"</code> validation details.
      </td>
      <td><b>Minor</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_010</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>204 No Content</code> and successfully deletes the order</td>
      <td>Positive</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>Valid authentication token, an order exists.</td>
      <td>
        1. Create a cart and add an item.<br>
        2. Place an order to obtain <code>orderId</code>.<br>
        3. Send a <code>DELETE</code> request to <code>/orders/{orderId}</code> using the bearer token.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/orders/{orderId}</code> and verify status code is 404.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Verification lookup returns <code>404 Not Found</code> with error message <code>"No order with id [orderId]"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_011</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>401 Unauthorized</code> and validation error when an invalid bearer token is used</td>
      <td>Negative</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>DELETE</code> request to <code>/orders/some-order-id</code> using <code>"invalid_token_12345"</code> as the token.<br>
        2. Retrieve the response and verify status code is 401.<br>
        3. Verify the error response contains <code>"bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object containing <code>"bearer token"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_012</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>401 Unauthorized</code> and validation error when bearer token is missing or null</td>
      <td>Negative</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>DELETE</code> request to <code>/orders/some-order-id</code> without any bearer token (null).<br>
        2. Retrieve the response and verify status code is 401.<br>
        3. Verify the error response contains <code>"bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object containing <code>"bearer token"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_013</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when attempting to delete the same order a second time</td>
      <td>Negative</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>Valid authentication token, an order exists.</td>
      <td>
        1. Create a cart and add an item.<br>
        2. Place an order to obtain <code>orderId</code>.<br>
        3. Send a <code>DELETE</code> request to <code>/orders/{orderId}</code> and verify status code is 204.<br>
        4. Attempt to send a <code>DELETE</code> request to the same <code>orderId</code> again.<br>
        5. Retrieve the response and verify status code is 404.<br>
        6. Verify the error response contains <code>"No order with id [orderId]"</code>.
      </td>
      <td>
        • First deletion returns <code>204 No Content</code>.<br>
        • Second deletion returns <code>404 Not Found</code> with error message <code>"No order with id [orderId]"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_014</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when the orderId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>Valid authentication token.</td>
      <td>
        1. Send a <code>DELETE</code> request to <code>/orders/non_existent_order_id_12345</code> using the valid token.<br>
        2. Retrieve the response and verify status code is 404.<br>
        3. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_015</b></td>
      <td>Verify that <code>DELETE /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when attempting to delete an order belonging to a different API client</td>
      <td>Negative</td>
      <td><code>DELETE /orders/:orderId</code></td>
      <td>Two separate registered API clients.</td>
      <td>
        1. Register Client A and obtain <code>FirstClientToken</code>.<br>
        2. Create a cart, add an item, and place an order using <code>FirstClientToken</code> to obtain <code>orderId</code>.<br>
        3. Register Client B and obtain <code>SecondClientToken</code>.<br>
        4. Send a <code>DELETE</code> request to delete <code>orderId</code> using <code>SecondClientToken</code>.<br>
        5. Retrieve the response and verify status code is 404.<br>
        6. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_016</b></td>
      <td>Verify that <code>GET /orders</code> returns <code>200 OK</code> and an array containing all orders placed by the authenticated API client</td>
      <td>Positive</td>
      <td><code>GET /orders</code></td>
      <td>Valid authentication token, at least one order exists for the client.</td>
      <td>
        1. Create a cart, add an item, and place an order using the valid token to obtain <code>orderId</code>.<br>
        2. Send a <code>GET</code> request to <code>/orders</code> using the bearer token.<br>
        3. Verify status code is 200.<br>
        4. Verify that the response body is a non-empty array of orders containing the placed order with matching customer name, comment, items, and quantities.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains an array of orders containing the specific order placed with matching fields.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_017</b></td>
      <td>Verify that <code>GET /orders</code> returns <code>401 Unauthorized</code> and validation error when an invalid bearer token is used</td>
      <td>Negative</td>
      <td><code>GET /orders</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/orders</code> using <code>"invalid_token_12345"</code> as the token.<br>
        2. Retrieve the response and verify status code is 401.<br>
        3. Verify the error response contains <code>"bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object containing <code>"bearer token"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_018</b></td>
      <td>Verify that <code>GET /orders/{orderId}</code> returns <code>200 OK</code> and order details when retrieving a valid order by ID</td>
      <td>Positive</td>
      <td><code>GET /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Valid authentication token, an order exists.</td>
      <td>
        1. Create a cart, add an item, and place an order using the valid token to obtain <code>orderId</code>.<br>
        2. Send a <code>GET</code> request to <code>/orders/{orderId}</code> using the bearer token.<br>
        3. Verify status code is 200.<br>
        4. Verify that the response body is a JSON object with matching id, customerName, comment, created timestamp, and items.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains the correct order details matching the requested order ID.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_019</b></td>
      <td>Verify that <code>GET /orders/{orderId}</code> returns <code>200 OK</code> and includes invoice details when query parameter invoice is set to true</td>
      <td>Positive</td>
      <td><code>GET /orders/:orderId</code><br>Path Param: <code>orderId</code><br>Query Param: <code>invoice=true</code></td>
      <td>Valid authentication token, an order exists.</td>
      <td>
        1. Create a cart, add an item, and place an order using the valid token to obtain <code>orderId</code>.<br>
        2. Send a <code>GET</code> request to <code>/orders/{orderId}</code> with query parameter <code>invoice</code> set to <code>true</code> using the bearer token.<br>
        3. Verify status code is 200.<br>
        4. Verify that the response body contains the order details and the invoice details are not null.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body includes order details with <code>invoice</code> field present.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_020</b></td>
      <td>Verify that <code>GET /orders/{orderId}</code> returns <code>401 Unauthorized</code> and validation error when an invalid bearer token is used</td>
      <td>Negative</td>
      <td><code>GET /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>None.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/orders/some-order-id</code> using <code>"invalid_token_12345"</code> as the token.<br>
        2. Retrieve the response and verify status code is 401.<br>
        3. Verify the error response contains <code>"bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object containing <code>"bearer token"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_021</b></td>
      <td>Verify that <code>GET /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when the orderId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>GET /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Valid authentication token.</td>
      <td>
        1. Send a <code>GET</code> request to <code>/orders/non_existent_order_id_12345</code> using the valid token.<br>
        2. Retrieve the response and verify status code is 404.<br>
        3. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_022</b></td>
      <td>Verify that <code>GET /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when attempting to retrieve an order belonging to a different API client</td>
      <td>Negative</td>
      <td><code>GET /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Two separate registered API clients.</td>
      <td>
        1. Register Client A and obtain <code>FirstClientToken</code>.<br>
        2. Create a cart, add an item, and place an order using <code>FirstClientToken</code> to obtain <code>orderId</code>.<br>
        3. Register Client B and obtain <code>SecondClientToken</code>.<br>
        4. Send a <code>GET</code> request to retrieve <code>orderId</code> using <code>SecondClientToken</code>.<br>
        5. Retrieve the response and verify status code is 404.<br>
        6. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_023</b></td>
      <td>Verify that <code>PATCH /orders/{orderId}</code> returns <code>204 No Content</code> and updates the order's customerName and comment fields</td>
      <td>Positive</td>
      <td><code>PATCH /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Valid authentication token, an order exists.</td>
      <td>
        1. Create a cart, add an item, and place an order to obtain <code>orderId</code>.<br>
        2. Send a <code>PATCH</code> request to <code>/orders/{orderId}</code> with new <code>customerName</code> and <code>comment</code> values in JSON format using the bearer token.<br>
        3. Verify the status code is 204.<br>
        4. Retrieve the order by ID and verify that <code>customerName</code> and <code>comment</code> fields match the updated values.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of the order verifies that <code>customerName</code> and <code>comment</code> were successfully updated.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_024</b></td>
      <td>Verify that <code>PATCH /orders/{orderId}</code> returns <code>401 Unauthorized</code> and validation error when an invalid bearer token is used</td>
      <td>Negative</td>
      <td><code>PATCH /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>An order exists.</td>
      <td>
        1. Create a cart, add an item, and place an order using the valid token to obtain <code>orderId</code>.<br>
        2. Send a <code>PATCH</code> request to <code>/orders/{orderId}</code> with <code>customerName</code> and <code>comment</code> fields using an invalid token (e.g., <code>"invalid_token_12345"</code>).<br>
        3. Retrieve the response and verify status code is 401.<br>
        4. Verify the error response contains <code>"bearer token"</code>.
      </td>
      <td>
        • HTTP Status: <code>401 Unauthorized</code><br>
        • Response body contains an error object containing <code>"bearer token"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_025</b></td>
      <td>Verify that <code>PATCH /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when the orderId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>PATCH /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Valid authentication token.</td>
      <td>
        1. Send a <code>PATCH</code> request to <code>/orders/non_existent_order_id_12345</code> with update details using the valid token.<br>
        2. Retrieve the response and verify status code is 404.<br>
        3. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_026</b></td>
      <td>Verify that <code>PATCH /orders/{orderId}</code> returns <code>404 Not Found</code> and validation error when attempting to update an order belonging to a different API client</td>
      <td>Negative</td>
      <td><code>PATCH /orders/:orderId</code><br>Path Param: <code>orderId</code></td>
      <td>Two separate registered API clients.</td>
      <td>
        1. Register Client A and obtain <code>firstClientToken</code>.<br>
        2. Create a cart, add an item, and place an order using <code>firstClientToken</code> to obtain <code>orderId</code>.<br>
        3. Register Client B and obtain <code>secondClientToken</code>.<br>
        4. Send a <code>PATCH</code> request to update <code>orderId</code> using <code>secondClientToken</code>.<br>
        5. Retrieve the response and verify status code is 404.<br>
        6. Verify the error response contains <code>"No order with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object containing <code>"No order with id"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_ORDER_027</b></td>
      <td>Verify the end-to-end flow of creating a new order, retrieving it by ID, verifying it in the orders list, deleting it, and verifying its deletion returns 404</td>
      <td>Positive</td>
      <td><code>POST /orders</code><br><code>GET /orders/:orderId</code><br><code>GET /orders</code><br><code>DELETE /orders/:orderId</code></td>
      <td>Valid authentication token, an available product exists, and a new cart with the product added exists.</td>
      <td>
        1. Create a new cart and add a valid available product with quantity = 1.<br>
        2. Send a <code>POST</code> request to <code>/orders</code> with the <code>cartId</code>, <code>customerName</code>, and <code>comment</code> using the bearer token.<br>
        3. Verify status code is 201 and retrieve the <code>orderId</code>.<br>
        4. Send a <code>GET</code> request to <code>/orders/{orderId}</code>. Verify status code is 200, and details match.<br>
        5. Send a <code>GET</code> request to <code>/orders</code>. Verify status code is 200 and the list contains the created order.<br>
        6. Send a <code>DELETE</code> request to <code>/orders/{orderId}</code>. Verify status code is 204.<br>
        7. Send a <code>GET</code> request to <code>/orders/{orderId}</code> and verify status code is 404.
      </td>
      <td>
        • Order creation returns <code>201 Created</code> and <code>orderId</code>.<br>
        • Order lookup returns <code>200 OK</code> with correct <code>customerName</code>, <code>comment</code>, and items.<br>
        • Order list lookup returns <code>200 OK</code> and contains the created order.<br>
        • Deletion returns <code>204 No Content</code>.<br>
        • Verification lookup returns <code>404 Not Found</code> with error message <code>"No order with id [orderId]"</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
  </tbody>
</table>
