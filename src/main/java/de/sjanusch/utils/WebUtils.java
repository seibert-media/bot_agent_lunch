package de.sjanusch.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WebUtils {

  public static String getTextAsString(final String url) throws Exception {
    final URL website = new URL(url);
    final URLConnection connection = website.openConnection();
    final BufferedReader in = new BufferedReader(
        new InputStreamReader(
            connection.getInputStream()));

    final StringBuilder response = new StringBuilder();
    String inputLine;

    while ((inputLine = in.readLine()) != null)
      response.append("\n" + inputLine);

    in.close();

    return response.toString();
  }

  public static String[] getText(final String url) throws Exception {
    final URL website = new URL(url);
    final URLConnection connection = website.openConnection();
    final BufferedReader in = new BufferedReader(
        new InputStreamReader(
            connection.getInputStream()));

    final ArrayList<String> lines = new ArrayList<>();
    String inputLine;

    while ((inputLine = in.readLine()) != null)
      lines.add(inputLine);

    in.close();

    return lines.toArray(new String[lines.size()]);
  }

}
