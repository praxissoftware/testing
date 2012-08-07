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
package com.meltmedia.testing.http.harness;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import com.meltmedia.testing.http.harness.jaxrs.Application;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This test harness implementation wraps an embedded Jetty instance.
 * @author Jason Rose
 *
 */
public class JaxRSJettyTestHarness implements HttpTestHarness {

  private final int PORT = 11117;

  private final String HOST_NAME = "http://localhost";

  private Server server;

  private final Filter[] filters;

  private final Object[] context;

  public JaxRSJettyTestHarness() {
    this(new Filter[0]);
  }

  public JaxRSJettyTestHarness(final Filter[] filters) {
    this(new Object[0], filters);
  }

  public JaxRSJettyTestHarness(final Object[] context) {
    this(context, new Filter[0]);
  }

  public JaxRSJettyTestHarness(final Object[] context, final Filter[] filters) {
    this.filters = filters;
    this.context = context;
  }

  @Override
  public void dispose() {
    if( server == null || server.isStopped() ) {
      throw new IllegalStateException("Server is currently not running.");
    }
    try {
      server.stop();
      server.destroy();
    } catch (final RuntimeException re) {
      throw re;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    server = null;
  }

  @Override
  public String getHostName() {
    return HOST_NAME;
  }

  @Override
  public int getPort() {
    return PORT;
  }

  @Override
  public void start() {
    if( server != null ) {
      throw new IllegalStateException("Server has already been started.");
    }
    try {
      server = new Server(PORT);
      final Context root = new Context(server, "/", Context.SESSIONS);

      // We apparently need a servlet in place for any filters to work.
      root.addServlet(new ServletHolder(new DefaultServlet()), "/default-servlet");

      // Add all the filter objects.
      final List<Filter> filterList = new ArrayList<Filter>(Arrays.asList(filters));
      filterList.add(new RequestFilter()); // for debugging

      // Add the context.
      if( context != null && context.length > 0 ) {
        final ServletContainer container = new ServletContainer(new Application(context));
        root.addServlet(new ServletHolder(container), "/*");
      }

      // We reverse order the filters because Jetty treats the last applied filter as the first in the filter chain.
      Collections.reverse(filterList);
      for( final Filter filter : filterList ) {
        final FilterHolder holder = new FilterHolder();
        holder.setFilter(filter);
        root.addFilter(holder, "/*", org.mortbay.jetty.Handler.DEFAULT);
      }
      server.start();
    } catch (final RuntimeException re) {
      throw re;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String url(final String path) throws MalformedURLException {
    if( server == null || !server.isRunning() ) {
      throw new IllegalStateException("Server must be running to generate URLs.");
    }
    return new URL(HOST_NAME + ":" + PORT + (path.startsWith("/") ? path : "/" + path)).toString();
  }

}
