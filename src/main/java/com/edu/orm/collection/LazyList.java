package com.edu.orm.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class LazyList<T> implements List<T> {

    private List<T> internalList;
    private Supplier<List<T>> collectionSupplier;

    public LazyList(Supplier<List<T>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    public List<T> getInternalList() {
        if (internalList == null) {
            internalList = collectionSupplier.get();
        }
        return internalList;
    }

    @Override
    public int size() {
        return getInternalList().size();
    }

    @Override
    public boolean isEmpty() {
        return getInternalList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getInternalList().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getInternalList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getInternalList().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return getInternalList().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getInternalList().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getInternalList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getInternalList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getInternalList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return getInternalList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getInternalList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getInternalList().retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        getInternalList().replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        getInternalList().sort(c);
    }

    @Override
    public void clear() {
        getInternalList().clear();
    }

    @Override
    public T get(int index) {
        return getInternalList().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getInternalList().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getInternalList().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getInternalList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getInternalList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getInternalList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getInternalList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getInternalList().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getInternalList().subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<T> spliterator() {
        return getInternalList().spliterator();
    }

    @Override
    public String toString() {
        return getInternalList().toString();
    }
}
