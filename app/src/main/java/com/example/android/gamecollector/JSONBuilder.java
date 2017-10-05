package com.example.android.gamecollector;

import java.util.List;

import javax.json.Json;

/**
 * Created by shalom on 2017-10-04.
 * Takes inputted text from ParseCSV.java and builds a JSON file
 */

public class JSONBuilder {

    private String buildJSONFile() {

        String json = Json.createObjectBuilder()

        for(List<String> line : ParseCSV.parseFiles()) {
            String console = line.get(0);
            String gameTitle = line.get(1);
            String licensee = line.get(2);
            String released = line.get(3);


        }
        return json;
    }
}
