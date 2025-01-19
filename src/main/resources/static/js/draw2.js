let socket;
const connectButton = document.getElementById('connectButton');
const socketNameInput = document.getElementById('socketName');
const serverAddress = '3.109.3.103';
//const serverAddress = 'localhost';
const serverPort = 9000;

//Chunk map and chunk size
const chunkMap= {};
const chunkSize=5;

// Flag to check if this is the primary session
let isPrimarySession = false;

// Initialize Konva
const stage = new Konva.Stage({
    container: 'container',
    width: 800,
    height: 600,
});

var layer = new Konva.Layer();
stage.add(layer);

let isDrawing = false;
let lastLine;
let drawColor;

// Default settings
let color = document.getElementById('color').value;
let thickness = parseFloat(document.getElementById('thickness').value);
let opacity = parseFloat(document.getElementById('opacity').value);
let isEraser = false;

// DOM Elements
const colorInput = document.getElementById('color');
const thicknessInput = document.getElementById('thickness');
const opacityInput = document.getElementById('opacity');
const toolButton = document.getElementById('eraser');
const resetButton = document.getElementById('reset');

//Functions
    function sendCanvasInChunks(message, type) {
        const chunks = [];
        const totalChunks = Math.ceil(JSON.stringify(message).length / chunkSize);
        const id = Math.floor(Math.random() * 100000) + 1;
        for (let i = 0; i < totalChunks; i++) {
            const chunk = JSON.stringify(message).slice(i * chunkSize, (i + 1) * chunkSize);
            chunks.push({
                id: id,
                type: type,
                body: chunk,
                isLastMessage: (i === (totalChunks - 1)),
                totalChunks: totalChunks,
                chunkId: i,
            });
        }

        // Send chunks to all connected sessions
        chunks.forEach(chunk => {
            socket.send(JSON.stringify(chunk));
        });
    }

    function handleCanvasChunk(data) {
        if(!chunkMap[data.id]){
        chunkMap[data.id]="";
        }
        chunkMap[data.id]+=data.body;
        if(data.isLastMessage){
            const msg = chunkMap[data.id];
            delete chunkMap[data.id];
            return msg;
        }
    }

// Update color, thickness, and opacity dynamically
colorInput.addEventListener('input', (e) => {
    color = e.target.value;
    if (!isEraser) {
        lastLine.stroke(color);
    }
});

thicknessInput.addEventListener('input', (e) => {
    thickness = parseFloat(e.target.value);
});

opacityInput.addEventListener('input', (e) => {
    opacity = parseFloat(e.target.value);
});

// Toggle tool (Pencil <-> Eraser)
toolButton.addEventListener('click', () => {
    isEraser = !isEraser;

    if (isEraser) {
        toolButton.textContent = 'Pencil';
    } else {
        toolButton.textContent = 'Eraser';
    }
});

// Reset canvas
resetButton.addEventListener('click', () => {
    layer.destroyChildren();
    layer.batchDraw();
    // Send reset event to all connected sockets
    if (isPrimarySession && socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ type: 'reset' }));
    }
});

// Drawing logic
stage.on('mousedown touchstart', () => {
    isDrawing = true;
    const pos = stage.getPointerPosition();
    drawColor = color;
    lastLine = new Konva.Line({
        stroke: isEraser ? 'white' : drawColor,
        strokeWidth: thickness,
        opacity: isEraser ? 1 : opacity,
        globalCompositeOperation: isEraser ? 'destination-out' : 'source-over',
        points: [pos.x, pos.y],
    });
    layer.add(lastLine);
});

stage.on('mousemove touchmove', () => {
    if (!isDrawing) return;

    const pos = stage.getPointerPosition();
    const newPoints = lastLine.points().concat([pos.x, pos.y]);
    lastLine.points(newPoints);
    layer.batchDraw();
});

stage.on('mouseup touchend', () => {
    isDrawing = false;

    if (socket && socket.readyState === WebSocket.OPEN) {
        if (!isPrimarySession) {
            // Send last line to primary session
            const shapeData = lastLine.toObject();
//            socket.send(JSON.stringify({ type: 'draw', shapeData }));
            sendCanvasInChunks(shapeData,'draw');
        } else {
            // Primary session broadcasts entire canvas
            const fullCanvas = stage.toJSON();
//            socket.send(JSON.stringify({ type: 'canvas', fullCanvas }));
            sendCanvasInChunks(fullCanvas,'canvas');
            console.log("Data size: " + new TextEncoder().encode(JSON.stringify({ type: 'canvas', fullCanvas })).length + " bytes");
        }
    }
});

// WebSocket logic
connectButton.addEventListener('click', async () => {
    const socketName = socketNameInput.value.trim();
    if (!socketName) {
        alert('Please enter a valid socket name.');
        return;
    }

    try {
        const response = await fetch(`http://${serverAddress}:${serverPort}/drawsocket/${socketName}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });

        if (response.ok) {
            console.log(`Socket ${socketName} created successfully.`);
            connectToSocket(socketName);
        } else {
            const error = await response.text();
            alert(`Error creating socket: ${error}`);
        }
    } catch (error) {
        console.error('Error creating socket:', error);
        alert('An error occurred while creating the socket.');
    }
});

function connectToSocket(socketName) {
    if (socket) {
        socket.close();
    }

    socket = new WebSocket(`ws://${serverAddress}:${serverPort}/draw/${socketName}`);

    socket.onopen = () => {
        console.log(`Connected to socket: ${socketName}`);
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log(data);
        if (data.type === 'draw' && isPrimarySession) {
            const body = handleCanvasChunk(data);
            if(body){
                const newLine = Konva.Node.create(body);
                const targetLayer = stage.findOne('Layer') || new Konva.Layer();
                targetLayer.add(newLine);
                if (!stage.hasChildren()) {
                    stage.add(targetLayer);
                }
                stage.batchDraw();
            }
        } else if (data.type === 'canvas') {
            const body = handleCanvasChunk(data);
            if(body){
                console.log("Received_Body=",body);
                stage.destroyChildren();
                const tempContainer = document.createElement('div');
                const newNodes = Konva.Node.create(JSON.parse(body),tempContainer);
                newNodes.children.forEach((child) => {
                    stage.add(child);
                });
                stage.batchDraw();
                layer =  stage.findOne('Layer');
                console.log("Layer=",layer);
            }
        } else if (data.type === 'reset') {
            layer.destroyChildren();
            layer.batchDraw();
        } else if (data.type === 'SetPrimary') {
            isPrimarySession = true;
        }
    };

    socket.onclose = () => {
        console.log('Socket closed');
    };

    socket.onerror = (error) => {
        console.error('Socket error:', error);
        alert('Error connecting to the socket.');
    };


}
