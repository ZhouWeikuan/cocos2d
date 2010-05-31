package org.box2d.collision;

import static org.box2d.collision.BBCollision.*;
import org.box2d.collision.shapes.BBCircleShape;
import org.box2d.collision.shapes.BBPolygonShape;
import static org.box2d.common.BBMath.*;
import static org.box2d.common.BBSettings.*;
import org.box2d.common.BBTransform;
import org.box2d.common.BBVec2;

public class BBCollide {


    public static void collideCircles(
            BBManifold manifold,
            final BBCircleShape circle1, final BBTransform xf1,
            final BBCircleShape circle2, final BBTransform xf2) {
        manifold.m_pointCount = 0;

        BBVec2 p1 = mul(xf1, circle1.m_p);
        BBVec2 p2 = mul(xf2, circle2.m_p);

        BBVec2 d = sub(p2, p1);
        float distSqr = dot(d, d);
        float radius = circle1.m_radius + circle2.m_radius;
        if (distSqr > radius * radius) {
            return;
        }

        manifold.m_type = BBManifold.e_circles;
        manifold.m_localPoint = circle1.m_p;
        manifold.m_localPlaneNormal.setZero();
        manifold.m_pointCount = 1;

        manifold.m_points[0].m_localPoint = circle2.m_p;
        manifold.m_points[0].m_id.key(0);
    }

    public static void collidePolygonAndCircle(
            BBManifold manifold,
            final BBPolygonShape polygon, final BBTransform xf1,
            final BBCircleShape circle, final BBTransform xf2) {
        manifold.m_pointCount = 0;

        // Compute circle position in the frame of the polygon.
        BBVec2 c = mul(xf2, circle.m_p);
        BBVec2 cLocal = mulT(xf1, c);

        // Find the min separating edge.
        int normalIndex = 0;
        float separation = -FLT_MAX;
        float radius = polygon.m_radius + circle.m_radius;
        int vertexCount = polygon.m_vertexCount;
        final BBVec2[] vertices = polygon.m_vertices;
        final BBVec2[] normals = polygon.m_normals;

        for (int i = 0; i < vertexCount; ++i) {
            float s = dot(normals[i], sub(cLocal, vertices[i]));

            if (s > radius) {
                // Early out.
                return;
            }

            if (s > separation) {
                separation = s;
                normalIndex = i;
            }
        }

        // Vertices that subtend the incident face.
        int vertIndex1 = normalIndex;
        int vertIndex2 = vertIndex1 + 1 < vertexCount ? vertIndex1 + 1 : 0;
        BBVec2 v1 = vertices[vertIndex1];
        BBVec2 v2 = vertices[vertIndex2];

        // If the center is inside the polygon ...
        if (separation < FLT_EPSILON) {
            manifold.m_pointCount = 1;
            manifold.m_type = BBManifold.e_faceA;
            manifold.m_localPlaneNormal = normals[normalIndex];
            manifold.m_localPoint = mul(0.5f, add(v1, v2));
            manifold.m_points[0].m_localPoint = circle.m_p;
            manifold.m_points[0].m_id.key(0);
            return;
        }

        // Compute barycentric coordinates
        float u1 = dot(sub(cLocal, v1), sub(v2, v1));
        float u2 = dot(sub(cLocal, v2), sub(v1, v2));
        if (u1 <= 0.0f) {
            if (distanceSquared(cLocal, v1) > radius * radius) {
                return;
            }

            manifold.m_pointCount = 1;
            manifold.m_type = BBManifold.e_faceA;
            manifold.m_localPlaneNormal = sub(cLocal, v1);
            manifold.m_localPlaneNormal.normalize();
            manifold.m_localPoint = v1;
            manifold.m_points[0].m_localPoint = circle.m_p;
            manifold.m_points[0].m_id.key(0);
        } else if (u2 <= 0.0f) {
            if (distanceSquared(cLocal, v2) > radius * radius) {
                return;
            }

            manifold.m_pointCount = 1;
            manifold.m_type = BBManifold.e_faceA;
            manifold.m_localPlaneNormal = sub(cLocal, v2);
            manifold.m_localPlaneNormal.normalize();
            manifold.m_localPoint = v2;
            manifold.m_points[0].m_localPoint = circle.m_p;
            manifold.m_points[0].m_id.key(0);
        } else {
            BBVec2 faceCenter = mul(0.5f, add(v1, v2));
            separation = dot(sub(cLocal, faceCenter), normals[vertIndex1]);
            if (separation > radius) {
                return;
            }

            manifold.m_pointCount = 1;
            manifold.m_type = BBManifold.e_faceA;
            manifold.m_localPlaneNormal = normals[vertIndex1];
            manifold.m_localPoint = faceCenter;
            manifold.m_points[0].m_localPoint = circle.m_p;
            manifold.m_points[0].m_id.key(0);
        }
    }

