package com.edu.orm.action.queue;

import com.edu.orm.action.Action;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ActionQueue {

    private Queue<Action> actionQueue;

    public boolean add(Action action) {
        return actionQueue.add(action);
    }

    public boolean offer(Action action) {
        return actionQueue.offer(action);
    }

    public Action remove() {
        return actionQueue.remove();
    }

    public Action poll() {
        return actionQueue.poll();
    }

    public Action element() {
        return actionQueue.element();
    }

    public Action peek() {
        return actionQueue.peek();
    }

    public int size() {
        return actionQueue.size();
    }

    public boolean isEmpty() {
        return actionQueue.isEmpty();
    }

    public boolean contains(Object o) {
        return actionQueue.contains(o);
    }

    public Iterator<Action> iterator() {
        return actionQueue.iterator();
    }

    public Object[] toArray() {
        return actionQueue.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return actionQueue.toArray(a);
    }

    public <T> T[] toArray(IntFunction<T[]> generator) {
        return actionQueue.toArray(generator);
    }

    public boolean remove(Object o) {
        return actionQueue.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return actionQueue.containsAll(c);
    }

    public boolean addAll(Collection<? extends Action> c) {
        return actionQueue.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return actionQueue.removeAll(c);
    }

    public boolean removeIf(Predicate<? super Action> filter) {
        return actionQueue.removeIf(filter);
    }

    public boolean retainAll(Collection<?> c) {
        return actionQueue.retainAll(c);
    }

    public void clear() {
        actionQueue.clear();
    }

    @Override
    public boolean equals(Object o) {
        return actionQueue.equals(o);
    }

    @Override
    public int hashCode() {
        return actionQueue.hashCode();
    }

    public Spliterator<Action> spliterator() {
        return actionQueue.spliterator();
    }

    public Stream<Action> stream() {
        return actionQueue.stream();
    }

    public Stream<Action> parallelStream() {
        return actionQueue.parallelStream();
    }

    public void forEach(Consumer<? super Action> action) {
        actionQueue.forEach(action);
    }
}
