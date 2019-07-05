import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static final Gson GSON= new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        List<Line> lineList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Map<String, List<String>> stationsPerLine = new TreeMap<>();
        List<Station[]> connectionList = new ArrayList<>();

        String htmlFile = parseFile("data/code.html");

        Document doc = Jsoup.parse(htmlFile);
        Element table = doc.select("table").get(3);
        List<Element> rows = table.select("tr");

        rows.stream().skip(1).forEach((elem) -> {

            Elements tds = elem.select("td");

            String lineId = tds.get(0).select("span[class=sortkey]").first().text();
            String lineName = tds.get(0).select("span").attr("title");
            String stationName = tds.get(1).select("a").attr("title");
            stationName = stationName.replaceAll("\\([^()]*\\)", "").trim();

            if (lineId != null && !idList.contains(lineId)) {
                idList.add(lineId);
                lineList.add(new Line(lineId, lineName));
                stationsPerLine.put(lineId, new ArrayList<>());
                stationsPerLine.get(lineId).add(stationName);
            }
            else{
                if(lineId != null){
                    stationsPerLine.get(lineId).add(stationName);
                }
            }

            try{
                String lineIdConnection = tds.get(3).select("span[class=sortkey]").text();
                String[] lineConnection = lineIdConnection.split(" ");
                String[] stationConnection = new String[lineConnection.length];
                Station[] stations = new Station[lineConnection.length + 1];
                stations[0] = new Station(lineId, stationName);
                for (int i = 0; i < stationConnection.length; i++){
                    stationConnection[i] = tds.get(3).select("a").get(i).attr("title");
                    stations[i+1] = new Station(lineConnection[i], stationConnection[i]);
                }
                connectionList.add(stations);
                //System.out.println(Arrays.toString(lineConnection) + "  " + Arrays.toString(stationConnection));
            }
            catch (Exception e){
                return;
            }
        });

        Metro metro = new Metro(lineList, connectionList, stationsPerLine);
        try {
            try (FileWriter file = new FileWriter("data/map.json")) {
                file.write(GSON.toJson(metro));
                file.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //------------Parsing JSON------------
        JSONParser parser = new JSONParser();
        System.out.println("\nКоличество станций на каждой линии (ч.3 задания 9.14)\n");
        try {
            JSONObject jsonData = (JSONObject) parser.parse(parseFile("data/map.json"));
            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            stationsObject.keySet().forEach(lineNumberObject -> {
                String lineId = (String) lineNumberObject;
                String lineName = metro.lineList.stream().filter(e -> e.getId().equals(lineId)).findFirst().get().getNameLine();
                JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
                System.out.println("line " + lineId + " : " + lineName +  " -> " + stationsArray.size() + " stations");
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String parseFile(String path){
        StringBuilder sb = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> sb.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}