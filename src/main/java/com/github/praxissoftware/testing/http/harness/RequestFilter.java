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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is designed to log servlet requests out to the log.
 * @author Christian Trimble
 */
public class RequestFilter implements Filter {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
    log.debug("The Request filter has been called.");

    // get the system time.
    final long startSystemTime = System.currentTimeMillis();

    // Wrap the response so we can log the response headers.
    final Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();

    final HttpServletResponseWrapper httpResponse = new HttpServletResponseWrapper((HttpServletResponse) response) {
      @Override
      public void addHeader(final String name, final String value) {
        List<String> values = responseHeaders.get(name);
        if( values == null ) {
          values = new ArrayList<String>();
          responseHeaders.put(name, values);
        }
        values.add(value);
        super.addHeader(name, value);
      }

      @Override
      public void setHeader(final String name, final String value) {
        final List<String> values = new ArrayList<String>();
        values.add(value);
        responseHeaders.put(name, values);
        super.setHeader(name, value);
      }
    };

    try {

      chain.doFilter(request, httpResponse);
    } finally {
      final long endSystemTime = System.currentTimeMillis();
      final long requestTime = endSystemTime - startSystemTime;

      if( request instanceof HttpServletRequest ) {
        logHttpRequest((HttpServletRequest) request, responseHeaders, requestTime);
      }
    }
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
  }

  @SuppressWarnings("unchecked")
  private void logHttpRequest(final HttpServletRequest request, final Map<String, List<String>> responseHeaders, final long requestTime) {
    final StringBuilder message = new StringBuilder();
    message.append("Request for ").append(request.getRequestURI()).append(" took ").append(requestTime).append(" millis.\n");
    message.append("Request URI:    ").append(request.getRequestURI()).append("\n");
    message.append("Request Method: ").append(request.getMethod()).append("\n");
    message.append("Request Time:   ").append(requestTime).append("\n");
    final Enumeration<String> parameterNames = request.getParameterNames();

    if( parameterNames.hasMoreElements() ) {
      message.append("Request Parameters:\n");
      while( parameterNames.hasMoreElements() ) {
        final String name = parameterNames.nextElement();
        final String[] values = request.getParameterValues(name);
        for( final String value : values ) {
          message.append("  ").append(name).append("=").append(value).append("\n");
        }
      }
    }

    final Enumeration<String> headerNames = request.getHeaderNames();

    if( headerNames.hasMoreElements() ) {
      message.append("\nRequest Headers:\n");
      while( headerNames.hasMoreElements() ) {
        final String name = headerNames.nextElement();
        final Enumeration<String> values = request.getHeaders(name);
        for( final String value : Collections.list(values) ) {
          message.append("  ").append(name).append("=").append(value).append("\n");
        }
      }
    }

    if( !responseHeaders.isEmpty() ) {
      message.append("\nResponse Headers:\n");
      for( final Entry<String, List<String>> entry : responseHeaders.entrySet() ) {
        message.append("  ").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
      }
    }

    log.debug(message.toString());
  }

}
