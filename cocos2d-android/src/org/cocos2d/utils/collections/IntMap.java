package org.cocos2d.utils.collections;

import java.util.Iterator;

import org.cocos2d.utils.collections.IntMap.Entry;

/**
 * Taken from http://code.google.com/p/skorpios/
 * 
 * @author Nate 
 */
public final class IntMap<T> implements Iterable<Entry<T>>, Cloneable {

    private Entry<T>[] table;
    private final float loadFactor;
    private int size, mask, capacity, threshold;

    public IntMap() {
        this(16, 0.75f);
    }

    public IntMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    @SuppressWarnings("unchecked")
	public IntMap(int initialCapacity, float loadFactor) {
        if (initialCapacity > 1 << 30){
            throw new IllegalArgumentException("initialCapacity is too large.");
        }
        if (initialCapacity < 0){
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        }
        if (loadFactor <= 0){
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        }
        capacity = 1;
        while (capacity < initialCapacity){
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.mask = capacity - 1;
    }

    @SuppressWarnings("unchecked")
	public IntMap<T> clone(){
        try{
            IntMap<T> clone = (IntMap<T>) super.clone();
            Entry<T>[] newTable = new Entry[table.length];
            for (int i = table.length - 1; i >= 0; i--){
                if (table[i] != null)
                    newTable[i] = table[i].clone();
            }
            clone.table = newTable;
            return clone;
        }catch (CloneNotSupportedException ex){
        }
        return null;
    }

    public boolean containsValue(Object value) {
        Entry<T>[] table = this.table;
        for (int i = table.length; i-- > 0;){
            for (Entry<T> e = table[i]; e != null; e = e.next){
                if (e.value.equals(value)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsKey(int key) {
        int index = ((int) key) & mask;
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key == key){
                return true;
            }
        }
        return false;
    }

    public T get(int key) {
        int index = key & mask;
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key == key){
                return e.value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public T put(int key, T value) {
        int index = key & mask;
        // Check if key already exists.
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key != key){
                continue;
            }
            T oldValue = e.value;
            e.value = value;
            return oldValue;
        }
        table[index] = new Entry<T>(key, value, table[index]);
        if (size++ >= threshold){
            // Rehash.
            int newCapacity = 2 * capacity;
            Entry<T>[] newTable = new Entry[newCapacity];
            Entry<T>[] src = table;
            int bucketmask = newCapacity - 1;
            for (int j = 0; j < src.length; j++){
                Entry<T> e = src[j];
                if (e != null){
                    src[j] = null;
                    do{
                        Entry<T> next = e.next;
                        index = e.key & bucketmask;
                        e.next = newTable[index];
                        newTable[index] = e;
                        e = next;
                    }while (e != null);
                }
            }
            table = newTable;
            capacity = newCapacity;
            threshold = (int) (newCapacity * loadFactor);
            mask = capacity - 1;
        }
        return null;
    }

    public T remove(int key) {
        int index = key & mask;
        Entry<T> prev = table[index];
        Entry<T> e = prev;
        while (e != null){
            Entry<T> next = e.next;
            if (e.key == key){
                size--;
                if (prev == e){
                    table[index] = next;
                }else{
                    prev.next = next;
                }
                return (T) e.value;
            }
            prev = e;
            e = next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        Entry<T>[] table = this.table;
        for (int index = table.length; --index >= 0;){
            table[index] = null;
        }
        size = 0;
    }

    public Iterator<Entry<T>> iterator() {
        return new IntMapIterator();
    }

    final class IntMapIterator implements Iterator<Entry<T>> {

        /**
         * Current entry.
         */
        private Entry<T> cur;

        /**
         * Entry in the table
         */
        private int idx = 0;

        /**
         * Element in the entry
         */
        private int el  = 0;

        public IntMapIterator() {
            cur = table[0];
        }

        public boolean hasNext() {
            return el < size;
        }

        public Entry<T> next() {
            if (el >= size)
                throw new IllegalStateException("No more elements!");

            if (cur != null){
                Entry<T> e = cur;
                cur = cur.next;
                el++;
                return e;
            }
//            if (cur != null && cur.next != null){
                // if we have a current entry, continue to the next entry in the list
//                cur = cur.next;
//                el++;
//                return cur;
//            }

            do {
                // either we exhausted the current entry list, or
                // the entry was null. find another non-null entry.
                cur = table[++idx];
            } while (cur == null);
            Entry<T> e = cur;
            cur = cur.next;
            el ++;
            return e;
        }

        public void remove() {
        }
        
    }
    
    public static final class Entry<T> implements Cloneable {

        final int key;
        T value;
        Entry<T> next;

        Entry(int k, T v, Entry<T> n) {
            key = k;
            value = v;
            next = n;
        }

        public int getKey(){
            return key;
        }

        public T getValue(){
            return value;
        }

        public String toString(){
            return key + " => " + value;
        }

        @SuppressWarnings("unchecked")
		public Entry<T> clone(){
            try{
                Entry<T> clone = (Entry<T>) super.clone();
                clone.next = next != null ? next.clone() : null;
                return clone;
            }catch (CloneNotSupportedException ex){
            }
            return null;
        }
    }
}

