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
package io.fabric8.io.fabric8.workflow.build.trigger;

import io.fabric8.io.fabric8.workflow.build.BuildCorrelationKey;
import io.fabric8.io.fabric8.workflow.build.correlate.BuildProcessCorrelator;
import io.fabric8.io.fabric8.workflow.build.correlate.BuildProcessCorrelators;
import io.fabric8.utils.Strings;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invoked from inside a jBPM process to trigger a new build in OpenShift and register
 * the {@link io.fabric8.io.fabric8.workflow.build.BuildCorrelationKey}
 * of the new build into the {@link BuildProcessCorrelator}
 */
public class BuildWorkItemHandler implements WorkItemHandler {
    private static final transient Logger LOG = LoggerFactory.getLogger(BuildWorkItemHandler.class);

    private BuildProcessCorrelator buildProcessCorrelator = BuildProcessCorrelators.getSingleton();
    private BuildTrigger buildTrigger = BuildTriggers.getSingleton();

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        long processInstanceId = workItem.getProcessInstanceId();

        String buildName = (String) workItem.getParameter("BuildName");
        String namespace = (String) workItem.getParameter("Namespace");

        LOG.info("Executing namespace: " + namespace + " build: " + buildName
                + " processInstanceId: " + processInstanceId + " workItemId: " + workItem.getId());

        if (Strings.isNullOrBlank(namespace)) {
            fail(workItem, manager, "Missing workflow configuration for Namespace");
        } else if (Strings.isNullOrBlank(buildName)) {
            fail(workItem, manager, "Missing workflow configuration for BuildName");
        } else {
            String buildUuid = buildTrigger.trigger(namespace, buildName);
            if (Strings.isNullOrBlank(buildUuid)) {
                fail(workItem, manager, "Could not trigger build for namespace: " + namespace + " build: " + buildName);
            } else {
                BuildCorrelationKey key = new BuildCorrelationKey(namespace, buildName, buildUuid);
                buildProcessCorrelator.putBuildProcessInstanceId(key, processInstanceId);
            }
        }

        // TODO else you could trigger completion using:
        // POST http://localhost:8080/jbpm-console/rest/runtime/demo:Build:1.0/workitem/INSERT_WORKITEMID_HERE/complete?map_Outcome=Success&map_ResultUrl=www.jbpm.org
    }

    protected void fail(WorkItem workItem, WorkItemManager manager, String reason) {
        // TODO how best to signal the failure of the work item?
        LOG.error("Failed to complete work item " + workItem.getId() + " due to: " + reason);
        workItem.getResults().put("Failed", reason);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("Aborting " + workItem.getParameter("BuildId"));
    }

    public BuildTrigger getBuildTrigger() {
        return buildTrigger;
    }

    public void setBuildTrigger(BuildTrigger buildTrigger) {
        this.buildTrigger = buildTrigger;
    }

    public BuildProcessCorrelator getBuildProcessCorrelator() {
        return buildProcessCorrelator;
    }

    public void setBuildProcessCorrelator(BuildProcessCorrelator buildProcessCorrelator) {
        this.buildProcessCorrelator = buildProcessCorrelator;
    }
}