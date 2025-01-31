/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.security.oauth2

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor
import org.springframework.stereotype.Component

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest

/**
 * This class supports the use case of an externally provided OAuth access token, for example, a
 * Github-issued personal access token.
 */
@Slf4j
@Component
class ExternalAuthTokenFilter implements Filter {

  // UserInfoRestTemplateFactory can't be Autowired if no oauth2 configurations are set.
  // In this case, userInfoRestTemplateFactory will be null
  @Autowired(required = false)
  UserInfoRestTemplateFactory userInfoRestTemplateFactory

  BearerTokenExtractor extractor = new BearerTokenExtractor()

  @Override
  void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    def httpServletRequest = (HttpServletRequest) request
    Authentication auth = SecurityContextHolder.getContext().getAuthentication()
    if (auth?.principal) {
      DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(auth.principal.toString())
      // Reassign token type to be capitalized "Bearer",
      // see https://github.com/spinnaker/spinnaker/issues/2074
      token.tokenType = OAuth2AccessToken.BEARER_TYPE
      if (userInfoRestTemplateFactory != null) {
        def ctx = userInfoRestTemplateFactory.getUserInfoRestTemplate().getOAuth2ClientContext()
        ctx.accessToken = token
      }
    }
    chain.doFilter(request, response)
  }

  @Override
  void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  void destroy() {}
}
