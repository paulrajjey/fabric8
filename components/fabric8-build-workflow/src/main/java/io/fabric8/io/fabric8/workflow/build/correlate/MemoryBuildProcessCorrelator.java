/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.io.fabric8.workflow.build.correlate;

import io.fabric8.io.fabric8.workflow.build.BuildCorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in memory implementation for testing.
 */
public class MemoryBuildProcessCorrelator implements BuildProcessCorrelator {
    private static final transient Logger LOG = LoggerFactory.getLogger(MemoryBuildProcessCorrelator.class);

    private Map<BuildCorrelationKey, Long> map = new ConcurrentHashMap<>();

    @Override
    public void putBuildProcessInstanceId(BuildCorrelationKey buildKey, long processInstanceId) {
        Long oldPid = map.get(buildKey);
        if (oldPid != null) {
            LOG.warn("Already associated build key " + buildKey + " with processID: " + oldPid + " so ignoring newer process: " + processInstanceId);
            return;
        }
        map.put(buildKey, processInstanceId);
    }

    @Override
    public Long findProcessInstanceIdForBuild(BuildCorrelationKey buildKey) {
        return map.get(buildKey);
    }
}
