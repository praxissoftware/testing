/**
 * Copyright (C) 2012 Jason Rose <jasoncrose@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.praxissoftware.testing.http.harness;

import javax.servlet.Filter;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

import com.github.praxissoftware.testing.http.harness.JaxRSJettyTestHarness;
import com.jayway.restassured.RestAssured;

public class JaxRSJettyTestHarnessTest {

  private JaxRSJettyTestHarness service;

  @After
  public void after() throws Exception {
    try {
      service.dispose();
    } catch (final Exception e) {
    } finally {
      service = null;
    }
  }

  @Test
  public void testFilterOrderWorks() throws Exception {
    service = new JaxRSJettyTestHarness(new Filter[] { new EchoFilter("first"), new EchoFilter("second") });
    service.start();
    final String path = service.url("/default-servlet");
    RestAssured.given().expect().statusCode(200).and().body(Matchers.containsString("first")).when().get(path);
    service.dispose();
  }

  @Test
  public void testFilterWorks() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.start();
    String path = service.url("/default-servlet");
    RestAssured.given().expect().statusCode(405).when().get(path);
    service.dispose();

    service = new JaxRSJettyTestHarness(new Filter[] { new EchoFilter("filter worked") });
    service.start();
    path = service.url("/default-servlet");
    RestAssured.given().expect().statusCode(200).and().body(Matchers.containsString("filter worked")).when().get(path);
    service.dispose();
  }

  @Test
  public void testJerseyEndpointWorks() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.start();
    String path = service.url("/echo");
    RestAssured.given().expect().statusCode(404).when().get(path);
    service.dispose();

    service = new JaxRSJettyTestHarness(new Object[] { new EchoResource("resource worked") });
    service.start();
    path = service.url("/echo");
    RestAssured.given().expect().statusCode(200).and().body(Matchers.containsString("resource worked")).when().get(path);
    service.dispose();
  }

  @Test
  public void testJerseyProviderWorks() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.start();
    String path = service.url("/echo");
    RestAssured.given().expect().statusCode(404).when().get(path);
    service.dispose();

    service = new JaxRSJettyTestHarness(new Object[] { new ExceptionResource(RuntimeException.class), new ExceptionMapper(500, "exception worked") });
    service.start();
    path = service.url("/echo");
    RestAssured.given().expect().statusCode(500).and().body(Matchers.containsString("exception worked")).when().get(path);
    service.dispose();
  }

  @Test(expected = IllegalStateException.class)
  public void testShutdownLifecycle() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.dispose();
  }

  @Test
  public void testShutdownLifecycleWorks() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.start();
    service.dispose();
  }

  @Test(expected = IllegalStateException.class)
  public void testStartLifecycle() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.start();
    service.start();
  }

  @Test(expected = IllegalStateException.class)
  public void testUrlNeedsRunningServer() throws Exception {
    service = new JaxRSJettyTestHarness();
    service.url("/test");
  }
}
