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
package dk.dma.embryo.user.shiro;

import dk.dma.embryo.common.configuration.Configuration;
import dk.dma.embryo.common.servlet.MultiReadHttpServletRequest;
import dk.dma.embryo.user.security.Subject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class EmbryoChangePasswordFilter extends AccessControlFilter {
    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        MultiReadHttpServletRequest multiReadRequest = MultiReadHttpServletRequest.create((HttpServletRequest) request);
        super.doFilterInternal(multiReadRequest, response, chain);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Object jsonObject = Util.getJson(request.getInputStream(), Object.class);
        String username = Util.getValue(jsonObject, ((String[]) mappedValue)[0]);

        Subject subject = Configuration.getBean(Subject.class);
        return subject.authorizedToChangePassword(username);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        log.debug("Access denied: {}, {}", WebUtils.toHttp(request).getRequestURI(), ((String[]) mappedValue)[0]);

        HttpServletResponse httpResp = WebUtils.toHttp(response);
        httpResp.setContentType("application/json");
        httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        Util.writeJson(httpResp, new Error(Error.AuthCode.UNAUTHORIZED,
                "User not authorized to change password for the user in question"));
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}
