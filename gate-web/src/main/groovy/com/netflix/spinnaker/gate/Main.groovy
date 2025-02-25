/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate

import graphql.kickstart.spring.web.boot.GraphQLWebsocketAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.ldap.LdapHealthContributorAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.EnableAsync
import com.netflix.spinnaker.kork.boot.DefaultPropertiesBuilder

@EnableAsync
@EnableConfigurationProperties
@SpringBootApplication(
  scanBasePackages = [
    "com.netflix.spinnaker.gate",
    "com.netflix.spinnaker.config"
  ],
  exclude = [
    GroovyTemplateAutoConfiguration,
    GsonAutoConfiguration,
    LdapHealthContributorAutoConfiguration,
//    GraphQLWebsocketAutoConfiguration
  ]
)
class Main {

  static final Map<String, String> DEFAULT_PROPS = new DefaultPropertiesBuilder().property("spring.application.name", "gate").build()

  static void main(String... args) {
    new SpringApplicationBuilder().properties(DEFAULT_PROPS).sources(Main).run(args)
  }
}
