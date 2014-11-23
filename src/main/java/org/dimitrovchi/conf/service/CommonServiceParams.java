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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.dimitrovchi.concurrent.BlockingQueueType;

/**
 * Common service parameters.
 * @author Dmitry Ovchinnikov
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CommonServiceParams {
    
    int threadCount() default 0;
    
    long keepAlive() default 0L;
    
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    
    int queueSize() default 0;
    
    BlockingQueueType queueType() default BlockingQueueType.LINKED_BLOCKING_QUEUE;
}
