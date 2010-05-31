package org.box2d.collision;

import static org.box2d.collision.BBCollision.BBAABB;
import static org.box2d.collision.BBCollision.BBRayCastInput;
import org.box2d.common.BBMath;
import org.box2d.common.BBVec2;
import org.box2d.dynamics.BBContactManager;
import static org.box2d.dynamics.BBWorld.*;

import java.util.Arrays;
import java.util.Comparator;

public class BBBroadPhase extends BBWorldQueryWrapper {

    static class BBPair implements Comparable<BBPair> {

        public int proxyIdA;
        public int proxyIdB;
        public int next;

        public int compareTo(BBPair pair) {
            return pairLessThan(this, pair) ? -1 : 1;
        }

        private static boolean pairLessThan(final BBPair pair1, final BBPair pair2) {
            return pair1.proxyIdA < pair2.proxyIdA || pair1.proxyIdA == pair2.proxyIdA && pair1.proxyIdB < pair2.proxyIdB;
        }
    }


    static class BBPairComparator implements Comparator<BBPair> {

        public int compare(BBPair pair1, BBPair pair2) {
            return pair1.compareTo(pair2);
        }
    }

    public static int e_nullProxy = -1;


    public BBBroadPhase() {
        m_proxyCount = 0;

        m_pairCapacity = 16;
        m_pairCount = 0;
        m_pairBuffer = new BBPair[m_pairCapacity];

        m_moveCapacity = 16;
        m_moveCount = 0;
        m_moveBuffer = new int[m_moveCapacity];
    }

    /// create a proxy with an initial AABB. Pairs are not reported until
    /// updatePairs is called.
    public int createProxy(final BBAABB aabb, Object userData) {
        int proxyId = m_tree.createProxy(aabb, userData);
        ++m_proxyCount;
        bufferMove(proxyId);
        return proxyId;

    }

    /// destroy a proxy. It is up to the client to remove any pairs.
    public void destroyProxy(int proxyId) {
        unBufferMove(proxyId);
        --m_proxyCount;
        m_tree.destroyProxy(proxyId);
    }

    /// Call moveProxy as many times as you like, then when you are done
    /// call updatePairs to finalized the proxy pairs (for your time step).
    public void moveProxy(int proxyId, final BBAABB aabb, final BBVec2 displacement) {
        boolean buffer = m_tree.moveProxy(proxyId, aabb, displacement);
        if (buffer) {
            bufferMove(proxyId);
        }
    }

    /// Get the fat AABB for a proxy.
    public BBAABB getFatAABB(int proxyId) {
        return m_tree.getFatAABB(proxyId);
    }

    /// Get user data from a proxy. Returns null if the id is invalid.
    public Object getUserData(int proxyId) {
        return m_tree.getUserData(proxyId);
    }


    /// Test overlap of fat AABBs.
    public boolean testOverlap(int proxyIdA, int proxyIdB) {
        final BBAABB aabbA = m_tree.getFatAABB(proxyIdA);
        final BBAABB aabbB = m_tree.getFatAABB(proxyIdB);
        return BBCollision.testOverlap(aabbA, aabbB);
    }

    /// Get the number of proxies.
    public int GetProxyCount() {
        return m_proxyCount;
    }


    /// Update the pairs. This results in pair callbacks. This can only add pairs.
    public <T extends BBContactManager> void updatePairs(T callback) {
        // Reset pair buffer
        m_pairCount = 0;

        // Perform tree queries for all moving proxies.
        for (int i = 0; i < m_moveCount; ++i) {
            m_queryProxyId = m_moveBuffer[i];
            if (m_queryProxyId == e_nullProxy) {
                continue;
            }

            // We have to query the tree with the fat AABB so that
            // we don't fail to create a pair that may touch later.
            final BBAABB fatAABB = m_tree.getFatAABB(m_queryProxyId);

            // query tree, create pairs and add them pair buffer.
            m_tree.query(this, fatAABB);
        }

        // Reset move buffer
        m_moveCount = 0;

        // Sort the pair buffer to expose duplicates.
        Arrays.sort(m_pairBuffer, 0, m_pairCount, new BBPairComparator());

        // Send the pairs back to the client.
        int i = 0;
        while (i < m_pairCount) {
            BBPair primaryPair = m_pairBuffer[i];
            Object userDataA = m_tree.getUserData(primaryPair.proxyIdA);
            Object userDataB = m_tree.getUserData(primaryPair.proxyIdB);

            callback.addPair(userDataA, userDataB);
            ++i;

            // Skip any duplicate pairs.
            while (i < m_pairCount) {
                BBPair pair = m_pairBuffer[i];
                if (pair.proxyIdA != primaryPair.proxyIdA || pair.proxyIdB != primaryPair.proxyIdB) {
                    break;
                }
                ++i;
            }
        }
    }


    /// query an AABB for overlapping proxies. The callback class
    /// is called for each proxy that overlaps the supplied AABB.
    public <T extends BBWorldQueryWrapper> void query(T callback, final BBAABB aabb) {
        m_tree.query(callback, aabb);
    }

    /// Ray-cast against the proxies in the tree. This relies on the callback
    /// to perform a exact ray-cast in the case were the proxy contains a shape.
    /// The callback also performs the any collision filtering. This has performance
    /// roughly equal to k * log(n), where k is the number of collisions and n is the
    /// number of proxies in the tree.
    /// @param input the ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
    /// @param callback a callback class that is called for each proxy that is hit by the ray.
    public <T extends BBWorldRayCastWrapper> void rayCast(T callback, final BBRayCastInput input) {
        m_tree.rayCast(callback, input);
    }

    /// Compute the height of the embedded tree.
    public int computeHeight() {
        return m_tree.computeHeight();
    }


    private void bufferMove(int proxyId) {
        if (m_moveCount == m_moveCapacity) {
            int[] oldBuffer = m_moveBuffer;
            m_moveCapacity *= 2;
            m_moveBuffer = new int[m_moveCapacity];
            System.arraycopy(oldBuffer, 0, m_moveBuffer, 0, m_moveCount);
        }

        m_moveBuffer[m_moveCount] = proxyId;
        ++m_moveCount;

    }

    private void unBufferMove(int proxyId) {
        for (int i = 0; i < m_moveCount; ++i) {
            if (m_moveBuffer[i] == proxyId) {
                m_moveBuffer[i] = e_nullProxy;
                return;
            }
        }
    }

    @Override
    public boolean queryCallback(int proxyId) {
        // A proxy cannot form a pair with itself.
        if (proxyId == m_queryProxyId) {
            return true;
        }

        // Grow the pair buffer as needed.
        if (m_pairCount == m_pairCapacity) {
            BBPair[] oldBuffer = m_pairBuffer;
            m_pairCapacity *= 2;
            m_pairBuffer = new BBPair[m_pairCapacity];
            System.arraycopy(oldBuffer, 0, m_pairBuffer, 0, m_pairCount);

        }

        m_pairBuffer[m_pairCount] = new BBPair();
        m_pairBuffer[m_pairCount].proxyIdA = BBMath.min(proxyId, m_queryProxyId);
        m_pairBuffer[m_pairCount].proxyIdB = BBMath.max(proxyId, m_queryProxyId);
        ++m_pairCount;

        return true;

    }

    private BBDynamicTree m_tree = new BBDynamicTree();

    private int m_proxyCount;

    private int[] m_moveBuffer;
    private int m_moveCapacity;
    private int m_moveCount;

    private BBPair[] m_pairBuffer;
    private int m_pairCapacity;
    private int m_pairCount;

    private int m_queryProxyId;

}
