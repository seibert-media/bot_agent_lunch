package de.sjanusch.listener;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;

import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 23:47
 */
public interface LunchListenerHelper {

  List<Lunch> getLunchlist(final Weekdays day);

  String convertNames(final String from);

  String createLunchOverview(final List<Lunch> lunchList, final String actualUser);

  boolean isLunchesClosed();

  int getSignedInNumber();

  void setSignedInNumber(final int value);

  SuperlunchRequestHandler getSuperlunchRequestHandler();
}
