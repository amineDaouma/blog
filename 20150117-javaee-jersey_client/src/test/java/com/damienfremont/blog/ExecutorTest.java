package com.damienfremont.blog;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import net.jadler.Jadler;

import org.fest.assertions.api.Assertions;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ExecutorTest {

	PersonService client;

	@Before
	public void setUp() {
		// INIT MOCK
		initJadler();
		// INIT REST CLIENT
		client = ProxyFactory.create(//
				PersonService.class, //
				"http://localhost:" + Jadler.port() + "/api", new Executor());
	}

	@After
	public void tearDown() {
		closeJadler();
	}

	@Test
	public void test_QUAND_ping_ETANT_DONNE_system_ok_ALORS_success() {

		// ETANT DONNE
		onRequest().havingMethodEqualTo("GET")
				.havingPathEqualTo("/api/persons").respond()
				.withDelay(1, SECONDS);

		// QUAND
			client.ping();
	}

	@Test
	public void test_QUAND_ping_ETANT_DONNE_system_lag_ALORS_timeout() {

		// ETANT DONNE
		onRequest().havingMethodEqualTo("GET")
				.havingPathEqualTo("/api/persons").respond()
				.withDelay(50, SECONDS);

		// QUAND
		try {
			client.ping();
			fail("Expected Exception!");
		} catch (Exception e) {
			Assertions.assertThat(e.getMessage()).contains("Read timed out");
		}
	}

}