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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter just echoes out its injected String. It is useful for testing filters.
 * @author Jason Rose
 */
public class EchoFilter implements Filter {

  private final String echo;

  public EchoFilter() {
    this(null);
  }

  public EchoFilter(final String echo) {
    this.echo = echo;
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
    final HttpServletResponse response = (HttpServletResponse) resp;
    response.setHeader("Content-Type", "text/plain; charset=utf-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().print(echo);
  }

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
  }

}
