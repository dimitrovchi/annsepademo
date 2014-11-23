/*
 * Copyright 2014 Dmitry Ovchinnikov.
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
 */
package org.dimitrovchi.conf.service.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.dimitrovchi.conf.service.AbstractService;
import org.dimitrovchi.conf.service.CommonServiceParams;
import org.dimitrovchi.conf.service.ServiceParameterUtils;
import org.springframework.core.env.Environment;

/**
 * Demo service.
 * @author Dmitry Ovchinnikov
 * @param <P> Demo service parameters container type.
 */
public class DemoService<P extends CommonServiceParams & DemoServiceParams> extends AbstractService<P> {
    
    protected final HttpServer httpServer;

    public DemoService(P parameters) throws IOException {
        super(parameters);
        this.httpServer = HttpServer.create(new InetSocketAddress(parameters.host(), parameters.port()), 0);
        this.httpServer.setExecutor(executor);
        this.httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange he) throws IOException {
                he.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                final byte[] message = "hello!".getBytes(StandardCharsets.UTF_8);
                he.sendResponseHeaders(HttpURLConnection.HTTP_OK, message.length);
                he.getResponseBody().write(message);
            }
        });
        log.info(ServiceParameterUtils.reflectToString("demoService", parameters));
    }
    
    public DemoService() throws IOException {
        this(DemoService.<P>mergeAnnotationParameters()); // In Java 8 just call mergeAnnotationParameters() ;-)
    }
    
    public DemoService(String prefix, Environment environment) throws IOException {
        this(DemoService.<P>parameters(prefix, environment));
    }
    
    @PostConstruct
    public void start() {
        httpServer.start();
        log.info(getClass().getSimpleName() + " started");
    }
    
    @PreDestroy
    public void stop() {
        httpServer.stop(parameters.shutdownTimeout());
        log.info(getClass().getSimpleName() + " destroyed");
    }
}
