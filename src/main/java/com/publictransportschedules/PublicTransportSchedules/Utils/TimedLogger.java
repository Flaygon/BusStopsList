package com.publictransportschedules.PublicTransportSchedules.Utils;

import java.time.LocalDateTime;

public class TimedLogger
{
    private LocalDateTime previousLogTime;

    public void StartLogChain()
    {
        previousLogTime = LocalDateTime.now();
    }

    public void Log(LogType type, String message)
    {
        var currentLogTime = LocalDateTime.now();

        var prefix = "";
        switch(type)
        {
            case Current ->
            {
                prefix = currentLogTime.toString() + ": ";
            }

            case PreviousAndCurrent ->
            {
                prefix = "" + ((currentLogTime.getNano() - previousLogTime.getNano()) / 1000000000.0f) + " seconds: ";
            }
        }
        System.out.println(prefix + message);

        previousLogTime = currentLogTime;
    }

    public enum LogType
    {
        Current,
        PreviousAndCurrent,
    }
}
