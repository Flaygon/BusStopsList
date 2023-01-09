package com.publictransportschedules.PublicTransportSchedules.Managers;

import com.publictransportschedules.PublicTransportSchedules.Data.DirectionData;
import com.publictransportschedules.PublicTransportSchedules.Data.LineData;
import com.publictransportschedules.PublicTransportSchedules.Utils.TimedLogger;
import org.apache.tomcat.util.json.JSONParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class TrafikLabAPIManager
{
    private final String URLPath = "https://api.sl.se/api2/LineData.json?model={model}&key={apikey}";
    private final String APIKey = "03754d95f9f44cb89bdc19f05504f304";

    // Model Key Constants
    private final String JourneyModelKey = "jour";
    private final String LineModelKey = "line";

    // Common Data Constants
    private final String DataResponseKey = "ResponseData";
    private final String DataResultKey = "Result";

    // Shared Data Constants
    private final String DataLineKey = "LineNumber";

    // Journey Specific Data Constants
    private final String DataDirectionKey = "DirectionCode";

    // Line Specific Data Constants
    private final String DataLineTypeKey = "DefaultTransportModeCode";
    private final String LineTypeFilterKey = "BUS";

    private final List<LineData> lineData;
    private boolean isDone;

    private final TimedLogger logger;

    public boolean IsDone() { return isDone; }

    public TrafikLabAPIManager()
    {
        lineData = new ArrayList<>(256);
        logger = new TimedLogger();
    }

    public List<LineData> GetTop10Lines()
    {
        var top10List = new ArrayList<LineData>(10);
        for(int iData = 0; iData < 10; ++iData)
        {
            top10List.add(lineData.get(iData));
        }
        return top10List;
    }

    public void FetchFilterAndSortBusData()
    {
        System.out.println("Fetching data from TrafikLab ...");

        FetchAndSortJourneyData();
        FetchLineDataAndFilterJourneyDataWithTypeKey();

        System.out.println("Data fetched successfully!");
        isDone = true;
    }

    private void FetchAndSortJourneyData()
    {
        try
        {
            logger.StartLogChain();
            logger.Log(TimedLogger.LogType.Current, "Downloading bus line data...");

            var connection = OpenConnection(JourneyModelKey);

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Downloading data completed!");

            CheckForErrors(connection);

            logger.Log(TimedLogger.LogType.Current, "Parsing downloaded data...");

            InputStream inputStream = connection.getInputStream();
            var parsedData = (LinkedHashMap<String, Object>)new JSONParser(inputStream).parse();
            var results = (ArrayList<LinkedHashMap<String, String>>)((LinkedHashMap<String, Object>)parsedData.get(DataResponseKey)).get(DataResultKey);

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Parsing data completed!");
            logger.Log(TimedLogger.LogType.Current, "Transforming parsed data...");

            LineData currentLineData = null;
            DirectionData currentDirectionData = null;
            for (var data: results)
            {
                int lineID = Integer.parseInt(data.get(DataLineKey));
                int directionID = Integer.parseInt(data.get(DataDirectionKey));

                if(currentLineData == null || lineID != currentLineData.ID)
                {
                    currentDirectionData = new DirectionData(directionID);
                    currentLineData = new LineData(lineID);
                    currentLineData.Data.add(currentDirectionData);
                    lineData.add(currentLineData);
                }
                else if(directionID != currentDirectionData.ID)
                {
                    currentDirectionData = new DirectionData(directionID);
                    currentLineData.Data.add(currentDirectionData);
                }

                ++currentDirectionData.NumStops;
                currentLineData.Data.sort(Comparator.comparingInt(element -> element.NumStops));
            }

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Transforming data completed!");
            logger.Log(TimedLogger.LogType.Current, "Sorting transformed data...");

            // Sorting by ints in reverse
            // Trying to use reverse in the comparator was giving me Type issues, so reversed ints instead
            lineData.sort(Comparator.comparingInt(element -> -element.Data.get(element.Data.size() - 1).NumStops));

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Sorting data completed!");

            connection.disconnect();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private void FetchLineDataAndFilterJourneyDataWithTypeKey()
    {
        try
        {
            logger.StartLogChain();
            logger.Log(TimedLogger.LogType.Current, "Downloading bus line data...");

            var connection = OpenConnection(LineModelKey);

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Downloading data completed!");

            CheckForErrors(connection);

            logger.Log(TimedLogger.LogType.Current, "Parsing downloaded data...");

            InputStream inputStream = connection.getInputStream();
            var parsedData = (LinkedHashMap<String, Object>)new JSONParser(inputStream).parse();
            var results = (ArrayList<LinkedHashMap<String, String>>)((LinkedHashMap<String, Object>)parsedData.get(DataResponseKey)).get(DataResultKey);

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Parsing data completed!");
            logger.Log(TimedLogger.LogType.Current, "Filtering Journey Data with parsed data...");

            for(var data: results)
            {
                if(data.get(DataLineTypeKey).contentEquals(LineTypeFilterKey))
                    continue;

                int IDToRemove = Integer.parseInt(data.get(DataLineKey));
                for(int iData = lineData.size() - 1; iData >= 0; --iData)
                {
                    var currentData = lineData.get(iData);
                    if(currentData.ID == IDToRemove)
                    {
                        lineData.remove(iData);
                        break;
                    }
                }
            }

            logger.Log(TimedLogger.LogType.PreviousAndCurrent, "Filtering data completed!");

            connection.disconnect();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private HttpURLConnection OpenConnection(String modelKey) throws Exception
    {
        var replacedURLPath = URLPath;
        replacedURLPath = replacedURLPath.replace("{model}", modelKey);
        replacedURLPath = replacedURLPath.replace("{apikey}", APIKey);

        URL url = new URL(replacedURLPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Language", "*");
        //connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.connect();

        return connection;
    }

    private void CheckForErrors(HttpURLConnection openConnection) throws Exception
    {
        System.out.println("Checking connection status ...");

        var responseCode = openConnection.getResponseCode();
        var responseMessage = openConnection.getResponseMessage();
        System.out.println(responseCode);
        System.out.println(responseMessage);

        if(responseCode >= 300)
            throw new Exception("Connection failed: '" + responseCode + "': '" + responseMessage + "'");
        System.out.println("Connection opened successfully!");
    }
}
