package org.box2d.collision;

import static org.box2d.collision.BBCollision.*;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.aabbExtension;
import static org.box2d.common.BBSettings.aabbMultiplier;
import org.box2d.common.BBVec2;
import static org.box2d.dynamics.BBWorld.BBWorldQueryWrapper;
import static org.box2d.dynamics.BBWorld.BBWorldRayCastWrapper;


public class BBDynamicTree {

    private static final int nullNode = -1;

    public static class BBDynamicTreeNode {
        public boolean IsLeaf() {
            return child1 == nullNode;
        }

        /// This is the fattened AABB.
        BBAABB aabb = new BBAABB();

        //int userData;
        Object userData;

        //        union
        int parent;
        int next;

        int child1;
        int child2;
    }

/// A dynamic tree arranges data in a binary tree to accelerate
/// queries such as volume queries and ray casts. Leafs are proxies
/// with an AABB. In the tree we expand the proxy AABB by fatAABBFactor
/// so that the proxy AABB is bigger than the client object. This allows the client
/// object to move by small amounts without triggering a tree update.
///
/// Nodes are pooled and relocatable, so we use node indices rather than pointers.

    /// Constructing the tree initializes the node pool.

    public BBDynamicTree() {
        m_root = nullNode;

        m_nodeCapacity = 16;
        m_nodeCount = 0;
        m_nodes = new BBDynamicTreeNode[m_nodeCapacity];

        // Build a linked list for the free list.
        for (int i = 0; i < m_nodeCapacity - 1; ++i) {
            m_nodes[i] = new BBDynamicTreeNode();
            m_nodes[i].next = i + 1;
        }
        m_nodes[m_nodeCapacity - 1] = new BBDynamicTreeNode();
        m_nodes[m_nodeCapacity - 1].next = nullNode;
        m_freeList = 0;

        // m_path = 0;

        m_insertionCount = 0;
    }

    /// create a proxy. Provide a tight fitting AABB and a userData pointer.
    public int createProxy(final BBAABB aabb, Object userData) {
        int proxyId = allocateNode();

        // Fatten the aabb.
        BBVec2 r = new BBVec2(aabbExtension, aabbExtension);
        m_nodes[proxyId].aabb.lowerBound = sub(aabb.lowerBound, r);
        m_nodes[proxyId].aabb.upperBound = add(aabb.upperBound, r);
        m_nodes[proxyId].userData = userData;

        insertLeaf(proxyId);

        return proxyId;
    }

    /// destroy a proxy. This asserts if the id is invalid.
    public void destroyProxy(int proxyId) {
        assert (0 <= proxyId && proxyId < m_nodeCapacity);
        assert (m_nodes[proxyId].IsLeaf());

        removeLeaf(proxyId);
        freeNode(proxyId);
    }

    /// Move a proxy with a swepted AABB. If the proxy has moved outside of its fattened AABB,
    /// then the proxy is removed from the tree and re-inserted. Otherwise
    /// the function returns immediately.
    /// @return true if the proxy was re-inserted.
    boolean moveProxy(int proxyId, final BBAABB aabb, final BBVec2 displacement) {
        assert (0 <= proxyId && proxyId < m_nodeCapacity);

        assert (m_nodes[proxyId].IsLeaf());

        if (m_nodes[proxyId].aabb.contains(aabb)) {
            return false;
        }

        removeLeaf(proxyId);

        // Extend AABB.
        BBAABB b = aabb;
        BBVec2 r = new BBVec2(aabbExtension, aabbExtension);
        b.lowerBound = sub(b.lowerBound, r);
        b.upperBound = add(b.upperBound, r);

        // Predict AABB displacement.
        BBVec2 d = mul(aabbMultiplier, displacement);

        if (d.x < 0.0f) {
            b.lowerBound.x += d.x;
        } else {
            b.upperBound.x += d.x;
        }

        if (d.y < 0.0f) {
            b.lowerBound.y += d.y;
        } else {
            b.upperBound.y += d.y;
        }

        m_nodes[proxyId].aabb = b;

        insertLeaf(proxyId);
        return true;
    }

    /// Perform some iterations to re-balance the tree.
    void rebalance(int iterations) {
//        if (m_root == nullNode)
//        {
//            return;
//        }
//
//        for (int i = 0; i < iterations; ++i)
//        {
//            int node = m_root;
//
//            int bit = 0;
//            while (!m_nodes[node].IsLeaf())
//            {
//                node children = m_nodes[node].child1;
//                node = children[(m_path >> bit) & 1];
//                bit = (bit + 1) & (8 * 4 - 1);
//            }
//            ++m_path;
//
//            removeLeaf(node);
//            insertLeaf(node);
//        }
    }

    /// Get proxy user data.
    /// @return the proxy user data or 0 if the id is invalid.
    Object getUserData(int proxyId) {
        assert (0 <= proxyId & proxyId < m_nodeCapacity);
        return m_nodes[proxyId].userData;
    }

