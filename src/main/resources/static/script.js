let socket;
const connectButton = document.getElementById('connectButton');
const socketNameInput = document.getElementById('socketName');
const editor = document.getElementById('editor');
const serverAddress= '3.109.3.103';
const serverPort= 9000;


let isUpdatingFromServer = false; // Flag to avoid triggering 'input' event during updates

// Event Listener for the Connect Button
connectButton.addEventListener('click', async () => {
    const socketName = socketNameInput.value.trim();
    if (!socketName) {
        alert("Please enter a valid socket name.");
        return;
    }

    try {
        // Step 1: Make REST API call to create the socket
        const response = await fetch(`http://${serverAddress}:${serverPort}/socket/${socketName}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            console.log(`Socket ${socketName} created successfully.`);

            // Step 2: Establish WebSocket connection
            connectToSocket(socketName);
        } else {
            const error = await response.text();
            alert(`Error creating socket: ${error}`);
        }
    } catch (error) {
        console.error("Error creating socket:", error);
        alert("An error occurred while creating the socket. Please try again.");
    }
});

// Function to establish WebSocket connection
function connectToSocket(socketName) {
    // Close existing socket if open
    if (socket) {
        socket.close();
    }

    // Establish WebSocket connection
    socket = new WebSocket(`ws://${serverAddress}:${serverPort}/chat/${socketName}`);

    socket.onopen = () => {
        console.log("Connected to socket:", socketName);
        editor.disabled = false; // Enable the text editor
        alert(`Connected to socket: ${socketName}`);
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data); // Parse received data

        // Handle incoming updates
        if (data.type === 'update') {
            isUpdatingFromServer = true; // Avoid triggering input event
            editor.value = data.content; // Update editor content
            isUpdatingFromServer = false;
        }
    };

    socket.onclose = () => {
        console.log("Socket closed");
        editor.disabled = true; // Disable editor if connection is lost
    };

    socket.onerror = (error) => {
        console.error("Socket error:", error);
        alert("Error connecting to the socket. Please try again.");
    };

    // Broadcast editor changes to the server
    editor.addEventListener('input', () => {
        if (!isUpdatingFromServer && socket && socket.readyState === WebSocket.OPEN) {
            const update = {
                type: 'update',
                content: editor.value, // Send the current editor content
            };
            socket.send(JSON.stringify(update)); // Send update to server
        }
    });
}