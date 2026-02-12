package com.hospital.system.appointments.support;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class MvcEndpointTestSupport {

    public record EndpointSpec(HttpMethod method, String path, String requestBody){}

    public void performEndpointAndExpect(
            MockMvc mockMvc, EndpointSpec spec, UsernamePasswordAuthenticationToken auth, int expectedStatus) throws Exception{
        MockHttpServletRequestBuilder request = buildRequest(spec);
        if (auth != null) {
            request = request.with(authentication(auth));
        }
        mockMvc.perform(request).andExpect(status().is(expectedStatus));
    }

    private MockHttpServletRequestBuilder buildRequest(EndpointSpec spec) {
        if (spec.method() == HttpMethod.GET) {
            return get(spec.path());
        }
        if (spec.method() == HttpMethod.DELETE) {
            return delete(spec.path());
        }
        if (spec.method() == HttpMethod.PUT) {
            MockHttpServletRequestBuilder putRequest = put(spec.path()).contentType(MediaType.APPLICATION_JSON);
            return spec.requestBody() != null ? putRequest.content(spec.requestBody()) : putRequest;
        }
        if (spec.method() == HttpMethod.POST) {
            MockHttpServletRequestBuilder postRequest = post(spec.path()).contentType(MediaType.APPLICATION_JSON);
            return spec.requestBody() != null ? postRequest.content(spec.requestBody()) : postRequest;
        }
        throw new IllegalStateException("Unexpected method: " + spec.method());
    }
}