    /// Get the fat AABB for a proxy.
    final BBAABB getFatAABB(int proxyId) {
        assert (0 <= proxyId & proxyId < m_nodeCapacity);
        return m_nodes[proxyId].aabb;
    }

    /// Compute the height of the tree.
    int computeHeight() {
        return computeHeight(m_root);
    }

    /// query an AABB for overlapping proxies. The callback class
    /// is called for each proxy that overlaps the supplied AABB.
    <T extends BBWorldQueryWrapper> void query(T callback, final BBAABB aabb) {
        final int k_stackSize = 128;
        int[] stack = new int[k_stackSize];

        int count = 0;
        stack[count++] = m_root;

        while (count > 0) {
            int nodeId = stack[--count];
            if (nodeId == nullNode) {
                continue;
            }

            final BBDynamicTreeNode node = m_nodes[nodeId];

            if (testOverlap(node.aabb, aabb)) {
                if (node.IsLeaf()) {
                    boolean proceed = callback.queryCallback(nodeId);
                    if (!proceed) {
                        return;
                    }
                } else {
                    assert (count + 1 < k_stackSize);
                    stack[count++] = node.child1;
                    stack[count++] = node.child2;
                }
            }
        }
    }

    /// Ray-cast against the proxies in the tree. This relies on the callback
    /// to perform a exact ray-cast in the case were the proxy contains a shape.
    /// The callback also performs the any collision filtering. This has performance
    /// roughly equal to k * log(n), where k is the number of collisions and n is the
    /// number of proxies in the tree.
    /// @param input the ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
    /// @param callback a callback class that is called for each proxy that is hit by the ray.
    <T extends BBWorldRayCastWrapper> void rayCast(T callback, final BBRayCastInput input) {
        BBVec2 p1 = input.p1;
        BBVec2 p2 = input.p2;
        BBVec2 r = sub(p2, p1);
        assert (r.lengthSquared() > 0.0f);
        r.normalize();

        // v is perpendicular to the segment.
        BBVec2 v = cross(1.0f, r);
        BBVec2 abs_v = abs(v);

        // Separating axis for segment (Gino, p80).
        // |dot(v, p1 - c)| > dot(|v|, h)

        float maxFraction = input.maxFraction;

        // Build a bounding box for the segment.
        BBAABB segmentAABB = new BBAABB();

        {
            BBVec2 t = add(p1, mul(maxFraction, sub(p2, p1)));
            segmentAABB.lowerBound = min(p1, t);
            segmentAABB.upperBound = max(p1, t);
        }

        final int k_stackSize = 128;
        int[] stack = new int[k_stackSize];

        int count = 0;
        stack[count++] = m_root;

        while (count > 0) {
            int nodeId = stack[--count];
            if (nodeId == nullNode) {
                continue;
            }

            final BBDynamicTreeNode node = m_nodes[nodeId];

            if (!testOverlap(node.aabb, segmentAABB)) {
                continue;
            }

            // Separating axis for segment (Gino, p80).
            // |dot(v, p1 - c)| > dot(|v|, h)
            BBVec2 c = node.aabb.getCenter();
            BBVec2 h = node.aabb.getExtents();
            float separation = abs(dot(v, sub(p1, c))) - dot(abs_v, h);
            if (separation > 0.0f) {
                continue;
            }

            if (node.IsLeaf()) {
                BBRayCastInput subInput = new BBRayCastInput();
                subInput.p1 = input.p1;
                subInput.p2 = input.p2;
                subInput.maxFraction = maxFraction;

                maxFraction = callback.RayCastCallback(subInput, nodeId);

                if (maxFraction == 0.0f) {
                    return;
                }

                // Update segment bounding box.
                {
                    BBVec2 t = add(p1, mul(maxFraction, sub(p2, p1)));
                    segmentAABB.lowerBound = min(p1, t);
                    segmentAABB.upperBound = max(p1, t);
                }
            } else {
                assert (count + 1 < k_stackSize);
                stack[count++] = node.child1;
                stack[count++] = node.child2;
            }
        }
    }

    private int allocateNode() {
        // Expand the node pool as needed.
        if (m_freeList == nullNode) {
            assert (m_nodeCount == m_nodeCapacity);

            // The free list is empty. Rebuild a bigger pool.
            BBDynamicTreeNode[] oldNodes = m_nodes;
            m_nodeCapacity *= 2;
            m_nodes = new BBDynamicTreeNode[m_nodeCapacity];
            System.arraycopy(oldNodes, 0, m_nodes, 0, m_nodeCount);

            // Build a linked list for the free list. The parent
            // pointer becomes the "next" pointer.
            for (int i = m_nodeCount; i < m_nodeCapacity - 1; ++i) {
                m_nodes[i] = new BBDynamicTreeNode();
                m_nodes[i].next = i + 1;
            }
            m_nodes[m_nodeCapacity - 1] = new BBDynamicTreeNode();
            m_nodes[m_nodeCapacity - 1].next = nullNode;
            m_freeList = m_nodeCount;
        }

        // Peel a node off the free list.
        int nodeId = m_freeList;
        m_freeList = m_nodes[nodeId].next;
        m_nodes[nodeId].parent = nullNode;
        m_nodes[nodeId].child1 = nullNode;
        m_nodes[nodeId].child2 = nullNode;
        ++m_nodeCount;
        return nodeId;
    }

