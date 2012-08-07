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
package com.meltmedia.testing.http.harness.jaxrs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a shim to allow us to build a JAX-RS Application object with an injected set of endpoints and providers.
 * @author Jason Rose
 */
public class Application extends javax.ws.rs.core.Application {

  private final Set<Object> context;

  public Application(final Object[] context) {
    this(new HashSet<Object>(Arrays.asList(context)));
  }

  public Application(final Set<Object> context) {
    this.context = context;
  }

  @Override
  public Set<Object> getSingletons() {
    return context;
  }
}
