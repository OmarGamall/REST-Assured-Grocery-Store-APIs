# Authentication & API Clients Test Cases Documentation

This document contains the test cases for the **Authentication and API Clients** module of the Simple Grocery Store API, covering both happy path client registration and validation/conflict scenarios.

## Auth Test Cases Table

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
      <td><b>TC_AUTH_001</b></td>
      <td>Verify that <code>POST /api-clients</code> returns <code>201 Created</code> and access token when registering a new API client with valid name and unique email</td>
      <td>Positive</td>
      <td><code>POST /api-clients</code></td>
      <td>None.</td>
      <td>
        1. Generate a random client name and a unique email address using Faker.<br>
        2. Send a <code>POST</code> request to <code>/api-clients</code> with <code>clientName</code> and <code>clientEmail</code> in JSON format.<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code.<br>
        5. Verify that the response body contains a non-null <code>accessToken</code>.
      </td>
      <td>
        • HTTP Status: <code>201 Created</code><br>
        • Response body contains a JSON object with <code>accessToken</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_AUTH_002</b></td>
      <td>Verify that <code>POST /api-clients</code> returns <code>400 Bad Request</code> and validation error when <code>clientEmail</code> is missing</td>
      <td>Negative</td>
      <td><code>POST /api-clients</code><br>Body Param: <code>clientEmail</code></td>
      <td>None.</td>
      <td>
        1. Generate a random client name and set the email address to null.<br>
        2. Send a <code>POST</code> request to <code>/api-clients</code> with <code>clientName</code> and without <code>clientEmail</code> (or null).<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code.<br>
        5. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "missing client email"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_AUTH_003</b></td>
      <td>Verify that <code>POST /api-clients</code> returns <code>400 Bad Request</code> and validation error when <code>clientName</code> is missing</td>
      <td>Negative</td>
      <td><code>POST /api-clients</code><br>Body Param: <code>clientName</code></td>
      <td>None.</td>
      <td>
        1. Generate a valid unique email address and set the client name to null.<br>
        2. Send a <code>POST</code> request to <code>/api-clients</code> with <code>clientEmail</code> and without <code>clientName</code> (or null).<br>
        3. Retrieve the response.<br>
        4. Verify the HTTP status code.<br>
        5. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "missing client name"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_AUTH_004</b></td>
      <td>Verify that <code>POST /api-clients</code> returns <code>400 Bad Request</code> and validation error when <code>clientEmail</code> has an invalid email format</td>
      <td>Negative</td>
      <td><code>POST /api-clients</code><br>Body Param: <code>clientEmail</code></td>
      <td>None.</td>
      <td>
        1. Generate a random client name.<br>
        2. Set the email address to an invalid format (e.g., <code>"invalid-email-format"</code>).<br>
        3. Send a <code>POST</code> request to <code>/api-clients</code> with <code>clientName</code> and the invalid <code>clientEmail</code>.<br>
        4. Retrieve the response.<br>
        5. Verify the HTTP status code.<br>
        6. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>400 Bad Request</code><br>
        • Response body contains an error object: <code>{"error": "Invalid or missing client email"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
    <tr>
      <td><b>TC_AUTH_005</b></td>
      <td>Verify that <code>POST /api-clients</code> returns <code>409 Conflict</code> and validation error when registering a client with an email address that is already registered</td>
      <td>Negative</td>
      <td><code>POST /api-clients</code></td>
      <td>An API client has already been registered with a specific email.</td>
      <td>
        1. Generate a random client name and a unique email address.<br>
        2. Register the first client with this email to establish the pre-condition.<br>
        3. Generate another random client name.<br>
        4. Attempt to register the second client using the exact same email address.<br>
        5. Retrieve the response.<br>
        6. Verify the HTTP status code.<br>
        7. Verify the error message details in the response body.
      </td>
      <td>
        • HTTP Status: <code>409 Conflict</code><br>
        • Response body contains an error object: <code>{"error": "API client already registered. Try a different email"}</code>.
      </td>
      <td><b>Critical</b></td>
    </tr>
  </tbody>
</table>
