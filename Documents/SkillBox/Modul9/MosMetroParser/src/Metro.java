import com.google.gson.annotations.SerializedName;

import java.util.*;

public class Metro {

    @SerializedName("stations")
    private Map<String, List<String>> stationsList;

    @SerializedName("connections")
    List<Station[]> connectionList;

    @SerializedName("lines")
    List<Line> lineList;

    public Metro(List<Line> lineList, List<Station[]> connectionList, Map<String, List<String>> stationsList) {
        this.lineList = lineList;
        this.connectionList = connectionList;
        this.stationsList = stationsList;
    }
}