async function fetchData() {
    const response = await fetch('/api/nodes');
    return await response.json();
}

async function addNode(nodeName) {
    const response = await fetch('/api/nodes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: nodeName})
    });
    return response.json();
}

async function connectNodes(from, to, distance) {
    const response = await fetch(`/api/nodes/${from}/connect/${to}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({distance})
    });
    return response.json();
}
