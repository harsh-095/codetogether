let socket;
const connectButton = document.getElementById('connectButton');
const socketNameInput = document.getElementById('socketName');
//const serverAddress = '3.109.3.103';
const serverAddress = 'localhost';
const serverPort = 9000;


// Initialize Konva
const stage = new Konva.Stage({
    container: 'container',
    width: 800,
    height: 600,
});

const layer = new Konva.Layer();
stage.add(layer);

let isDrawing = false;
let lastLine;
let drawColor;

// Default settings
let color = document.getElementById('color').value;
let thickness = parseFloat(document.getElementById('thickness').value);
let opacity = parseFloat(document.getElementById('opacity').value);
let isEraser = false; // Tracks the current tool mode (false = pencil, true = eraser)

// DOM Elements
const colorInput = document.getElementById('color');
const thicknessInput = document.getElementById('thickness');
const opacityInput = document.getElementById('opacity');
const toolButton = document.getElementById('eraser');
const resetButton = document.getElementById('reset');

// Update color, thickness, and opacity dynamically
colorInput.addEventListener('input', (e) => {
    color = e.target.value;
    if (!isEraser) {
        // Update the drawing color only if it's in pencil mode
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
});

// Drawing logic
stage.on('mousedown touchstart', () => {
    isDrawing = true;
    const pos = stage.getPointerPosition();
    drawColor = color;
    lastLine = new Konva.Line({
        stroke: isEraser ? 'white' : drawColor, // Use the current color or white for eraser
        strokeWidth: thickness,
        opacity: isEraser ? 1 : opacity,
        globalCompositeOperation: isEraser ? 'destination-out' : 'source-over',
        points: [pos.x, pos.y],
    });
    layer.add(lastLine);
    console.log("mousedown");
});

stage.on('mousemove touchmove', () => {
    if (!isDrawing) return;

    const pos = stage.getPointerPosition();
    const newPoints = lastLine.points().concat([pos.x, pos.y]);
    lastLine.points(newPoints);

    // Send the drawing data to the server (commented for now)
    // sendDrawing(JSON.stringify(lastLine.toObject()));

    layer.batchDraw();
//    console.log("mousemove " +JSON.stringify(lastLine.toObject()));
});

stage.on('mouseup touchend', () => {
    isDrawing = false;
        const pos = stage.getPointerPosition();
        // Do not remove below three lines
        drawColor = color;
        lastLine = new Konva.Line({});
        layer.add(lastLine); // So that last added line color doesnot change
    console.log("mouseup");
    const drawingsAsJSON = stage.toJSON(); // Get the canvas as a base64 image string
    console.log(drawingsAsJSON);
});

// WebSocket setup (commented for now)
/*
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
    stompClient.subscribe('/topic/updates', (message) => {
        const data = JSON.parse(message.body);

        // Add received shape to canvas
        const receivedShape = Konva.Node.create(data.shapeData, 'container');
        layer.add(receivedShape);
        layer.batchDraw();
    });
});

function sendDrawing(shapeData) {
    stompClient.send('/app/draw', {}, JSON.stringify({ shapeData }));
}
*/
