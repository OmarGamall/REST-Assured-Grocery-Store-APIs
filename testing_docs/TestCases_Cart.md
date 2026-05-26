# Cart Test Cases Documentation

This document contains the test cases for the **Cart** module of the Simple Grocery Store API, covering cart creation, item additions, modifications, replacements, deletions, and all associated validation scenarios.

## Cart Test Cases Table

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
      <td><b>TC_CART_001</b></td>
      <td>Verify that <code>POST /carts</code> returns <code>201 Created</code> and cartId when creating a new cart, and <code>GET /carts/{cartId}</code> returns <code>200 OK</code> and empty items list</td>
      <td>Positive</td>
      <td><code>POST /carts</code><br><code>GET /carts/:cartId</code></td>
      <td>None.</td>
      <td>
        1. Send an empty <code>POST</code> request to <code>/carts</code>.<br>
        2. Verify response status code is 201 and retrieve the <code>cartId</code>.<br>
        3. Send a <code>GET</code> request to <code>/carts/{cartId}</code> using the created <code>cartId</code>.<br>
        4. Verify status code is 200.<br>
        5. Check that the created timestamp is present and the items list is empty.
      </td>
      <td>
        • HTTP Status for creation: <code>201 Created</code><br>
        • Response body contains <code>created=true</code> and a non-null <code>cartId</code>.<br>
        • HTTP Status for retrieval: <code>200 OK</code><br>
        • Response body contains cart details with an empty <code>items</code> array.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_002</b></td>
      <td>Verify that <code>GET /carts/{cartId}/items</code> returns <code>200 OK</code> and an empty items list when a new empty cart is requested</td>
      <td>Positive</td>
      <td><code>GET /carts/:cartId/items</code></td>
      <td>A new empty cart is created.</td>
      <td>
        1. Create a new empty cart and retrieve its <code>cartId</code>.<br>
        2. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code>.<br>
        3. Verify status code is 200 and the response body returns an empty array.
      </td>
      <td>
        • HTTP Status: <code>200 OK</code><br>
        • Response body contains an empty JSON array of items <code>[]</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_003</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>201 Created</code> and successfully adds an item when a valid productId and quantity are provided</td>
      <td>Positive</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists, and a valid available product exists.</td>
      <td>
        1. Create a new empty cart and retrieve <code>cartId</code>.<br>
        2. Retrieve a random available product and determine a random quantity less than or equal to stock.<br>
        3. Send a <code>POST</code> request to <code>/carts/{cartId}/items</code> with <code>productId</code> and <code>quantity</code>.<br>
        4. Verify status code is 201 and retrieve the <code>itemId</code> from the response.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the cart contains exactly 1 item matching the added <code>productId</code>, <code>quantity</code>, and <code>itemId</code>.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code><br>
        • Response body contains <code>created=true</code> and a non-null <code>itemId</code>.<br>
        • Retrieval of cart items verifies the product was added with the specified quantity and <code>itemId</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_004</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>201 Created</code> and adds the item when quantity is exactly equal to the product's current stock</td>
      <td>Positive</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists, and a valid available product exists.</td>
      <td>
        1. Create a new empty cart and retrieve <code>cartId</code>.<br>
        2. Retrieve a random available product and set quantity to its exact <code>currentStock</code>.<br>
        3. Send a <code>POST</code> request to <code>/carts/{cartId}/items</code> with <code>productId</code> and <code>quantity</code> equal to stock.<br>
        4. Verify status code is 201 and retrieve <code>itemId</code>.<br>
        5. Retrieve cart items and verify the product is added with the exact stock quantity.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code><br>
        • The item is successfully added and matches the exact stock quantity when retrieved.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_005</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>201 Created</code> for multiple unique products added sequentially to the same cart</td>
      <td>Positive</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists.</td>
      <td>
        1. Create a new empty cart and retrieve <code>cartId</code>.<br>
        2. Retrieve 10 random unique available products and random valid quantities.<br>
        3. Sequentially send <code>POST</code> requests to <code>/carts/{cartId}/items</code> for each of the 10 items.<br>
        4. Verify status code is 201 for each addition.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify it contains exactly 10 items with correct <code>productId</code> and <code>quantity</code> pairings.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code> for all 10 requests.<br>
        • Retrieval of cart items returns an array of size 10 containing all added products with their correct quantities.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_006</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when attempting to add a product that has already been added to the cart</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid cart exists containing a specific product.</td>
      <td>
        1. Create a cart and add an available product with a valid quantity.<br>
        2. Attempt to add the exact same product again to the same cart.<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code is 400.<br>
        5. Verify the error response contains <code>"This product has already been added to cart"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "This product has already been added to cart"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_007</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when attempting to add an out-of-stock product</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>An empty cart exists, and an out-of-stock (<code>inStock=false</code>) product exists in the inventory.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a random non-available product.<br>
        3. Send a <code>POST</code> request to add this product with quantity = 1.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"This product is not in stock and cannot be ordered"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "This product is not in stock and cannot be ordered"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_008</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when the requested quantity exceeds the product's current stock</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists, and a valid available product exists.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a random available product and get its current stock.<br>
        3. Send a <code>POST</code> request to add the product with quantity set to <code>currentStock + 1</code>.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"The quantity requested exceeds the current stock"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "The quantity requested exceeds the current stock"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_009</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when adding an item with a quantity of 0</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists, and a valid available product exists.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a random available product.<br>
        3. Send a <code>POST</code> request to add the product with quantity set to <code>0</code>.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"Quantity must be at least 1"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Quantity must be at least 1"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_010</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when adding an item with a negative quantity</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists, and a valid available product exists.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a random available product.<br>
        3. Send a <code>POST</code> request to add the product with quantity set to a negative value (e.g., -5).<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"Quantity must be at least 1"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Quantity must be at least 1"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_011</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>400 Bad Request</code> and validation error when adding an item with an invalid or non-existent productId</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>A valid empty cart exists.</td>
      <td>
        1. Create a cart.<br>
        2. Set <code>productId</code> to a non-existent ID (e.g., 999999).<br>
        3. Send a <code>POST</code> request to add this product to the cart with quantity = 1.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"Invalid or missing productId"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing productId"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_012</b></td>
      <td>Verify that <code>POST /carts/{cartId}/items</code> returns <code>404 Not Found</code> and validation error when the cartId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>POST /carts/:cartId/items</code></td>
      <td>None.</td>
      <td>
        1. Set <code>cartId</code> to <code>"invalid-cart-id"</code>.<br>
        2. Retrieve a random available product.<br>
        3. Send a <code>POST</code> request to add this product to the invalid cart ID with quantity = 1.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code is 404.<br>
        6. Verify the error response contains <code>"No cart with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No cart with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_013</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and successfully deletes the item from the cart</td>
      <td>Positive</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a new cart.<br>
        2. Retrieve a random available product and add it to the cart, obtaining the <code>itemId</code>.<br>
        3. Send a <code>DELETE</code> request to <code>/carts/{cartId}/items/{itemId}</code>.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the items list is empty.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the item is no longer in the cart.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_014</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> for sequential deletions of multiple items in the same cart</td>
      <td>Positive</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing two unique items.</td>
      <td>
        1. Create a cart.<br>
        2. Add two unique available products to the cart, obtaining their respective <code>itemId1</code> and <code>itemId2</code>.<br>
        3. Send a <code>DELETE</code> request for <code>itemId1</code> and verify status code is 204.<br>
        4. Send a <code>DELETE</code> request for <code>itemId2</code> and verify status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify that the items list is empty.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code> for both requests.<br>
        • Retrieval of cart items verifies that both items have been removed.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_015</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when attempting to delete the same item a second time</td>
      <td>Negative</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item which is deleted once.</td>
      <td>
        1. Create a cart.<br>
        2. Add an available product to the cart and retrieve its <code>itemId</code>.<br>
        3. Delete the item and verify status code is 204.<br>
        4. Attempt to delete the same item again.<br>
        5. Retrieve the response and verify the HTTP status code is 404.<br>
        6. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • First delete returns <code>204 No Content</code>.<br>
        • Second delete returns <code>404 Not Found</code> with an error object containing the message <code>"No item with id"</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_016</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the cartId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A valid item has been added to a valid cart.</td>
      <td>
        1. Create a cart.<br>
        2. Add an available product and retrieve its <code>itemId</code>.<br>
        3. Set <code>invalidCartId</code> to <code>"invalid-cart-id"</code>.<br>
        4. Send a <code>DELETE</code> request for the item using <code>invalidCartId</code>.<br>
        5. Retrieve the response and verify status code is 404.<br>
        6. Verify the error response contains <code>"No cart with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No cart with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_017</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the itemId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product to the cart.<br>
        3. Send a <code>DELETE</code> request with an invalid <code>itemId</code> like <code>"invalid-item-id"</code>.<br>
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_018</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when attempting to delete an item from an empty cart</td>
      <td>Negative</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>A valid empty cart exists.</td>
      <td>
        1. Create a new cart.<br>
        2. Send a <code>DELETE</code> request with a non-existent item ID <code>"non-existent-item-id"</code>.<br>
        3. Retrieve the response and verify status code is 404.<br>
        4. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_019</b></td>
      <td>Verify that <code>DELETE /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when attempting to delete an item using a different cartId than the one it belongs to</td>
      <td>Negative</td>
      <td><code>DELETE /carts/:cartId/items/:itemId</code></td>
      <td>Two separate carts exist. Cart A has an item added.</td>
      <td>
        1. Create Cart A and Cart B.<br>
        2. Add a product to Cart A and retrieve its <code>itemIdA</code>.<br>
        3. Send a <code>DELETE</code> request to delete <code>itemIdA</code> but specifying the path parameter <code>cartId</code> as Cart B's ID.<br>
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_020</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and successfully updates the quantity of an item in the cart</td>
      <td>Positive</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item. The product has a stock of at least 2.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a product with stock >= 2 and add it to the cart with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with a request body specifying a new quantity of 2.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the quantity of the item is updated to 2.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the item quantity is updated to 2.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_021</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> when updating the quantity to the same value</td>
      <td>Positive</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item with quantity = 1.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product to the cart with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with quantity = 1.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the quantity remains 1.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the item quantity remains unchanged.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_022</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when the modified quantity exceeds the product's current stock</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart.<br>
        2. Retrieve a product with stock >= 2 and add it to the cart with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Determine <code>quantityExceedingStock = currentStock + 1</code>.<br>
        4. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with the quantity exceeding stock.<br>
        5. Retrieve the response and verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"The quantity requested is not available in stock"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "The quantity requested is not available in stock"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_023</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when modifying the quantity of an item to 0</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart and add an available product, obtaining the <code>itemId</code>.<br>
        2. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with quantity set to 0.<br>
        3. Retrieve the response and verify the HTTP status code is 400.<br>
        4. Verify the error response contains <code>"Invalid or missing quantity"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing quantity"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_024</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when modifying the quantity of an item to a negative number</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart and add an available product, obtaining the <code>itemId</code>.<br>
        2. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with quantity set to -5.<br>
        3. Retrieve the response and verify the HTTP status code is 400.<br>
        4. Verify the error response contains <code>"Invalid or missing quantity"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing quantity"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_025</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the itemId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart and add an available product.<br>
        2. Send a <code>PATCH</code> request with an invalid <code>itemId</code> like <code>"invalid-item-id"</code> and quantity = 2.<br>
        3. Retrieve the response and verify status code is 404.<br>
        4. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_026</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the cartId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart and add an available product, obtaining the <code>itemId</code>.<br>
        2. Send a <code>PATCH</code> request using an invalid <code>cartId</code> like <code>"invalid-cart-id"</code> for the valid <code>itemId</code> with quantity = 2.<br>
        3. Retrieve the response and verify status code is 404.<br>
        4. Verify the error response contains <code>"No cart with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No cart with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_027</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the quantity parameter is missing or null</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart and add an available product, obtaining the <code>itemId</code>.<br>
        2. Send a <code>PATCH</code> request to <code>/carts/{cartId}/items/{itemId}</code> with a request body where the quantity parameter is null or missing.<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code is 404.<br>
        5. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing quantity"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_028</b></td>
      <td>Verify that <code>PATCH /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when attempting to modify an item using a different cartId than the one it belongs to</td>
      <td>Negative</td>
      <td><code>PATCH /carts/:cartId/items/:itemId</code></td>
      <td>Two separate carts exist. Cart A has an item added.</td>
      <td>
        1. Create Cart A and Cart B.<br>
        2. Add a product to Cart A and retrieve its <code>itemId</code>.<br>
        3. Send a <code>PATCH</code> request to modify the item specifying the path parameter <code>cartId</code> as Cart B's ID, with quantity = 2.
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_029</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and successfully replaces both the product and its quantity</td>
      <td>Positive</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to <code>/carts/{cartId}/items/{itemId}</code> with a new available <code>productId</code> and quantity = 3.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the cart contains exactly 1 item with the replaced product and quantity.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the item has been replaced with the new product and quantity.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_030</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and updates the quantity when the productId is unchanged but the quantity is modified</td>
      <td>Positive</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to <code>/carts/{cartId}/items/{itemId}</code> with the same <code>productId</code> and a new quantity of 3.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the cart contains exactly 1 item with the same product and quantity = 3.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the item quantity is updated.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_031</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and replaces the product while keeping the quantity unchanged</td>
      <td>Positive</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item with quantity = 2.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 2, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to <code>/carts/{cartId}/items/{itemId}</code> with a new available <code>productId</code> and quantity = 2.<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the cart contains exactly 1 item with the new product and quantity = 2.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • Retrieval of cart items verifies that the product is replaced and the quantity remains 2.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_032</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>204 No Content</code> and replaces the product while keeping the previous quantity when the quantity parameter is omitted</td>
      <td>Positive</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one item with quantity = 2.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 2, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to <code>/carts/{cartId}/items/{itemId}</code> with a new available <code>productId</code> and no quantity field (null).<br>
        4. Verify the status code is 204.<br>
        5. Send a <code>GET</code> request to <code>/carts/{cartId}/items</code> and verify the cart contains exactly 1 item with the new product and quantity remains 2.
      </td>
      <td>
        • HTTP Status: <code>204 No Content</code><br>
        • The product is successfully replaced, and the quantity remains at its previous value (2).
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_033</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when replacing an item with an invalid or non-existent productId</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add an available product with quantity = 2, obtaining the <code>itemId</code>.<br>
        3. Set <code>invalidProductId</code> to 999999.<br>
        4. Send a <code>PUT</code> request to replace the item with the invalid <code>productId</code> and quantity = 2.<br>
        5. Retrieve the response and verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"Invalid or missing productId"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing productId"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_034</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the cartId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 2, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request with invalidCartId = <code>"invalid-cart-id"</code> using the valid <code>itemId</code> with the same product and quantity.<br>
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No cart with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No cart with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_035</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when the itemId is invalid or non-existent</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 2.<br>
        3. Send a <code>PUT</code> request with an invalid <code>itemId = "invalid-item-id"</code> for the same product and quantity.<br>
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_036</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when replacing an item with an out-of-stock product</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item, and a non-available product exists in the inventory.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 2, obtaining the <code>itemId</code>.<br>
        3. Retrieve a random non-available product.<br>
        4. Send a <code>PUT</code> request to replace the item with the non-available product's ID and quantity = 2.<br>
        5. Retrieve the response and verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"The quantity requested is not available in stock"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "The quantity requested is not available in stock"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_037</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when the replacement quantity exceeds the product's current stock</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Set <code>quantityExceedingStock = currentStock + 1</code>.<br>
        4. Send a <code>PUT</code> request to replace the item with the same product and the quantity exceeding stock.<br>
        5. Retrieve the response and verify the HTTP status code is 400.<br>
        6. Verify the error response contains <code>"The quantity requested is not available in stock"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "The quantity requested is not available in stock"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_038</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when replacing an item with a negative quantity</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to replace the item with the same product and a negative quantity (e.g., -5).<br>
        4. Retrieve the response and verify the HTTP status code is 400.<br>
        5. Verify the error response contains <code>"Invalid or missing quantity"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing quantity"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_039</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>400 Bad Request</code> and validation error when replacing an item with a quantity of 0</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>A cart exists containing one valid item.</td>
      <td>
        1. Create a cart.<br>
        2. Add a product with quantity = 1, obtaining the <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to replace the item with the same product and quantity set to 0.<br>
        4. Retrieve the response and verify the HTTP status code is 400.<br>
        5. Verify the error response contains <code>"Invalid or missing quantity"</code>.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing quantity"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
    <tr>
      <td><b>TC_CART_040</b></td>
      <td>Verify that <code>PUT /carts/{cartId}/items/{itemId}</code> returns <code>404 Not Found</code> and validation error when attempting to replace an item using a different cartId than the one it belongs to</td>
      <td>Negative</td>
      <td><code>PUT /carts/:cartId/items/:itemId</code></td>
      <td>Two separate carts exist. Cart A has an item added.</td>
      <td>
        1. Create Cart A and Cart B.<br>
        2. Add a product to Cart A and retrieve its <code>itemId</code>.<br>
        3. Send a <code>PUT</code> request to replace the item in Cart B, using Cart A's <code>itemId</code> in the path, with a replacement product and quantity = 2.<br>
        4. Retrieve the response and verify status code is 404.<br>
        5. Verify the error response contains <code>"No item with id"</code>.
      </td>
      <td>
        • HTTP Status: <code>404 Not Found</code><br>
        • Response body contains an error object: <code>{"error": "No item with id"}</code>.
      </td>
      <td><b>Major</b></td>
    </tr>
  </tbody>
</table>
