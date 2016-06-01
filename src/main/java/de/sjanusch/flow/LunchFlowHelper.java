package de.sjanusch.flow;

/**
 * Created by Sandro Janusch
 * Date: 23.05.16
 * Time: 20:20
 */
public class LunchFlowHelper {

    public String extractId(final String incomeMessage) {
        String[] strings = incomeMessage.split(" ");
        for (String s : strings) {
            try {
                int i = Integer.parseInt(s);
                return String.valueOf(i);
            } catch (Exception e) {

            }
        }
        return null;
    }
}
