package de.micromata.merlin.server.logging;

import java.util.ArrayList;
import java.util.Iterator;

public class FiFoBuffer<T> {
    private ArrayList<T> list;
    private int maxSize;

    public FiFoBuffer(int maxSize) {
        this.maxSize = maxSize;
        list = new ArrayList<>(maxSize);
    }

    public void add(T element) {
        synchronized (list) {
            if (list.size() >= maxSize) {
                Iterator<T> it = list.iterator();
                it.next();
                it.remove();
            }
            list.add(element);
        }
    }

    public T get(int index) {
        synchronized (list) {
            if (index <= 0 || index >= list.size()) {
                return null;
            }
            return list.get(index);
        }
    }

    public int getSize() {
        return list.size();
    }
}
