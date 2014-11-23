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
package org.dimitrovchi.conf.service;

import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

/**
 * Abstract demo service.
 * @author Dmitry Ovchinnikov
 * @param <P> Service parameters container type.
 */
public abstract class AbstractService<P extends CommonServiceParams> implements AutoCloseable {
    
    protected final Log log = LogFactory.getLog(getClass());
    protected final P parameters;
    protected final ThreadPoolExecutor executor;
    
    public AbstractService(P parameters) {
        this.parameters = parameters;
        final int threadCount = parameters.threadCount() == 0 
                ? Runtime.getRuntime().availableProcessors() 
                : parameters.threadCount();
        this.executor = new ThreadPoolExecutor(
                threadCount, 
                parameters.threadCount(), 
                parameters.keepAlive(), 
                parameters.timeUnit(), 
                parameters.queueType().createBlockingQueue(parameters.queueSize()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
    
    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
  
    /**
     * Merges annotated parameters from class annotations.
     * @param <P> Parameters type.
     * @return Merged parameters.
     */
    protected static <P> P mergeAnnotationParameters() {
        return ServiceParameterUtils.mergeAnnotationParameters();
    }
    
    /**
     * Get parameters from Spring environment.
     * @param <P> Parameters type.
     * @param prefix Environment prefix.
     * @param environment Spring environment.
     * @return Parameters parsed from the environment.
     */
    protected static <P> P parameters(String prefix, Environment environment) {
        return ServiceParameterUtils.parameters(prefix, environment);
    }
}
