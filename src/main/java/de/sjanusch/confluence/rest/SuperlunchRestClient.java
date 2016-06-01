package de.sjanusch.confluence.rest;

import java.util.List;

import de.sjanusch.model.superlunch.Lunch;

/**
 * Created by Sandro Janusch Date: 13.05.16 Time: 19:39
 */
public interface SuperlunchRestClient {

  List<Lunch> superlunchRestApiGet();

  boolean superlunchRestApiSignIn(final String id, final String username);

  boolean superlunchRestApiSignOut(final String id, final String username);
}
