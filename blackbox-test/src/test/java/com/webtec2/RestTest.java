package com.webtec2;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.webtec2.DBMessage;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class RestTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public RestTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(RestTest.class);
	}

	public void testStartupBean() {

		final ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(JacksonJsonProvider.class);

		final Client client = ClientBuilder.newClient(clientConfig);
		final WebTarget resource = client.target("http://localhost:8080/blackbox");
		final Response response = resource.path("news").request(MediaType.APPLICATION_JSON).get();

		final List<DBMessage> msg = response.readEntity(new GenericType<List<DBMessage>>() {});
		// For single results, use
		// final DBMessage msg = response.readEntity(DBMessage.class);

		Assert.assertEquals(1, msg.size());

		final DBMessage item = msg.get(0);

		Assert.assertEquals("Info", item.getHeadline());
	}
}