    // Find the separation between poly1 and poly2 for a give edge normal on poly1.
    static float edgeSeparation(final BBPolygonShape poly1, final BBTransform xf1, int edge1,
                                final BBPolygonShape poly2, final BBTransform xf2) {
        int count1 = poly1.m_vertexCount;
        final BBVec2[] vertices1 = poly1.m_vertices;
        final BBVec2[] normals1 = poly1.m_normals;

        int count2 = poly2.m_vertexCount;
        final BBVec2[] vertices2 = poly2.m_vertices;

        assert (0 <= edge1 & edge1 < count1);

        // Convert normal from poly1's frame into poly2's frame.
        BBVec2 normal1World = mul(xf1.R, normals1[edge1]);
        BBVec2 normal1 = mulT(xf2.R, normal1World);

        // Find support vertex on poly2 for -normal.
        int index = 0;
        float minDot = FLT_MAX;

        for (int i = 0; i < count2; ++i) {
            float dot = dot(vertices2[i], normal1);
            if (dot < minDot) {
                minDot = dot;
                index = i;
            }
        }

        BBVec2 v1 = mul(xf1, vertices1[edge1]);
        BBVec2 v2 = mul(xf2, vertices2[index]);
        return dot(sub(v2, v1), normal1World);
    }

    // Find the max separation between poly1 and poly2 using edge normals from poly1.
    public static float findMaxSeparation(int[] edgeIndex,
                                          final BBPolygonShape poly1, final BBTransform xf1,
                                          final BBPolygonShape poly2, final BBTransform xf2) {
        int count1 = poly1.m_vertexCount;
        final BBVec2[] normals1 = poly1.m_normals;

        // Vector pointing from the centroid of poly1 to the centroid of poly2.
        BBVec2 d = mul(xf2, sub(poly2.m_centroid, mul(xf1, poly1.m_centroid)));
        BBVec2 dLocal1 = mulT(xf1.R, d);

        // Find edge normal on poly1 that has the largest projection onto d.
        int edge = 0;
        float maxDot = -FLT_MAX;
        for (int i = 0; i < count1; ++i) {
            float dot = dot(normals1[i], dLocal1);
            if (dot > maxDot) {
                maxDot = dot;
                edge = i;
            }
        }

        // Get the separation for the edge normal.
        float s = edgeSeparation(poly1, xf1, edge, poly2, xf2);

        // Check the separation for the previous edge normal.
        int prevEdge = edge - 1 >= 0 ? edge - 1 : count1 - 1;
        float sPrev = edgeSeparation(poly1, xf1, prevEdge, poly2, xf2);

        // Check the separation for the next edge normal.
        int nextEdge = edge + 1 < count1 ? edge + 1 : 0;
        float sNext = edgeSeparation(poly1, xf1, nextEdge, poly2, xf2);

        // Find the best edge and the search direction.
        int bestEdge;
        float bestSeparation;
        int increment;
        if (sPrev > s & sPrev > sNext) {
            increment = -1;
            bestEdge = prevEdge;
            bestSeparation = sPrev;
        } else if (sNext > s) {
            increment = 1;
            bestEdge = nextEdge;
            bestSeparation = sNext;
        } else {
            edgeIndex[0] = edge;
            return s;
        }

        // Perform a local search for the best edge normal.
        for (; ;) {
            if (increment == -1)
                edge = bestEdge - 1 >= 0 ? bestEdge - 1 : count1 - 1;
            else
                edge = bestEdge + 1 < count1 ? bestEdge + 1 : 0;

            s = edgeSeparation(poly1, xf1, edge, poly2, xf2);

            if (s > bestSeparation) {
                bestEdge = edge;
                bestSeparation = s;
            } else {
                break;
            }
        }

        edgeIndex[0] = bestEdge;
        return bestSeparation;
    }

    public static void findIncidentEdge(BBClipVertex[] c,
                                        final BBPolygonShape poly1, final BBTransform xf1, int edge1,
                                        final BBPolygonShape poly2, final BBTransform xf2) {
        int count1 = poly1.m_vertexCount;
        final BBVec2[] normals1 = poly1.m_normals;

        int count2 = poly2.m_vertexCount;
        final BBVec2[] vertices2 = poly2.m_vertices;
        final BBVec2[] normals2 = poly2.m_normals;

        assert (0 <= edge1 & edge1 < count1);

        // Get the normal of the reference edge in poly2's frame.
        BBVec2 normal1 = mulT(xf2.R, mul(xf1.R, normals1[edge1]));

        // Find the incident edge on poly2.
        int index = 0;
        float minDot = FLT_MAX;
        for (int i = 0; i < count2; ++i) {
            float dot = dot(normal1, normals2[i]);
            if (dot < minDot) {
                minDot = dot;
                index = i;
            }
        }

        // Build the clip vertices for the incident edge.
        int i1 = index;
        int i2 = i1 + 1 < count2 ? i1 + 1 : 0;

        c[0].v = mul(xf2, vertices2[i1]);
        c[0].id.features.referenceEdge = (byte) edge1;
        c[0].id.features.incidentEdge = (byte) i1;
        c[0].id.features.incidentVertex = 0;

        c[1].v = mul(xf2, vertices2[i2]);
        c[1].id.features.referenceEdge = (byte) edge1;
        c[1].id.features.incidentEdge = (byte) i2;
        c[1].id.features.incidentVertex = 1;
    }

