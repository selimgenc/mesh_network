function processData(data) {
    const nodes = data.map(d => ({id: d.id, name: d.name}));
    const connections = [];

    data.forEach(node => {
        node.connections.forEach(connection => {
            connections.push({
                source: connection.nodes[0],
                target: connection.nodes[1],
                distance: connection.distance
            });
        });
    });

    return {nodes, connections};
}
