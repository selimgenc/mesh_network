function highlightPath(nodes, connections, pathNodes) {
    // Reset all links and nodes
    nodes.forEach(node => {
        d3.select(`#node-${node.id} circle`).classed("highlight-node", false);
    });
    connections.forEach(connection => {
        d3.selectAll(".link").classed("highlight", false);
    });

    const pathNodeIds = pathNodes.map(node => node.id);

    // Highlight nodes in the path
    pathNodeIds.forEach(nodeId => {
        d3.select(`#node-${nodeId} circle`).classed("highlight-node", true);
    });

    // Highlight links in the path
    for (let i = 0; i < pathNodeIds.length - 1; i++) {
        const sourceId = pathNodeIds[i];
        const targetId = pathNodeIds[i + 1];
        d3.selectAll(".link").filter(d =>
            (d.source.id === sourceId && d.target.id === targetId) ||
            (d.source.id === targetId && d.target.id === sourceId)
        ).classed("highlight", true);
    }
}
