/*
 * Copyright (c) 2015, Andrey Lavrov <lavroff@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import cy.alavrov.jminerguide.log.JMGLogFormatter;
import cy.alavrov.jminerguide.log.JMGLogger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * HTTP Client utility class. Basically, a wrapper for the Apache http client.
 * @author alavrov
 */
public class HTTPClient {
    private CloseableHttpClient httpclient;
    
    public HTTPClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

        RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(10000)
            .setConnectTimeout(10000)
            .build();

        httpclient = HttpClients.custom().setSSLSocketFactory(
                sslsf).setDefaultRequestConfig(config).build();
    }
    
    /**
     * Fetches the page via a GET result and returns it as a string. 
     * Returns null on error.
     * @param request
     * @return page text or null.
     */
    public String getStringFromURL(HttpGet request) {
        CloseableHttpResponse response = null;
        
        try {       
            String result;
            response = httpclient.execute(request);
            StatusLine rstatus = response.getStatusLine();          
            
            HttpEntity entity = response.getEntity();
            try (InputStream ios = entity.getContent()) {
                result = IOUtils.toString(ios, Charset.forName("utf8"));
            }
            
            EntityUtils.consume(entity);
            
            if (rstatus.getStatusCode() != HttpStatus.SC_OK) {
                try {
                    response.close();
                    httpclient.close();
                } catch (IOException e) {}
                
                JMGLogger.logSevere("Fetching "+request.toString()+" failed with status "+rstatus.getStatusCode()+", returned: "+result);                                                
                return null;
            }
            
            return result;
        } catch (IOException ioe) {
            JMGLogger.logSevere("IOException during fetching "+request.toString(), ioe);  
            return null;
        } finally {
            try {
                if (response != null) response.close();
                httpclient.close();
            } catch (IOException e) {}
        }
    }
}
