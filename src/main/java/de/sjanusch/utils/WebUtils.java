package de.sjanusch.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WebUtils {

    public static String getTextAsString(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append("\n" + inputLine);

        in.close();

        return response.toString();
    }

    public static String[] getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        ArrayList<String> lines = new ArrayList<String>();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) 
            lines.add(inputLine);

        in.close();

        return lines.toArray(new String[lines.size()]);
    }

}
