fetchData().then(data => {
    let {nodes, connections} = processData(data);

    const svg = d3.select("svg"),
        width = +svg.attr("width"),
        height = +svg.attr("height");

    const color = d3.scaleOrdinal(d3.schemeCategory10);

    const simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(d => d.id).distance(d => d.distance))
        .force("charge", d3.forceManyBody().strength(-400))
        .force("center", d3.forceCenter(width / 2, height / 2));

    const linkGroup = svg.append("g").attr("class", "links");
    const nodeGroup = svg.append("g").attr("class", "nodes");

    function updateGraph() {
        const link = linkGroup.selectAll("g")
            .data(connections, d => `${d.source.id}-${d.target.id}`);

        const linkEnter = link.enter().append("g").attr("class", "link-group");

        linkEnter.append("line").attr("class", "link");
        linkEnter.append("text")
            .attr("class", "link-label")
            .attr("dy", -5)
            .attr("text-anchor", "middle")
            .text(d => d.distance);

        link.exit().remove();

        const node = nodeGroup.selectAll("g")
            .data(nodes, d => d.id);

        const nodeEnter = node.enter().append("g").attr("id", d => `node-${d.id}`);

        nodeEnter.append("circle")
            .attr("class", "node")
            .attr("r", 10) // Adjust the radius to make nodes bigger
            .attr("fill", d => color(d.group))
            .call(d3.drag()
                .on("start", dragstarted)
                .on("drag", dragged)
                .on("end", dragended));

        nodeEnter.append("text")
            .attr("class", "label")
            .attr("x", 12) // Adjust x position to accommodate bigger node size
            .attr("y", 4)  // Adjust y position to center text vertically
            .text(d => `${d.id}: ${d.name}`);

        node.exit().remove();

        simulation.nodes(nodes).on("tick", ticked);
        simulation.force("link").links(connections);
        simulation.alpha(1).restart();
    }

    function ticked() {
        linkGroup.selectAll("line")
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        linkGroup.selectAll("text")
            .attr("x", d => (d.source.x + d.target.x) / 2)
            .attr("y", d => (d.source.y + d.target.y) / 2);

        nodeGroup.selectAll("g").attr("transform", d => `translate(${d.x},${d.y})`);
    }

    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }

    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }

    // Initial graph rendering
    updateGraph();

    // Handle form submission for shortest path
    document.getElementById('pathForm').addEventListener('submit', async function (event) {
        event.preventDefault();
        const from = document.getElementById('from').value;
        const to = document.getElementById('to').value;
        const response = await fetch(`/api/nodes/${from}/shortestPath/${to}`);
        const pathData = await response.json();
        highlightPath(nodes, connections, pathData.nodes);
        document.getElementById('totalDistance').textContent = `Total Distance: ${pathData.totalDistance}`;
        event.target.reset(); // Clear the form
    });

    // Handle form submission for adding a new node
    document.getElementById('addNodeForm').addEventListener('submit', async function (event) {
        event.preventDefault();
        const nodeName = document.getElementById('nodeName').value;
        const newNode = await addNode(nodeName);
        newNode.x = width / 2; // Set initial x position
        newNode.y = height / 2; // Set initial y position
        nodes.push({id: newNode.id, name: newNode.name, x: newNode.x, y: newNode.y});
        updateGraph();
        event.target.reset(); // Clear the form
    });
// Handle form submission for connecting nodes
    document.getElementById('connectNodes').addEventListener('submit', async function (event) {
        event.preventDefault();
        const from = document.getElementById('fromConnect').value;
        const to = document.getElementById('toConnect').value;
        const distance = document.getElementById('distance').value;

        try {
            const newConnection = await connectNodes(from, to, distance);

            // Ensure the connection was successful and valid
            if (newConnection && newConnection.distance) {
                const sourceNode = nodes.find(n => n.id === parseInt(from));
                const targetNode = nodes.find(n => n.id === parseInt(to));

                // Ensure sourceNode and targetNode exist
                if (sourceNode && targetNode) {
                    connections.push({
                        source: sourceNode,
                        target: targetNode,
                        distance: newConnection.distance
                    });
                    updateGraph();
                } else {
                    console.error("Source or target node not found");
                }
            } else {
                console.error("Connection failed or invalid distance");
            }
        } catch (error) {
            console.error("Error connecting nodes:", error);
        }
        event.target.reset(); // Clear the form
    });


});