    // Find edge normal of max separation on A - return if separating axis is found
    // Find edge normal of max separation on B - return if separation axis is found
    // Choose reference edge as min(minA, minB)
    // Find incident edge
    // Clip

    // The normal points from 1 to 2

    public static void collidePolygons(BBManifold manifold,
                                       final BBPolygonShape polyA, final BBTransform xfA,
                                       final BBPolygonShape polyB, final BBTransform xfB) {
        manifold.m_pointCount = 0;
        float totalRadius = polyA.m_radius + polyB.m_radius;

        int[] edgeA = new int[1];
        float separationA = findMaxSeparation(edgeA, polyA, xfA, polyB, xfB);
        if (separationA > totalRadius)
            return;

        int[] edgeB = new int[1];
        float separationB = findMaxSeparation(edgeB, polyB, xfB, polyA, xfA);
        if (separationB > totalRadius)
            return;

        final BBPolygonShape poly1;    // reference polygon
        final BBPolygonShape poly2;    // incident polygon
        BBTransform xf1, xf2;
        int edge1;        // reference edge
        byte flip;
        final float k_relativeTol = 0.98f;
        final float k_absoluteTol = 0.001f;

        if (separationB > k_relativeTol * separationA + k_absoluteTol) {
            poly1 = polyB;
            poly2 = polyA;
            xf1 = xfB;
            xf2 = xfA;
            edge1 = edgeB[0];
            manifold.m_type = BBManifold.e_faceB;
            flip = 1;
        } else {
            poly1 = polyA;
            poly2 = polyB;
            xf1 = xfA;
            xf2 = xfB;
            edge1 = edgeA[0];
            manifold.m_type = BBManifold.e_faceA;
            flip = 0;
        }

        BBClipVertex[] incidentEdge = new BBClipVertex[2];
        incidentEdge[0] = new BBClipVertex();
        incidentEdge[1] = new BBClipVertex();
        findIncidentEdge(incidentEdge, poly1, xf1, edge1, poly2, xf2);

        int count1 = poly1.m_vertexCount;
        final BBVec2[] vertices1 = poly1.m_vertices;

        BBVec2 v11 = vertices1[edge1];
        BBVec2 v12 = edge1 + 1 < count1 ? vertices1[edge1 + 1] : vertices1[0];

        BBVec2 localTangent = sub(v12, v11);
        localTangent.normalize();

        BBVec2 localNormal = cross(localTangent, 1.0f);
        BBVec2 planePoint = mul(0.5f, add(v11, v12));

        BBVec2 tangent = mul(xf1.R, localTangent);
        BBVec2 normal = cross(tangent, 1.0f);

        v11 = mul(xf1, v11);
        v12 = mul(xf1, v12);

        // Face offset.
        float frontOffset = dot(normal, v11);

        // Side offsets, extended by polytope skin thickness.
        float sideOffset1 = -dot(tangent, v11) + totalRadius;
        float sideOffset2 = dot(tangent, v12) + totalRadius;

        // Clip incident edge against extruded edge1 side edges.
        BBClipVertex[] clipPoints1 = new BBClipVertex[2];
        clipPoints1[0] = new BBClipVertex();
        clipPoints1[1] = new BBClipVertex();
        BBClipVertex[] clipPoints2 = new BBClipVertex[2];
        clipPoints2[0] = new BBClipVertex();
        clipPoints2[1] = new BBClipVertex();
        int np;

        // Clip to box side 1
        np = clipSegmentToLine(clipPoints1, incidentEdge, tangent.neg(), sideOffset1);

        if (np < 2)
            return;

        // Clip to negative box side 1
        np = clipSegmentToLine(clipPoints2, clipPoints1, tangent, sideOffset2);

        if (np < 2) {
            return;
        }

        // Now clipPoints2 contains the clipped points.
        manifold.m_localPlaneNormal = localNormal;
        manifold.m_localPoint = planePoint;

        int pointCount = 0;
        for (int i = 0; i < maxManifoldPoints; ++i) {
            float separation = dot(normal, clipPoints2[i].v) - frontOffset;

            if (separation <= totalRadius) {
                BBManifoldPoint cp = manifold.m_points[pointCount];
                cp.m_localPoint = mulT(xf2, clipPoints2[i].v);
                cp.m_id = clipPoints2[i].id;
                cp.m_id.features.flip = flip;
                ++pointCount;
            }
        }

        manifold.m_pointCount = pointCount;
    }

}
