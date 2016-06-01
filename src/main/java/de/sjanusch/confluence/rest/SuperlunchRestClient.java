package de.sjanusch.confluence.rest;

import de.sjanusch.model.superlunch.Lunch;

import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 19:39
 */
public interface SuperlunchRestClient {

    List<Lunch> superlunchRestApiGet();

    boolean superlunchRestApiSignIn(final String id, final String username);

    boolean superlunchRestApiSignOut(final String id, final String username);
}
