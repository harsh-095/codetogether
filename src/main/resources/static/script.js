let socket;
const connectButton = document.getElementById('connectButton');
const socketNameInput = document.getElementById('socketName');
const serverAddress = '3.109.3.103';
const serverPort = 9000;
//import Quill from "quill";
const Delta = Quill.import("delta");

const quill = new Quill('#editor', {
    theme: 'snow',
    placeholder: 'Start typing...',
    modules: {
        toolbar: true
    }
});

// Keep track of whether the session is primary
let isPrimarySession = false;

// Event Listener for the Connect Button
connectButton.addEventListener('click', async () => {
    const socketName = socketNameInput.value.trim();
    if (!socketName) {
        alert('Please enter a valid socket name.');
        return;
    }

    try {
        // Create socket via REST API
        const response = await fetch(`http://${serverAddress}:${serverPort}/socket/${socketName}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
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

// Establish WebSocket connection
function connectToSocket(socketName) {
    if (socket) {
        socket.close();
    }

    socket = new WebSocket(`ws://${serverAddress}:${serverPort}/chat/${socketName}`);

    socket.onopen = () => {
        console.log(`Connected to socket: ${socketName}`);
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log(`Received= `+JSON.stringify(event))
        console.log(`Data= `+JSON.stringify(data))
        if (data.type === 'update') {
            const delta= new Delta(data.delta);
            if(isPrimarySession){
            quill.updateContents(delta);
            }
            else{
            const myDelta = quill.getContents();
             console.log(`myDelta= `+JSON.stringify(myDelta));
            const changeDelta = myDelta.diff(delta);
            console.log(`Change Delta = `+JSON.stringify(changeDelta));
            quill.updateContents(changeDelta);
            }
        } else if (data.type === 'SetPrimary') {
            // Assign this session as primary if notified
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

    // Listen for text changes and broadcast deltas
    quill.on('text-change', (delta, oldDelta, source) => {
          if (source == 'api' && !isPrimarySession) {
            return;
          }
        if (socket && socket.readyState === WebSocket.OPEN) {
            const update = {
                type: 'update',
                delta: isPrimarySession?quill.getContents():delta,
            };
            socket.send(JSON.stringify(update));
console.log(`SendingUpdate= ${JSON.stringify(update)}`);
        }
    });

}
