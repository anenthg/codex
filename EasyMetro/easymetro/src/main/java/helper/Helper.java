package helper;

/**
 * Created by ashwin on 24/6/14.
 */
public class Helper {
    public static boolean isValidStation(String station, String[] stations)
    {
        boolean stationFound=false;
        for(String temp:stations)
        {
            if(station.equals(temp))
            {
                stationFound=true;
            }
        }
        return stationFound;
    }
}
