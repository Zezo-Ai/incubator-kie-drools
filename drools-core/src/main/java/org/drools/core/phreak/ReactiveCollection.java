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
package org.drools.core.phreak;

import java.util.Collection;
import java.util.Iterator;

import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;
import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;

public class ReactiveCollection<T, W extends Collection<T>> extends AbstractReactiveObject implements Collection<T> {

    protected final W wrapped;

    public ReactiveCollection(W wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }
    
    @Override
    public boolean add(T t) {
        boolean result = wrapped.add(t);
        if (result) {
            ReactiveObjectUtil.notifyModification(t, getTuples(), ModificationType.ADD);
            if (t instanceof ReactiveObject) {
                for (BaseTuple lts : getTuples()) {
                    ((ReactiveObject) t).addTuple(lts);
                }
            }
        }
        return result;
    }
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = false;
        for ( T elem : c ) {
            result |= add(elem);
        }
        return result;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for ( Object elem : c ) {
            result |= remove(elem);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        for ( T elem : wrapped ) {
            if ( !c.contains(elem) ) {
                result |= remove(elem);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        for ( T elem : wrapped ) {
            wrapped.remove(elem);
        }
    }
    
    @Override
    public boolean remove(Object o) {
        boolean result = wrapped.remove(o);
        if (result) {
            if (o instanceof ReactiveObject) {
                for (BaseTuple lts : getTuples()) {
                    ((ReactiveObject) o).removeTuple(lts);
                }
            }
            ReactiveObjectUtil.notifyModification(o, getTuples(), ModificationType.REMOVE);
        }
        return result;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ReactiveIterator<>( wrapped.iterator() );
    }
    
    protected class ReactiveIterator<WI extends Iterator<T>> implements Iterator<T> {
        protected WI wrapped;
        protected T last;

        public ReactiveIterator(WI wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean hasNext() {
            return wrapped.hasNext();
        }

        @Override
        public T next() {
            last = wrapped.next();
            return last;
        }

        @Override
        public void remove() {
            wrapped.remove();
            // the line above either throws UnsupportedOperationException or follows with:
            if (last != null) {
                if (last instanceof ReactiveObject) {
                    for (BaseTuple lts : getTuples()) {
                        ((ReactiveObject) last).removeTuple(lts);
                    }
                }
                ReactiveObjectUtil.notifyModification(last, getTuples(), ModificationType.REMOVE);
                last = null;
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReactiveCollection [").append(wrapped).append("]");
        return builder.toString();
    }

    // Reminder: no need to override boolean removeIf(Predicate<? super E> filter)
    // as it uses the collection's Iterator which in this case is already opportunely wrapped.
    
}
