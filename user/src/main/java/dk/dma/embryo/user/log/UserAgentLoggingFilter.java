/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.embryo.user.log;

import dk.dma.embryo.common.configuration.Configuration;
import dk.dma.embryo.user.security.Subject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Slf4j
public class UserAgentLoggingFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        logRequest(request);
    }

    public void destroy() {

    }

    public static void logRequest(ServletRequest req) {

        HttpServletRequest request = (HttpServletRequest) req;

        if (log.isInfoEnabled()) {

            Subject subject = Configuration.getBean(Subject.class);
            String user = subject.isLoggedIn() ? subject.getUser().getUserName() : "Unknown";

            String userAgent = request.getHeader("User-Agent");
            log.info("User: {}, User-Agent: {}", user, userAgent);
        }
    }
}
