
Technical Challenge
===================
Mesh Network: Lightest path from Origin to Destination


Functional Requirements
-----------------------
The application aims to send Information within a mesh network from Node 'Origin' to Node 'Destination' in efficient way.

The application must meet the next requirements. As a user, I want to:
- See the list of all existing nodes
- create and delete nodes
- establish connection between two nodes and see the distance between them
- see the list of all existing node connections. The list must be sortable by
  - any link node (alphabetically)
  - distance
- know the optimal route between node origin and node destination

Assumptions
-----------
- Node connections are biderictional. The distance between two nodes is equal in both directions.
- The optimal route is the one where the sum of all distances is lowest. Note that the number of nodes visited can be used as criteria to determine the optimal route when there is more than one optimal route.
- A node can have up to 4 simultaneous connections.

Technical Requirements
----------------------
The focus should be on maintainability, stability and scalability. The performance is not the concern.


About Project and solution   
----------------------
## Running the Project
To run the project, use the following command, if you have java 17 or higher version available:
```sh
./gradlew bootRun
```
Alternatively you can run the project as a docker image as following command
```sh
./gradlew runOnContainer 
```

Testing Data:
----
To Help testing the following graph will be created for you, if you want empty graph, then disable the configuration on
```yaml
sample:
  disable: true
```
```
    (2)----(4)----(6)
    /  \   / |    / \
   /    \ /  |   /   \
 (1)----(3) (7)--(9) (11)
  | \   /    |    \  / \
  |  \ /    |      \/   \
 (5) (8)  (10)----(12) (14)
  |   /    |     /      |
  |  /     |    /      |
 (13)----(15) (17)--(19)
   \    /  |   /    /
    \  /   |  /    /
    (16)---(18)--(20)
```
For API document visit  URL: http://localhost:8080/swagger-ui/index.html
-----------------

Swagger UI provides all the actions that a user can take against the API.
One can use swagger UI to test all the use cases and get information about the supported request and response objects


User Interface: http://localhost:8080/index.html
--------------

There is a static html page to visualize the mesh as graph and do the following:
1. Show the shortest path between two selected nodes
2. Add new Node
3. Connect two nodes

Note: There is no error handling or showing notifications about background callbacks.

# Node Resources API

## Overview
This API provides endpoints for performing various operations on nodes. It supports CRUD operations, node connections, and finding the shortest path between nodes.

## Endpoints

### Get All Nodes
**GET** `/api/nodes`
- Retrieves a list of all nodes.
- **Response Code:** `200`
- **Response:** JSON array of nodes. Each Node data will come with corresponding connection data.

### Get Node by ID
**GET** `/api/nodes/{id}`
- Retrieves a specific node by its ID. Nodes are unique by their ID in the system.
- **Response Code:** `200`
- **Response:** JSON representation of the node.  Each Node data will come with corresponding connection data.

### Create Node
**POST** `/api/nodes`
- Creates a new node with the provided name. The id of the Node will be created by system automatically.
- **Request Body:** JSON object with node details.
- **Response Code:** `201`
- **Response:** JSON representation of the created node.

### Delete Node by ID
**DELETE** `/api/nodes/{id}`
- Deletes a node by its ID, including all its connections. This also mean, the counterpart of connected nodes will also loose their connection.
- **Response Code:** `204`

### Connect Nodes
**POST** `/api/nodes/{id}/connect/{targetId}`
- Connects two nodes. If they are already connected, returns a conflict.
- **Request Body:** JSON object with connection details which includes distance value.
- **Response Code:** `201`
- **Response:** JSON representation of the connection.

### Get Connection Between Nodes
**GET** `/api/nodes/{id}/connection/{targetId}`
- Retrieves the connection details between two nodes.
- **Response Code:** `200`
- **Response:** JSON representation of the connection.

### Disconnect Nodes
**DELETE** `/api/nodes/{id}/disconnect/{targetId}`
- Disconnects two nodes.
- **Response Code:** `204`

### Find Shortest Path
**GET** `/api/nodes/{id}/shortestPath/{targetId}`
- Finds the shortest path between two nodes. When there is multiple path with same distance value, the route with less node will be returned.
- **Response Code:** `200`
- **Response:** JSON representation of the shortest path. List of Nodes and total distance

### Get All Connections
**GET** `/api/nodes/connections`
- Retrieves all connections between all nodes, sorted by specified criteria and sort type.
- **Response Code:** `200`
- **Response:** JSON array of connections. The result will not contain dublicate connection.

## Technologies
- **Spring Boot**: Framework used to create the RESTful API.
- **Java**: Programming language used for implementation.
- **Gradle**: Build tool.
- **D3.js**: Visualize the graph# mesh_network
