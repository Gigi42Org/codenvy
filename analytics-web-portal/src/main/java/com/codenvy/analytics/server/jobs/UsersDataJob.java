/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */


package com.codenvy.analytics.server.jobs;

import com.codenvy.analytics.metrics.*;
import com.codenvy.analytics.metrics.value.FSValueDataManager;
import com.codenvy.analytics.metrics.value.ListListStringValueData;
import com.codenvy.analytics.metrics.value.MapStringListListStringValueData;
import com.codenvy.analytics.metrics.value.ValueData;

import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class UsersDataJob implements Job, ForceableJobRunByContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersDataJob.class);

    /** {@inheritDoc} */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Map<String, String> execContext = Utils.initializeContext(TimeUnit.DAY);
            run(execContext);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void forceRun(Map<String, String> context) throws Exception {
        run(context);
    }

    private void run(Map<String, String> context) throws Exception {
        LOGGER.info("UsersDataJob is started " + context.toString());
        long start = System.currentTimeMillis();

        try {
//            for (MetricType metricType : MetricType.values()) {
//                DataProcessing.calculateAndStore(metricType, context);
//            }
            DataProcessing.calculateAndStore(MetricType.PRODUCT_USAGE_TIME_USERS, context);
            DataProcessing.calculateAndStore(MetricType.PRODUCT_USAGE_TIME_DOMAINS, context);
            // TODO
        } finally {
            LOGGER.info("UsersDataJob is finished in " + (System.currentTimeMillis() - start) / 1000 + " sec.");
        }
    }
}
