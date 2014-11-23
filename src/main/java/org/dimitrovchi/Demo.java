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
package org.dimitrovchi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dimitrovchi.conf.DemoApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Demo entry-point class.
 * @author Dmitry Ovchinnikov
 */
public class Demo {
    
    private static final Log LOG = LogFactory.getLog(Demo.class);
    
    public static void main(String... args) throws Exception {
        final String confPkgName = DemoApplicationConfiguration.class.getPackage().getName();
        try (final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(confPkgName)) {
            LOG.info("Context " + context + " started");
            context.start();
            Thread.sleep(60_000L);
        }
    }
}
