package com.publictransportschedules.PublicTransportSchedules.Controllers;

import com.publictransportschedules.PublicTransportSchedules.Managers.TrafikLabAPIManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController
{
    private final TrafikLabAPIManager trafikLabAPIManager;

    public MainController()
    {
        trafikLabAPIManager = new TrafikLabAPIManager();
        trafikLabAPIManager.FetchFilterAndSortBusData();
    }

    // Mappings

    @GetMapping(value = "/")
    public String LandingPage()
    {
        return "index.html";
    }

    @CrossOrigin
    @GetMapping(value = "/data")
    @ResponseBody
    public String Data()
    {
        if(!trafikLabAPIManager.IsDone())
            return "[]";

        String returnJSON = "[";
        var top10Lines = trafikLabAPIManager.GetTop10Lines();
        for(int iLine = 0; iLine < top10Lines.size(); ++iLine)
        {
            var currentLineData = top10Lines.get(iLine);

            returnJSON += "{";

            returnJSON += "\"name\":\"" + currentLineData.ID + "\",";
            returnJSON += "\"stops\":\"" + currentLineData.Data.get(currentLineData.Data.size() - 1).NumStops + "\"";

            returnJSON += "}";

            if(iLine < top10Lines.size() - 1)
                returnJSON += ",";
        }
        returnJSON += "]";
        return returnJSON;
    }
}
