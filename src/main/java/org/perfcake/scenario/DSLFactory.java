/*
 * -----------------------------------------------------------------------\
 * PerfCake
 *  
 * Copyright (C) 2010 - 2013 the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -----------------------------------------------------------------------/
 */
package org.perfcake.scenario;

import org.perfcake.PerfCakeException;
import org.perfcake.scenario.dsl.ScenarioDelegate;
import org.perfcake.util.Utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import groovy.lang.Binding;

/**
 * Loads the scenario from a DSL script.
 * This class serves as a bridge between Java and Groovy DSL scenario implementation.
 * The DSL language is pure Groovy based on a set of classes specially designed to allow the needed syntax.
 *
 * @author Martin Večeřa <marvenec@gmail.com>
 */
public class DSLFactory implements ScenarioFactory {

   public static final Logger log = Logger.getLogger(DSLFactory.class);
   private String scenarioDefinition;
   private Scenario scenario = null;

   public void init(final URL scenarioURL) throws PerfCakeException {
      try {
         this.scenarioDefinition = Utils.readFilteredContent(scenarioURL);

         if (log.isDebugEnabled()) {
            log.debug(String.format("Loaded scenario definition from '%s'.", scenarioURL.toString()));
         }
      } catch (IOException e) {
         throw new PerfCakeException("Cannot read scenario configuration: ", e);
      }
   }

   public synchronized Scenario getScenario() throws PerfCakeException {
      if (scenario == null) {
         final Binding binding = new Binding();
         binding.setProperty("dslScript", scenarioDefinition);

         final ScenarioDelegate scenarioDelegate = new ScenarioDelegate();
         scenarioDelegate.setBinding(binding);
         scenario = (Scenario) scenarioDelegate.run();
      }

      return scenario;
   }
}
