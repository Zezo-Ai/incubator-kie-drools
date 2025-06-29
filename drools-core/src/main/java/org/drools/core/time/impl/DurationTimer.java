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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ConditionalElement;
import org.drools.base.rule.Declaration;
import org.drools.base.time.JobHandle;
import org.drools.base.time.Trigger;
import org.drools.base.time.impl.Timer;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.MathUtils;
import org.kie.api.runtime.Calendars;
import org.kie.api.time.Calendar;

public class DurationTimer extends BaseTimer
    implements
        Timer,
    Externalizable {

    private long duration;
    private Declaration eventFactHandle;

    public DurationTimer() {

    }

    public DurationTimer(long duration) {
        this.duration = duration;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(duration);
        out.writeObject(eventFactHandle);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        duration = in.readLong();
        eventFactHandle = (Declaration ) in.readObject();
    }

    public long getDuration() {
        return duration;
    }

    public Declaration[][] getTimerDeclarations(Map<String, Declaration> outerDeclrs) {
        return new Declaration[][] { new Declaration[] { getEventFactHandleDeclaration()}, null };
    }

    public Trigger createTrigger(InternalMatch item, InternalWorkingMemory wm) {
        long timestamp;
        if (eventFactHandle != null) {
            Tuple leftTuple = item.getTuple();
            DefaultEventHandle fh = (DefaultEventHandle) leftTuple.get(eventFactHandle);
            timestamp = fh.getStartTimestamp();
        } else {
            timestamp = wm.getTimerService().getCurrentTime();
        }
        String[] calendarNames = item.getRule().getCalendars();
        Calendars calendars = wm.getCalendars();
        return createTrigger(timestamp, calendarNames, calendars);
    }

    public Trigger createTrigger(long timestamp,
                                 BaseTuple leftTuple,
                                 JobHandle jh,
                                 String[] calendarNames,
                                 Calendars calendars,
                                 Declaration[][] declrs,
                                 ValueResolver valueResolver) {
        return createTrigger(getEventTimestamp(leftTuple, timestamp), calendarNames, calendars);
    }

    long getEventTimestamp(BaseTuple leftTuple, long timestamp) {
        return eventFactHandle != null ?
               ((DefaultEventHandle) leftTuple.get(eventFactHandle)).getStartTimestamp() :
               timestamp;
    }

    public Trigger createTrigger(long timestamp,
                                 String[] calendarNames,
                                 Calendars calendars) {
        long offset = timestamp + duration;
        if (MathUtils.isAddOverflow(timestamp, duration, offset)) {
            // this should not happen, but possible in some odd simulation scenarios, so creating a trigger for immediate execution instead
            return PointInTimeTrigger.createPointInTimeTrigger(timestamp, getCalendars(calendarNames, calendars));
        } else {
            return PointInTimeTrigger.createPointInTimeTrigger(offset, getCalendars(calendarNames, calendars));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (duration ^ (duration >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        DurationTimer other = (DurationTimer) obj;
        if ( duration != other.duration ) {
            return false;
        }
        return true;
    }

    @Override
    public ConditionalElement clone() {
        return new DurationTimer( duration );
    }

    @Override
    public String toString() {
        return "DurationTimer: " + duration + "ms";
    }

    public void setEventFactHandle(Declaration eventFactHandle) {
        this.eventFactHandle = eventFactHandle;
    }

    public Declaration getEventFactHandleDeclaration() {
        return eventFactHandle;
    }

    private Collection<Calendar> getCalendars(final String[] calendarNames, final Calendars calendars) {
        final List<Calendar> result = new ArrayList<>();
        if (calendars != null && calendarNames != null && calendarNames.length > 0) {
            for (final String calName : calendarNames) {
                result.add(calendars.get(calName));
            }
        }
        return result;
    }
}
