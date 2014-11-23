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
package org.dimitrovchi.conf;

import java.io.IOException;
import org.dimitrovchi.conf.service.demo.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Demo application configuration.
 * @author Dmitry Ovchinnikov
 */
@Configuration
@PropertySource("classpath:/application.properties")
public class DemoApplicationConfiguration {
    
    @Autowired
    private Environment environment;
    
    @Bean
    public DemoService demoService() throws IOException {
        return new DemoService("demoService", environment);
    }
}
