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

import java.io.IOException;
import org.dimitrovchi.conf.service.CommonServiceParams;
import org.dimitrovchi.conf.service.demo.DemoService;
import org.dimitrovchi.conf.service.demo.DemoServiceParams;

/**
 * Annotated service demo.
 * @author Dmitry Ovchinnikov
 */
public class AnnotatedServiceDemo {
    
    public static void main(String... args) throws Exception {
        try (final AnnotatedDemoService service = new AnnotatedDemoService()) {
            service.start();
            Thread.sleep(60_000L);
            service.stop();
        }
    }
    
    @CommonServiceParams(threadCount = 1)
    @DemoServiceParams(port = 8888)
    static class AnnotatedDemoService extends DemoService {
        
        public AnnotatedDemoService() throws IOException {
        }
    }
}
