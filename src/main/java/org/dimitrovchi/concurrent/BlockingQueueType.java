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
package org.dimitrovchi.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Blocking queue type.
 * @author Dmitry Ovchinnikov
 */
public enum BlockingQueueType {
    
    ARRAY_BLOCKING_QUEUE {
        @Override
        public BlockingQueue<Runnable> createBlockingQueue(int size) {
            return new ArrayBlockingQueue<>(size == 0 ? 1000 : size);
        }
    },
    LINKED_BLOCKING_QUEUE {
        @Override
        public BlockingQueue<Runnable> createBlockingQueue(int size) {
            return new LinkedBlockingQueue<>(size == 0 ? Integer.MAX_VALUE : size);
        }
    },
    SYNCHRONOUS_QUEUE {
        @Override
        public BlockingQueue<Runnable> createBlockingQueue(int size) {
            return new SynchronousQueue<>();
        }
    };
    
    /**
     * Creates a blocking queue with given size.
     * @param size Blocking queue size (pass 0 to allocate a default queue for this queue type).
     * @return Blocking queue.
     */
    public abstract BlockingQueue<Runnable> createBlockingQueue(int size);
}
