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

import java.net.MalformedURLException;

/**
 * This interface defines the generic functionality allowed to our test harnesses.
 * @author Jason Rose
 * 
 */
public interface HttpTestHarness {

  /**
   * Stop the server.
   */
  void dispose();

  /**
   * Returns the host name that this harness is masquerading as.
   * @return The hoste name that this harness is masquerading as.
   */
  String getHostName();

  /**
   * Returns the port that this server is binding to.
   * @return The port that this server is binding to.
   */
  int getPort();

  /**
   * Starts the server.
   */
  void start();

  /**
   * Generates a fully-qualified URL for the requested path.
   * @param path The path to generate a URL for.
   * @return A fully-qualified URL string.
   * @throws MalformedURLException If the path is bad.
   */
  String url(String path) throws MalformedURLException;
}
