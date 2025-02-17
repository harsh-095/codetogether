let socket;
 const serverAddress = '3.109.3.103';
//const serverAddress = 'localhost';
const serverPort = 9000;
var logContainer = document.getElementById("log-container");

socket = new WebSocket(`ws://${serverAddress}:${serverPort}/logsocket`);

socket.onopen = () => {
    console.log(`Connected to Logger socket`);
};

// Event handler when a log message is received
function getLogMap(logMessage){
const logMessageCleaned = logMessage.substring(1, logMessage.length - 1);

// Create an object to store the key-value pairs
const logObj = {};

// Split the cleaned string by commas to separate each key-value pair
const pairs = logMessageCleaned.split(", ");

// Loop through each key-value pair
pairs.forEach(pair => {
    // Split by the first occurrence of "': '"
    const [key, value] = pair.split("':'");

    // Remove the surrounding single quotes from the key and value
    const cleanedKey = key.substring(1); // Remove the starting single quote
    const cleanedValue = value.substring(0, value.length - 1); // Remove the ending single quote

    // Assign the cleaned key-value pair to the object
    logObj[cleanedKey] = cleanedValue;
});

    // Print the resulting map
return logObj;
}
socket.onmessage = function(event) {
    try {
                let logMessage = event.data.trim();
                console.log("logMessage=",logMessage);
                let logMap = getLogMap(logMessage)
                console.log("logMap =",logMap);
                if (logMessage.startsWith("{") && logMessage.endsWith("}")) {
//                    logMessage = JSON.parse(logMessage); // Convert string to JSON

                    let row = document.createElement("tr");

                    // Assign class based on log level for styling
                    row.className = logMap.level.toLowerCase();

                    row.innerHTML = `
                        <td>${logMap.timestamp}</td>
                        <td>${logMap.level}</td>
                        <td>
                            <div>
                                Message: ${logMap.message} <br/>
                                Logger: ${logMap.logger} <br/>
                                Line: ${logMap.line}
                            </div>
                        </td>
                    `;

                    logContainer.appendChild(row);
                    logContainer.scrollTop = logContainer.scrollHeight; // Auto-scroll
                } else {
                    console.warn("Received malformed JSON log:", logMessage);
                }
            } catch (error) {
        console.error(error);
    }
};

// Event handler for error cases
socket.onerror = function(event) {
    console.log("Error occurred in WebSocket connection.");
};