    private void freeNode(int nodeId) {
        assert (0 <= nodeId && nodeId < m_nodeCapacity);
        assert (0 < m_nodeCount);
        m_nodes[nodeId].next = m_freeList;
        m_freeList = nodeId;
        --m_nodeCount;
    }

    private void insertLeaf(int leaf) {
        ++m_insertionCount;

        if (m_root == nullNode) {
            m_root = leaf;
            m_nodes[m_root].parent = nullNode;
            return;
        }

        // Find the best sibling for this node.
        BBVec2 center = m_nodes[leaf].aabb.getCenter();
        int sibling = m_root;
        if (!m_nodes[sibling].IsLeaf()) {
            do {
                int child1 = m_nodes[sibling].child1;
                int child2 = m_nodes[sibling].child2;

                BBVec2 delta1 = abs(sub(m_nodes[child1].aabb.getCenter(), center));
                BBVec2 delta2 = abs(sub(m_nodes[child2].aabb.getCenter(), center));

                float norm1 = delta1.x + delta1.y;
                float norm2 = delta2.x + delta2.y;

                if (norm1 < norm2) {
                    sibling = child1;
                } else {
                    sibling = child2;
                }

            }
            while (!m_nodes[sibling].IsLeaf());
        }

        // create a parent for the siblings.
        int node1 = m_nodes[sibling].parent;
        int node2 = allocateNode();
        m_nodes[node2].parent = node1;
        m_nodes[node2].userData = null;
        m_nodes[node2].aabb.combine(m_nodes[leaf].aabb, m_nodes[sibling].aabb);

        if (node1 != nullNode) {
            if (m_nodes[m_nodes[sibling].parent].child1 == sibling) {
                m_nodes[node1].child1 = node2;
            } else {
                m_nodes[node1].child2 = node2;
            }

            m_nodes[node2].child1 = sibling;
            m_nodes[node2].child2 = leaf;
            m_nodes[sibling].parent = node2;
            m_nodes[leaf].parent = node2;

            do {
                if (m_nodes[node1].aabb.contains(m_nodes[node2].aabb)) {
                    break;
                }

                m_nodes[node1].aabb.combine(m_nodes[m_nodes[node1].child1].aabb, m_nodes[m_nodes[node1].child2].aabb);
                node2 = node1;
                node1 = m_nodes[node1].parent;
            }
            while (node1 != nullNode);
        } else {
            m_nodes[node2].child1 = sibling;
            m_nodes[node2].child2 = leaf;
            m_nodes[sibling].parent = node2;
            m_nodes[leaf].parent = node2;
            m_root = node2;
        }
    }

    private void removeLeaf(int leaf) {
        if (leaf == m_root) {
            m_root = nullNode;
            return;
        }

        int node2 = m_nodes[leaf].parent;
        int node1 = m_nodes[node2].parent;
        int sibling;
        if (m_nodes[node2].child1 == leaf) {
            sibling = m_nodes[node2].child2;
        } else {
            sibling = m_nodes[node2].child1;
        }

        if (node1 != nullNode) {
            // destroy node2 and connect node1 to sibling.
            if (m_nodes[node1].child1 == node2) {
                m_nodes[node1].child1 = sibling;
            } else {
                m_nodes[node1].child2 = sibling;
            }
            m_nodes[sibling].parent = node1;
            freeNode(node2);

            // Adjust ancestor bounds.
            while (node1 != nullNode) {
                BBAABB oldAABB = m_nodes[node1].aabb;
                m_nodes[node1].aabb.combine(m_nodes[m_nodes[node1].child1].aabb, m_nodes[m_nodes[node1].child2].aabb);

                if (oldAABB.contains(m_nodes[node1].aabb)) {
                    break;
                }

                node1 = m_nodes[node1].parent;
            }
        } else {
            m_root = sibling;
            m_nodes[sibling].parent = nullNode;
            freeNode(node2);
        }
    }

    private int computeHeight(int nodeId) {
        if (nodeId == nullNode) {
            return 0;
        }

        assert (0 <= nodeId && nodeId < m_nodeCapacity);
        BBDynamicTreeNode node = m_nodes[nodeId];
        int height1 = computeHeight(node.child1);
        int height2 = computeHeight(node.child2);
        return 1 + max(height1, height2);
    }

    private int m_root;

    private BBDynamicTreeNode[] m_nodes;
    private int m_nodeCount;
    private int m_nodeCapacity;

    private int m_freeList;

    /// This is used incrementally traverse the tree for re-balancing.
    // private int m_path;
    private int m_insertionCount;

}
