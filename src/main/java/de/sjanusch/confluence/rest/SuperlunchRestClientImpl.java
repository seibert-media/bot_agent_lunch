package de.sjanusch.confluence.rest;

import com.google.inject.Inject;
import de.sjanusch.configuration.LunchConfiguration;
import de.sjanusch.model.superlunch.Lunch;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SuperlunchRestClientImpl implements SuperlunchRestClient {

  public static final Logger logger = LoggerFactory.getLogger(SuperlunchRestClientImpl.class);

  private final LunchConfiguration lunchConfiguration;

  @Inject
  public SuperlunchRestClientImpl(final LunchConfiguration lunchConfiguration) {
    this.lunchConfiguration = lunchConfiguration;
  }

  private Client buildClient() throws NoSuchAlgorithmException, KeyManagementException, IOException {
    final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManagerAllowAll() };
    final SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    final Client client = ClientBuilder.newBuilder()
        .register(new HttpBasicAuthFilter(lunchConfiguration.getLunchUsername(), lunchConfiguration.getLunchUserPassword()))
        .register(JacksonFeature.class)
        .hostnameVerifier(new HostnameVerifierAllowAll())
        .sslContext(sc)
        .build();
    client.property(ClientProperties.CONNECT_TIMEOUT, lunchConfiguration.getRestApiConnectTimeout());
    client.property(ClientProperties.READ_TIMEOUT, lunchConfiguration.getRestApiReadTimeout());
    return client;
  }

  @Override
  public List<Lunch> superlunchRestApiGet() {
    try {
      return superlunchRestApiGet(buildClient().target(lunchConfiguration.getRestApi()).path(lunchConfiguration.getRestApiPath()));
    } catch (final NoSuchAlgorithmException | IOException | KeyManagementException e) {
      logger.error(e.getMessage());
    }
    return null;
  }

  @Override
  public boolean superlunchRestApiSignIn(final String id, final String username) {
    try {
      final String path = "/" + id + "/join/" + username;
      return superlunchRestApiPost(buildClient().target(lunchConfiguration.getRestApi()).path(lunchConfiguration.getRestApiPath() + path));
    } catch (final NoSuchAlgorithmException | IOException | KeyManagementException e) {
      logger.error(e.getMessage());
    }
    return false;
  }

  @Override
  public boolean superlunchRestApiSignOut(final String id, final String username) {
    try {
      final String path = "/" + id + "/join/" + username;
      return superlunchRestApiDelete(
          buildClient().target(lunchConfiguration.getRestApi()).path(lunchConfiguration.getRestApiPath() + path));
    } catch (final NoSuchAlgorithmException | IOException | KeyManagementException e) {
      logger.error(e.getMessage());
    }
    return false;
  }

  private List<Lunch> superlunchRestApiGet(final WebTarget target) throws IOException {
    try {
      final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
      logger.debug("Requesting " + target.getUri() + " by GET with Response " + response.getStatus());
      switch (response.getStatus()) {
        default:
          this.logResponseError(response);
          break;
        case 200:
          final String jsonResponse = response.readEntity(String.class);
          final Lunch[] lunches = convertResponseToLunchArray(jsonResponse);
          return new LinkedList<>(Arrays.asList(lunches));
      }
    } catch (final ProcessingException e) {
      logger.error("Unexpected return code from calling '" + e.getMessage());
    }
    return null;
  }

  private boolean superlunchRestApiPost(final WebTarget target) throws IOException {
    try {
      final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(null), Response.class);
      logger.debug("Requesting " + target.getUri() + " by POST with Response " + response.getStatus());
      switch (response.getStatus()) {
        default:
          this.logResponseError(response);
          break;
        case 200:
          return true;
      }
    } catch (final ProcessingException e) {
      logger.error("Unexpected return code from calling '" + e.getMessage());
    }
    return false;
  }

  private boolean superlunchRestApiDelete(final WebTarget target) throws IOException {
    try {
      final Response response = target.request(MediaType.APPLICATION_JSON_TYPE).delete();
      logger.debug("Requesting " + target.getUri() + " by DELETE with Response " + response.getStatus());
      switch (response.getStatus()) {
        default:
          this.logResponseError(response);
          break;
        case 200:
          return true;
      }
    } catch (final ProcessingException e) {
      logger.error("Unexpected return code from calling '" + e.getMessage());
    }
    return false;
  }

  private void logResponseError(final Response response) {
    logger.error("Unexpected return code from calling '" + response.getStatus());
    logger.error(response.readEntity(String.class));
  }

  private Lunch[] convertResponseToLunchArray(final String jsonResponse) {
    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(jsonResponse, Lunch[].class);
    } catch (final IOException e) {
      logger.warn(e.getClass().getName(), e);
    }
    return null;
  }

  private final class HostnameVerifierAllowAll implements HostnameVerifier {

    @Override
    public boolean verify(final String arg0, final SSLSession arg1) {
      return true;
    }
  }

  private final class X509TrustManagerAllowAll implements X509TrustManager {

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
      return null;
    }

    @Override
    public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
    }

    @Override
    public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
    }
  }

}
