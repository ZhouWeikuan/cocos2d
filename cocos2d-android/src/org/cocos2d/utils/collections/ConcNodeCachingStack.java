package org.cocos2d.utils.collections;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * This is simple non-blocking stack implementation using Treiber's algorithm.
 * @author genius
 */
public class ConcNodeCachingStack<T> {

    private static class Node<T> {
        volatile T item;
        volatile Node<T> next;
    }
    
    volatile Node<T> head = null;
    
    @SuppressWarnings("rawtypes")
	private static final
                 AtomicReferenceFieldUpdater<ConcNodeCachingStack, Node>
                 headUpdater =
                 AtomicReferenceFieldUpdater.newUpdater
                 (ConcNodeCachingStack.class, Node.class, "head");
    
	private boolean casHead(Node<T> cmp, Node<T> val) {
		return headUpdater.compareAndSet(this, cmp, val);
	}
	
	/**
	 * Second stack of free nodes for reuse.
	 */
    volatile Node<T> freeNode = null;
    
    @SuppressWarnings("rawtypes")
	private static final
			    AtomicReferenceFieldUpdater<ConcNodeCachingStack, Node>
			    freeNodeUpdater =
			    AtomicReferenceFieldUpdater.newUpdater
			    (ConcNodeCachingStack.class, Node.class, "freeNode");

	private boolean casNewNode(Node<T> cmp, Node<T> val) {
		return freeNodeUpdater.compareAndSet(this, cmp, val);
	}
	
	/**
	 * Get Node from stack, or create new node.
	 * @return new node.
	 */
	private Node<T> newNode() {
        Node<T> ret;
        Node<T> newFree;
        do {
            ret = freeNode;
            if (ret == null) 
                return new Node<T>();
            newFree = ret.next;
        } while ( !casNewNode(ret,newFree) );
        return ret;
	}
	
	/**
	 * Store node for reuse it later.
	 * @param node node for reuse.
	 */
	private void freeNode(Node<T> node) {
		node.item = null;
        
        Node<T> oldNew;
        do {
            oldNew = freeNode;
            node.next = oldNew;
        } while ( !casNewNode(oldNew, node) );
	}
    
	// PUBLIC METHODS
	
    public void push(T item) {
        Node<T> newHead = newNode();
        newHead.item = item;
        
        Node<T> oldHead;
        do {
            oldHead = head;
            newHead.next = oldHead;
        } while ( !casHead(oldHead, newHead) );
    }

    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        do {
            oldHead = head;
            if (oldHead == null) 
                return null;
            newHead = oldHead.next;
        } while ( !casHead(oldHead,newHead) );
        T ret = oldHead.item;
        freeNode(oldHead);
        return ret;
    }
}
