/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.time.impl;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PseudoClockSchedulerTest {

    private Job mockJob_1 = mock(Job.class, "mockJob_1");
    private JobContext mockContext_1 = mock(JobContext.class, "mockContext_1");
    private Trigger mockTrigger_1 = mock(Trigger.class, "mockTrigger_1");

    private Job mockJob_2 = mock(Job.class, "mockJob_2");
    private JobContext mockContext_2 = mock(JobContext.class, "mockContext_2");
    private Trigger mockTrigger_2 = mock(Trigger.class, "mockTrigger_2");

    private PseudoClockScheduler scheduler = new PseudoClockScheduler();

    @Test 
    public void removeExistingJob() {
        final Date triggerTime = new Date(1000);
        when( mockTrigger_1.hasNextFireTime() ).thenReturn(triggerTime);

        JobHandle jobHandle = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        assertThat(scheduler.getTimeToNextJob()).isEqualTo(triggerTime.getTime());

        scheduler.removeJob(jobHandle);
        assertThat(scheduler.getTimeToNextJob()).isEqualTo(-1L);
        
        verify( mockTrigger_1, atLeastOnce()).hasNextFireTime();
    }


    @Test 
    public void removeExistingJobWhenMultipleQueued() {
        final Date triggerTime_1 = new Date(1000);
        final Date triggerTime_2 = new Date(2000);
        when( mockTrigger_1.hasNextFireTime() ).thenReturn(triggerTime_1);
        when( mockTrigger_2.hasNextFireTime() ).thenReturn(triggerTime_2);

        JobHandle jobHandle_1 = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        JobHandle jobHandle_2 = scheduler.scheduleJob(mockJob_2, this.mockContext_2, mockTrigger_2);
        assertThat(scheduler.getTimeToNextJob()).isEqualTo(triggerTime_1.getTime());

        scheduler.removeJob(jobHandle_1);
        assertThat(scheduler.getTimeToNextJob()).isEqualTo(triggerTime_2.getTime());

        scheduler.removeJob(jobHandle_2);
        assertThat(scheduler.getTimeToNextJob()).isEqualTo(-1L);
        
        verify( mockTrigger_1, atLeastOnce()).hasNextFireTime();
        verify( mockTrigger_2, atLeastOnce()).hasNextFireTime();
    }

    @Test 
    public void timerIsSetToJobTriggerTimeForExecution() {
        final Date triggerTime = new Date(1000);
        when( mockTrigger_1.hasNextFireTime() ).thenReturn(triggerTime, triggerTime, triggerTime, null);
        when( mockTrigger_1.nextFireTime() ).thenReturn(triggerTime);
        when(mockContext_1.getInternalKnowledgeRuntime()).thenReturn(Optional.empty());

        Job job = new Job() {
            public void execute(JobContext ctx) {
                // Even though the clock has been advanced to 5000, the job should run
                // with the time set its trigger time.
                assertThat(scheduler.getCurrentTime()).isEqualTo(1000L);
            }
        };

        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);

        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);

        // Now, after the job has been executed the time should be what it was advanced to
        assertThat(scheduler.getCurrentTime()).isEqualTo(5000L);
        
        verify( mockTrigger_1, atLeast(2) ).hasNextFireTime();
        verify( mockTrigger_1, times(1) ).nextFireTime();
    }

    @Test 
    public void timerIsResetWhenJobThrowsExceptions() {
        final Date triggerTime = new Date(1000);
        when( mockTrigger_1.hasNextFireTime() ).thenReturn(triggerTime, triggerTime, triggerTime, null);
        when( mockTrigger_1.nextFireTime() ).thenReturn(triggerTime);
        when(mockContext_1.getInternalKnowledgeRuntime()).thenReturn(Optional.empty());

        Job job = new Job() {
            public void execute(JobContext ctx) {
                assertThat(scheduler.getCurrentTime()).isEqualTo(1000L);
                throw new RuntimeException("for test");
            }
        };

        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);

        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);

        // The time must be advanced correctly even when the job throws an exception
        assertThat(scheduler.getCurrentTime()).isEqualTo(5000L);
        verify( mockTrigger_1, atLeast(2) ).hasNextFireTime();
        verify( mockTrigger_1, times(1) ).nextFireTime();
    }
}
