package com.monstrous.pixels.world.ECS;

import com.badlogic.gdx.utils.reflect.ArrayReflection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.lang.Integer.max;

/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 *
 * @param <T>
 *		object type this bag holds
 *
 * Based on version from Arni Arent
 */

public class Bag<T> implements Iterable<T> {

    protected T[] data;
    protected int size;
    private BagIterator it;

    public Bag() {
        this( 64);
    }

    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        data = (T[]) ArrayReflection.newInstance(Object.class, capacity);
        size = 0;
    }


    public T get(int index) {
        return data[index];
    }

    public void remove(int index){
        if(index >= size)
            return;
        T t = data[index];
        data[index] = data[size-1];
        data[size-1] = null;
        size--;
    }

    public void add(T e){
        if(size == data.length)
            grow(2*data.length);

        data[size++] = e;
    }

    public void unsafeSet(int index, T e){
        data[index] = e;
    }

    public void set(int index, T e) {
        if(index >= data.length)
            grow(max((2 * data.length), index + 1));

        size = Math.max(size, index + 1);
        unsafeSet(index, e);
    }

    public int getSize(){
        return size;
    }

    public boolean empty(){
        return size == 0;
    }

    public boolean notEmpty(){
        return size > 0;
    }

    private void grow(int newCapacity) {
        data = Arrays.copyOf(data, newCapacity);
    }

    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        if (it == null) it = new BagIterator();

        it.validCursorPos = false;
        it.cursor = 0;

        return it;
    }


    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }

    /**
     * An Iterator for Bag.
     *
     * @see java.util.Iterator
     */
    private final class BagIterator implements Iterator<T> {

        /** Current position. */
        private int cursor;
        /** True if the current position is within bounds. */
        private boolean validCursorPos;


        @Override
        public boolean hasNext() {
            return (cursor < size);
        }


        @Override
        public T next() throws NoSuchElementException {
            if (cursor == size) {
                throw new NoSuchElementException("Iterated past last element");
            }

            T e = data[cursor++];
            validCursorPos = true;
            return e;
        }


//        @Override
//        public void remove() throws IllegalStateException {
//            if (!validCursorPos) {
//                throw new IllegalStateException();
//            }
//
//            validCursorPos = false;
//            Bag.this.remove(--cursor);
//        }
    }
}
