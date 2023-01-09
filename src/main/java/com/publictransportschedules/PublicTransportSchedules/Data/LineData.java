package com.publictransportschedules.PublicTransportSchedules.Data;

import java.util.ArrayList;
import java.util.List;

public class LineData
{
    public LineData(int ID)
    {
        this.ID = ID;
    }

    public int ID;
    public List<DirectionData> Data = new ArrayList<>(64);
}
