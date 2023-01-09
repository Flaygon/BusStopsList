# BusStopsList
Lists the top 10 bus stops from bus lines in Stockholm using TrafikLab API

# Running the Server Locally (WINDOWS)
Open project in IntelliJ IDEA or similar development environment

Set project java SDK to Java 17

Go to class PublicTransportSchedulesApplication and run the project

Wait for gradle to do it's thing

Once it runs the application will now attempt to retrieve data from the TrafikLab API

Wait until the message "Data fetched successfully!" appears in the console before attempting the below 'Viewing Data' steps

PS.
EACH TIME the Server starts, it will do 2 api calls, and with the free Key in the project it will only authorize 5 calls/minute.
Please be respectful of that. There will be errors in execution if exceeding this restriction with this naive implementation.

# Viewing data
Go to any web browser (tets were performed in Chrome) and type in http://localhost:8080 after you've started the server. You should now see the bus lines displayed from top to bottom.

PS.
You can check the json data returned by going to http://localhost:8080/data
