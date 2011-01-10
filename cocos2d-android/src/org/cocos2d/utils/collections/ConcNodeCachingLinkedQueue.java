package org.cocos2d.utils.collections;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ConcNodeCachingLinkedQueue<E> {
	
    private static class Node<E> {
        volatile E item;
        volatile Node<E> next;

        @SuppressWarnings("rawtypes")
		private static final
            AtomicReferenceFieldUpdater<Node, Node>
            nextUpdater =
            AtomicReferenceFieldUpdater.newUpdater
            (Node.class, Node.class, "next");

        boolean casNext(Node<E> cmp, Node<E> val) {
            return nextUpdater.compareAndSet(this, cmp, val);
        }
                                                                   
    }                                                                 

    @SuppressWarnings("rawtypes")
	private static final
	    AtomicReferenceFieldUpdater<ConcNodeCachingLinkedQueue, Node>
	    tailUpdater =
	    AtomicReferenceFieldUpdater.newUpdater
	    (ConcNodeCachingLinkedQueue.class, Node.class, "tail");
	@SuppressWarnings("rawtypes")
	private static final
	    AtomicReferenceFieldUpdater<ConcNodeCachingLinkedQueue, Node>
	    headUpdater =
	    AtomicReferenceFieldUpdater.newUpdater
	    (ConcNodeCachingLinkedQueue.class,  Node.class, "head");
	
	private boolean casTail(Node<E> cmp, Node<E> val) {
	    return tailUpdater.compareAndSet(this, cmp, val);
	}
	
	private boolean casHead(Node<E> cmp, Node<E> val) {
	    return headUpdater.compareAndSet(this, cmp, val);
	}


	/**
	 * Pointer to header node, initialized to a dummy node.  The first
	 * actual node is at head.getNext().
	 */
	private volatile Node<E> head = new Node<E>();
	
	/** Pointer to last node on list **/
	private volatile Node<E> tail = head;  
	
	
	/**
	 * Stack of free nodes for reuse.
	 */
    volatile Node<E> freeNode = null;
    
    @SuppressWarnings("rawtypes")
	private static final
			    AtomicReferenceFieldUpdater<ConcNodeCachingLinkedQueue, Node>
			    freeNodeUpdater =
			    AtomicReferenceFieldUpdater.newUpdater
			    (ConcNodeCachingLinkedQueue.class, Node.class, "freeNode");

	private boolean casNewNode(Node<E> cmp, Node<E> val) {
		return freeNodeUpdater.compareAndSet(this, cmp, val);
	}
	
	/**
	 * Get Node from stack, or create new node.
	 * @return new node.
	 */
	private Node<E> newNode() {
        Node<E> ret;
        Node<E> newFree;
        do {
            ret = freeNode;
            if (ret == null) 
                return new Node<E>();
            newFree = ret.next;
        } while ( !casNewNode(ret,newFree) );
        return ret;
	}
	
	/**
	 * Store node for reuse it later.
	 * @param node node for reuse.
	 */
	private void freeNode(Node<E> node) {
		node.item = null;
        
        Node<E> oldNew;
        do {
            oldNew = freeNode;
            node.next = oldNew;
        } while ( !casNewNode(oldNew, node) );
	}

    /**
     * Inserts the specified element at the tail of this queue.
     */
    public void push(E e) {
        Node<E> n = newNode();
        n.item = e;
        n.next = null;
        
        while(true) {
            Node<E> t = tail;
            Node<E> s = t.next;
            if (t == tail) {
                if (s == null) {
                    if (t.casNext(s, n)) {
                        casTail(t, n);
                        return;
                    }
                } else {
                    casTail(t, s);
                }
            }
        }
    }

    public E poll() {
    	while(true) {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.next;
            if (h == head) {
                if (h == t) {
                    if (first == null)
                        return null;
                    else
                        casTail(t, first);
                } else if (casHead(h, first)) {
                	freeNode(h);
                    E item = first.item;
                    if (item != null) {
                        first.item = null;
                        return item;
                    }
                }
            }
        }
    }                                                                
}
