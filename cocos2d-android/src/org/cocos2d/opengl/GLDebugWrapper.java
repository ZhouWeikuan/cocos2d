package org.cocos2d.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.util.Log;


public class GLDebugWrapper implements GLSurfaceView.GLWrapper {
    private static final String LOG_TAG = GLDebugWrapper.class.getSimpleName();

    public GL wrap(GL gl) {
        return new MyGLImplementation((GL10) gl);
    }

    static class MyGLImplementation implements GL, GL10 {

        private GL10 gl;

        MyGLImplementation(GL10 gl) {
            this.gl = gl;
        }

        public void glActiveTexture(int texture) {
            Log.v(LOG_TAG, "glActiveTexture(texture_:" + texture + ")\n");
            gl.glActiveTexture(texture);
        }

        public void glAlphaFunc(int func, float ref) {
            Log.v(LOG_TAG, "glAlphaFunc(func:" + func + ", ref:" + ref + ")\n");
            gl.glAlphaFunc(func, ref);
        }

        public void glAlphaFuncx(int func, int ref) {
            Log.v(LOG_TAG, "glAlphaFuncx(func:" + func + ", ref:" + ref + ")\n");
            gl.glAlphaFuncx(func, ref);
        }

        public void glBindTexture(int target, int texture) {
            Log.v(LOG_TAG, "glBindTexture(" + getBindTextureTargetString(target) + ", " + texture + ")\n");
            gl.glBindTexture(target, texture);
        }

        public void glBlendFunc(int sfactor, int dfactor) {
            Log.v(LOG_TAG, "glBlendFunc(" + getBlendFuncSFactorString(sfactor) + ", " + getBlendFuncDFactorString(dfactor) + ")\n");
            gl.glBlendFunc(sfactor, dfactor);
        }

        public void glClear(int mask) {
            Log.v(LOG_TAG, "glClear(" + getClearMaskString(mask) + ")\n");
            gl.glClear(mask);
        }

        public void glClearColor(float red, float green, float blue, float alpha) {
            Log.v(LOG_TAG, "glClearColor(" + red + "f, " + green + "f, " + blue + "f, " + alpha + "f)\n");
            gl.glClearColor(red, green, blue, alpha);
        }

        public void glClearColorx(int red, int green, int blue, int alpha) {
            Log.v(LOG_TAG, "glClearColorx(" + red + ", " + green + ", " + blue + ", " + alpha + ")\n");
            gl.glClearColorx(red, green, blue, alpha);
        }

        public void glClearDepthf(float depth) {
            Log.v(LOG_TAG, "glClearDepthf(" + depth + "f)\n");
            gl.glClearDepthf(depth);
        }

        public void glClearDepthx(int depth) {
            Log.v(LOG_TAG, "glClearDepthx(" + depth + ")\n");
            gl.glClearDepthx(depth);
        }

        public void glClearStencil(int s) {
            Log.v(LOG_TAG, "glClearStencil(" + s + ")\n");
            gl.glClearStencil(s);
        }

        public void glClientActiveTexture(int texture) {
            Log.v(LOG_TAG, "glClientActiveTexture(texture_:" + texture + ")\n");
            gl.glClientActiveTexture(texture);
        }

        public void glColor4f(float red, float green, float blue, float alpha) {
            Log.v(LOG_TAG, "glColor4f(" + red + "f, " + green + "f, " + blue + "f, " + alpha + "f)\n");
            gl.glColor4f(red, green, blue, alpha);
        }

        public void glColor4x(int red, int green, int blue, int alpha) {
            Log.v(LOG_TAG, "glColor4x(" + red + ", " + green + ", " + blue + ", " + alpha + ")\n");
            gl.glColor4x(red, green, blue, alpha);
        }

        public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
            Log.v(LOG_TAG, "glColorMask(" + getBooleanString(red) + ", " + getBooleanString(green) + ", " + getBooleanString(blue) + ", " + getBooleanString(alpha) + ")\n");
            gl.glColorMask(red, green, blue, alpha);
        }

        public void glColorPointer(int size, int type, int stride, Buffer pointer) {
            Log.v(LOG_TAG, "glColorPointer(" + size + ", " + getColorPointerTypeString(type) + ", " + stride + ", pointer)\n");
            gl.glColorPointer(size, type, stride, pointer);
        }

        public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
            Log.v(LOG_TAG, "glCompressedTexImage2D()\n");
            gl.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
        }

        public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
            Log.v(LOG_TAG, "glCompressedTexSubImage2D()\n");
            gl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
        }

        public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
            Log.v(LOG_TAG, "glCopyTexImage2D()\n");
            gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
        }

        public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
            Log.v(LOG_TAG, "glCopyTexSubImage2D()\n");
            gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
        }

        public void glCullFace(int mode) {
            Log.v(LOG_TAG, "glCullFace(mode:" + getCullFaceModeString(mode) + ")\n");
            gl.glCullFace(mode);
        }

        public void glDeleteTextures(int n, int[] textures, int offset) {
            Log.v(LOG_TAG, "glDeleteTextures()\n");
            gl.glDeleteTextures(n, textures, offset);
        }

        public void glDeleteTextures(int n, IntBuffer textures) {
            Log.v(LOG_TAG, "glDeleteTextures()\n");
            gl.glDeleteTextures(n, textures);
        }

        public void glDepthFunc(int func) {
            Log.v(LOG_TAG, "glDepthFunc(func" + func + ")\n");
            gl.glDepthFunc(func);
        }

        public void glDepthMask(boolean flag) {
            Log.v(LOG_TAG, "glDepthMask()\n");
            gl.glDepthMask(flag);
        }

        public void glDepthRangef(float zNear, float zFar) {
            Log.v(LOG_TAG, "glDepthRangef()\n");
            gl.glDepthRangef(zNear, zFar);
        }

        public void glDepthRangex(int zNear, int zFar) {
            Log.v(LOG_TAG, "glDepthRangex()\n");
            gl.glDepthRangex(zNear, zFar);
        }

        public void glDisable(int cap) {
            Log.v(LOG_TAG, "glDisable(" + getEnableDisableCapString(cap) + ")\n");
            gl.glDisable(cap);
        }

        public void glDisableClientState(int array) {
            Log.v(LOG_TAG, "glDisableClientState(" + getClientStateArrayString(array) + ")\n");
            gl.glDisableClientState(array);
        }

        public void glDrawArrays(int mode, int first, int count) {
            Log.v(LOG_TAG, "glDrawArrays(" + getDrawArraysModeString(mode) + ", " + first + ", " + count + ")\n");
            gl.glDrawArrays(mode, first, count);
        }

        public void glDrawElements(int mode, int count, int type, Buffer indices) {
            Log.v(LOG_TAG, "glDrawElements()\n");
            gl.glDrawElements(mode, count, type, indices);
        }

        public void glEnable(int cap) {
            Log.v(LOG_TAG, "glEnable(" + getEnableDisableCapString(cap) + ")\n");
            gl.glEnable(cap);
        }

        public void glEnableClientState(int array) {
            Log.v(LOG_TAG, "glEnableClientState(" + getClientStateArrayString(array) + ")\n");
            gl.glEnableClientState(array);
        }

        public void glFinish() {
            Log.v(LOG_TAG, "glFinish()\n");
            gl.glFinish();
        }

        public void glFlush() {
            Log.v(LOG_TAG, "glFlush()\n");
            gl.glFlush();
        }

        public void glFogf(int pname, float param) {
            Log.v(LOG_TAG, "glFogf()\n");
            gl.glFogf(pname, param);
        }

        public void glFogfv(int pname, FloatBuffer params) {
            Log.v(LOG_TAG, "glFogfv()\n");
            gl.glFogfv(pname, params);
        }

        public void glFogfv(int pname, float[] params, int offset) {
            Log.v(LOG_TAG, "glFogfv()\n");
            gl.glFogfv(pname, params, offset);
        }

        public void glFogx(int pname, int param) {
            Log.v(LOG_TAG, "glFogx()\n");
            gl.glFogx(pname, param);
        }

        public void glFogxv(int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glFogxv()\n");
            gl.glFogxv(pname, params, offset);
        }

        public void glFogxv(int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glFogxv()\n");
            gl.glFogxv(pname, params);
        }

        public void glFrontFace(int mode) {
            Log.v(LOG_TAG, "glFrontFace(mode:" + mode + ")\n");
            gl.glFrontFace(mode);
        }

        public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
            Log.v(LOG_TAG, "glFrustumf(" + left + "f, " + right + "f, " + bottom + "f, " + top + "f, " + zNear + "f, " + zFar + "f)\n");
            gl.glFrustumf(left, right, bottom, top, zNear, zFar);
        }

        public void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar) {
            Log.v(LOG_TAG, "glFrustumx()\n");
            gl.glFrustumx(left, right, bottom, top, zNear, zFar);
        }

        public void glGenTextures(int n, IntBuffer textures) {
            Log.v(LOG_TAG, "glGenTextures()\n");
            gl.glGenTextures(n, textures);
        }

        public void glGenTextures(int n, int[] textures, int offset) {
            Log.v(LOG_TAG, "glGenTextures(" + n + ", textures[], " + offset + ")\n");
            gl.glGenTextures(n, textures, offset);
        }

        public int glGetError() {
            Log.v(LOG_TAG, "glGetError()\n");
            int err = gl.glGetError();
            return err;
        }

        public void glGetIntegerv(int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glGetIntegerv()\n");
            gl.glGetIntegerv(pname, params, offset);
        }

        public void glGetIntegerv(int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glGetIntegerv()\n");
            gl.glGetIntegerv(pname, params);
        }

        public String glGetString(int name) {
            Log.v(LOG_TAG, "glGetString(" + name + ")\n");
            String string = gl.glGetString(name);
            return string;
        }

        public void glHint(int target, int mode) {
            Log.v(LOG_TAG, "glHint(" + getHintTargetString(target) + ", " + getHintModeString(mode) + ")\n");
            gl.glHint(target, mode);
        }

        public void glLightModelf(int pname, float param) {
            Log.v(LOG_TAG, "glLightModelf()\n");
            gl.glLightModelf(pname, param);
        }

        public void glLightModelfv(int pname, FloatBuffer params) {
            Log.v(LOG_TAG, "glLightModelfv()\n");
            gl.glLightModelfv(pname, params);
        }

        public void glLightModelfv(int pname, float[] params, int offset) {
            Log.v(LOG_TAG, "()\n");
            gl.glLightModelfv(pname, params, offset);
        }

        public void glLightModelx(int pname, int param) {
            Log.v(LOG_TAG, "glLightModelfv()\n");
            gl.glLightModelx(pname, param);
        }

        public void glLightModelxv(int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glLightModelxv()\n");
            gl.glLightModelxv(pname, params);
        }

        public void glLightModelxv(int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glLightModelxv()\n");
            gl.glLightModelxv(pname, params, offset);
        }

        public void glLightf(int light, int pname, float param) {
            Log.v(LOG_TAG, "glLightf()\n");
            gl.glLightf(light, pname, param);
        }

        public void glLightfv(int light, int pname, FloatBuffer params) {
            Log.v(LOG_TAG, "glLightfv()\n");
            gl.glLightfv(light, pname, params);
        }

        public void glLightfv(int light, int pname, float[] params, int offset) {
            Log.v(LOG_TAG, "glLightfv()\n");
            gl.glLightfv(light, pname, params, offset);
        }

        public void glLightx(int light, int pname, int param) {
            Log.v(LOG_TAG, "glLightx()\n");
            gl.glLightx(light, pname, param);
        }

        public void glLightxv(int light, int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glLightxv()\n");
            gl.glLightxv(light, pname, params, offset);
        }

        public void glLightxv(int light, int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glLightxv()\n");
            gl.glLightxv(light, pname, params);
        }

        public void glLineWidth(float width) {
            Log.v(LOG_TAG, "glLineWidth(" + width + "f)\n");
            gl.glLineWidth(width);
        }

        public void glLineWidthx(int width) {
            Log.v(LOG_TAG, "glLineWidthx(" + width + ")\n");
            gl.glLineWidthx(width);
        }

        public void glLoadIdentity() {
            Log.v(LOG_TAG, "glLoadIdentity()\n");
            gl.glLoadIdentity();
        }

        public void glLoadMatrixf(float[] m, int offset) {
            Log.v(LOG_TAG, "glLoadMatrixf()\n");
            gl.glLoadMatrixf(m, offset);
        }

        public void glLoadMatrixf(FloatBuffer m) {
            Log.v(LOG_TAG, "glLoadMatrixf()\n");
            gl.glLoadMatrixf(m);
        }

        public void glLoadMatrixx(int[] m, int offset) {
            Log.v(LOG_TAG, "glLoadMatrixx()\n");
            gl.glLoadMatrixx(m, offset);
        }

        public void glLoadMatrixx(IntBuffer m) {
            Log.v(LOG_TAG, "glLoadMatrixx(m:" + m + ")\n");
            gl.glLoadMatrixx(m);
        }

        public void glLogicOp(int opcode) {
            Log.v(LOG_TAG, "glLogicOp(opcode:" + getLogicOpOpcodeString(opcode) + ")\n");
            gl.glLogicOp(opcode);
        }

        public void glMaterialf(int face, int pname, float param) {
            Log.v(LOG_TAG, "glMaterialf()\n");
            gl.glMaterialf(face, pname, param);
        }

        public void glMaterialfv(int face, int pname, float[] params, int offset) {
            Log.v(LOG_TAG, "glMaterialfv()\n");
            gl.glMaterialfv(face, pname, params, offset);
        }

        public void glMaterialfv(int face, int pname, FloatBuffer params) {
            Log.v(LOG_TAG, "glMaterialfv()\n");
            gl.glMaterialfv(face, pname, params);
        }

        public void glMaterialx(int face, int pname, int param) {
            Log.v(LOG_TAG, "glMaterialx()\n");
            gl.glMaterialx(face, pname, param);
        }

        public void glMaterialxv(int face, int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glMaterialxv()\n");
            gl.glMaterialxv(face, pname, params, offset);
        }

        public void glMaterialxv(int face, int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glMaterialxv()\n");
            gl.glMaterialxv(face, pname, params);
        }

        public void glMatrixMode(int mode) {
            Log.v(LOG_TAG, "glMatrixMode(" + getMatrixModeString(mode) + ")\n");
            gl.glMatrixMode(mode);
        }

        public void glMultMatrixf(FloatBuffer m) {
            Log.v(LOG_TAG, "glMultMatrixf(m:" + m + ")\n");
            gl.glMultMatrixf(m);
        }

        public void glMultMatrixf(float[] m, int offset) {
            Log.v(LOG_TAG, "glMultMatrixf()\n");
            gl.glMultMatrixf(m, offset);
        }

        public void glMultMatrixx(int[] m, int offset) {
            Log.v(LOG_TAG, "glMultMatrixx()\n");
            gl.glMultMatrixx(m, offset);
        }

        public void glMultMatrixx(IntBuffer m) {
            Log.v(LOG_TAG, "glMultMatrixx(m:" + m + ")\n");
            gl.glMultMatrixx(m);
        }

        public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
            Log.v(LOG_TAG, "glMultiTexCoord4f()\n");
            gl.glMultiTexCoord4f(target, s, t, r, q);
        }

        public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
            Log.v(LOG_TAG, "glMultiTexCoord4x()\n");
            gl.glMultiTexCoord4x(target, s, t, r, q);
        }

        public void glNormal3f(float nx, float ny, float nz) {
            Log.v(LOG_TAG, "glNormal3f()\n");
            gl.glNormal3f(nx, ny, nz);
        }

        public void glNormal3x(int nx, int ny, int nz) {
            Log.v(LOG_TAG, "glNormal3x()\n");
            gl.glNormal3x(nx, ny, nz);
        }

        public void glNormalPointer(int type, int stride, Buffer pointer) {
            Log.v(LOG_TAG, "glNormalPointer()\n");
            gl.glNormalPointer(type, stride, pointer);
        }

        public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
            Log.v(LOG_TAG, "glOrthof(" + left + "f, " + right + "f, " + bottom + "f, " + top + "f, " + zNear + "f, " + zFar + "f)\n");
            gl.glOrthof(left, right, bottom, top, zNear, zFar);
        }

        public void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar) {
            Log.v(LOG_TAG, "glOrthox(" + left + ", " + right + ", " + bottom + ", " + top + ", " + zNear + ", " + zFar + ")\n");
            gl.glOrthox(left, right, bottom, top, zNear, zFar);
        }

        public void glPixelStorei(int pname, int param) {
            Log.v(LOG_TAG, "glPixelStorei()\n");
            gl.glPixelStorei(pname, param);
        }

        public void glPointSize(float size) {
            Log.v(LOG_TAG, "glPointSize()\n");
            gl.glPointSize(size);
        }

        public void glPointSizex(int size) {
            Log.v(LOG_TAG, "glPointSizex()\n");
            gl.glPointSizex(size);
        }

        public void glPolygonOffset(float factor, float units) {
            Log.v(LOG_TAG, "glPolygonOffset()\n");
            gl.glPolygonOffset(factor, units);
        }

        public void glPolygonOffsetx(int factor, int units) {
            Log.v(LOG_TAG, "glPolygonOffsetx()\n");
            gl.glPolygonOffsetx(factor, units);
        }

        public void glPopMatrix() {
            Log.v(LOG_TAG, "glPopMatrix()\n");
            gl.glPopMatrix();
        }

        public void glPushMatrix() {
            Log.v(LOG_TAG, "glPushMatrix()\n");
            gl.glPushMatrix();
        }

        public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
            Log.v(LOG_TAG, "glReadPixels()\n");
            gl.glReadPixels(x, y, width, height, format, type, pixels);
        }

        public void glRotatef(float angle, float x, float y, float z) {
            Log.v(LOG_TAG, "glRotatef()\n");
            gl.glRotatef(angle, x, y, z);
        }

        public void glRotatex(int angle, int x, int y, int z) {
            Log.v(LOG_TAG, "glRotatex()\n");
            gl.glRotatex(angle, x, y, z);
        }

        public void glSampleCoverage(float value, boolean invert) {
            Log.v(LOG_TAG, "glSampleCoverage()\n");
            gl.glSampleCoverage(value, invert);
        }

        public void glSampleCoveragex(int value, boolean invert) {
            Log.v(LOG_TAG, "glSampleCoveragex()\n");
            gl.glSampleCoveragex(value, invert);
        }

        public void glScalef(float x, float y, float z) {
            Log.v(LOG_TAG, "glScalef()\n");
            gl.glScalef(x, y, z);
        }

        public void glScalex(int x, int y, int z) {
            Log.v(LOG_TAG, "glScalex()\n");
            gl.glScalex(x, y, z);
        }

        public void glScissor(int x, int y, int width, int height) {
            Log.v(LOG_TAG, "glScissor()\n");
            gl.glScissor(x, y, width, height);
        }

        public void glShadeModel(int mode) {
            Log.v(LOG_TAG, "glShadeModel(" + getShadeModelString(mode) + ")\n");
            gl.glShadeModel(mode);
        }

        public void glStencilFunc(int func, int ref, int mask) {
            Log.v(LOG_TAG, "glStencilFunc()\n");
            gl.glStencilFunc(func, ref, mask);
        }

        public void glStencilMask(int mask) {
            Log.v(LOG_TAG, "glStencilMask()\n");
            gl.glStencilMask(mask);
        }

        public void glStencilOp(int fail, int zfail, int zpass) {
            Log.v(LOG_TAG, "glStencilOp()\n");
            gl.glStencilOp(fail, zfail, zpass);
        }

        public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
            Log.v(LOG_TAG, "glTexCoordPointer(" + size + ", " + getTexCoordPointerTypeString(type) + ", " + stride + ", pointer)\n");
            gl.glTexCoordPointer(size, type, stride, pointer);
        }

        public void glTexEnvf(int target, int pname, float param) {
            Log.v(LOG_TAG, "glTexEnvf(" + getTexEnvTargetString(target) + ", " + getTextEnvNameString(pname) + ", " + getTexEnvParam((int) param) + ")\n");
            gl.glTexEnvf(target, pname, param);
        }

        public void glTexEnvfv(int target, int pname, float[] params, int offset) {
            Log.v(LOG_TAG, "glTexEnvfv()\n");
            gl.glTexEnvfv(target, pname, params, offset);
        }

        public void glTexEnvfv(int target, int pname, FloatBuffer params) {
            Log.v(LOG_TAG, "glTexEnvfv()\n");
            gl.glTexEnvfv(target, pname, params);
        }

        public void glTexEnvx(int target, int pname, int param) {
            Log.v(LOG_TAG, "glTexEnvx(" + getTexEnvTargetString(target) + ", " + getTextEnvNameString(pname) + ", " + getTexEnvParam(param) + ")\n");
            gl.glTexEnvx(target, pname, param);
        }

        public void glTexEnvxv(int target, int pname, int[] params, int offset) {
            Log.v(LOG_TAG, "glTexEnvxv()\n");
            gl.glTexEnvxv(target, pname, params, offset);
        }

        public void glTexEnvxv(int target, int pname, IntBuffer params) {
            Log.v(LOG_TAG, "glTexEnvxv(" + getTextEnvTargetString(target) + ")\n");
            gl.glTexEnvxv(target, pname, params);
        }

        public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
            Log.v(LOG_TAG, "glTexImage2D(" + getTexImage2DTargetString(target) + ", " + level + ", " +
                    getTexImage2DInternalFormatString(internalformat) + ", " +
                    width + ", " + height + ", " + border + ", " +
                    getTexImage2DFormatString(format) + ", " + pixels + ")\n");
            gl.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
        }

        public void glTexParameterf(int target, int pname, float param) {
            Log.v(LOG_TAG, "glTexParameterf(" + getTexParameterTargetString(target) + ", " + getTexParameterNameString(pname) + ", " + getTexParameterParamString((int) param) + ")\n");
            gl.glTexParameterf(target, pname, param);
        }

        public void glTexParameterx(int target, int pname, int param) {
            Log.v(LOG_TAG, "glTexParameterx(" + getTexParameterTargetString(target) + ", " + getTexParameterNameString(pname) + ", " + getTexParameterParamString(param) + ")\n");
            gl.glTexParameterx(target, pname, param);
        }

        public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
            Log.v(LOG_TAG, "glTexSubImage2D()\n");
            gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
        }

        public void glTranslatef(float x, float y, float z) {
            Log.v(LOG_TAG, "glTranslatef(" + x + "f, " + y + "f, " + z + "f)\n");
            gl.glTranslatef(x, y, z);
        }

        public void glTranslatex(int x, int y, int z) {
            Log.v(LOG_TAG, "glTranslatex(" + x + ", " + y + ", " + z + ")\n");
            gl.glTranslatex(x, y, z);
        }

        public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
            Log.v(LOG_TAG, "glVertexPointer(" + size + ", " + getVertexPointerTypeString(type) + ", " + stride + ", pointer" + ")\n");
            gl.glVertexPointer(size, type, stride, pointer);
        }

        public void glViewport(int x, int y, int width, int height) {
            Log.v(LOG_TAG, "glViewport(" + x + ", " + y + ", " + width + ", " + height + ")\n");
            gl.glViewport(x, y, width, height);
        }

        private static String getTexCoordPointerTypeString(int type) {
            switch (type) {
                case GL_SHORT:
                    return "GL_SHORT";
                case GL_FIXED:
                    return "GL_FIXED";
                case GL_FLOAT:
                    return "GL_FLOAT";
                default:
                    return "UNKNOWN";
            }
        }

        private String getShadeModelString(int mode) {
            switch (mode) {
                case GL_FLAT:
                    return "GL_FLAT";
                case GL_SMOOTH:
                    return "GL_SMOOTH";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getBlendFuncSFactorString(int sfactor) {
            switch (sfactor) {
                case GL_ZERO:
                    return "GL_ZERO";
                case GL_ONE:
                    return "GL_ONE";
                case GL_SRC_COLOR:
                    return "GL_SRC_COLOR";
                case GL_ONE_MINUS_SRC_COLOR:
                    return "GL_ONE_MINUS_SRC_COLOR";
                case GL_DST_COLOR:
                    return "GL_DST_COLOR";
                case GL_ONE_MINUS_DST_COLOR:
                    return "GL_ONE_MINUS_DST_COLOR";
                case GL_SRC_ALPHA:
                    return "GL_SRC_ALPHA";
                case GL_ONE_MINUS_SRC_ALPHA:
                    return "GL_ONE_MINUS_SRC_ALPHA";
                case GL_DST_ALPHA:
                    return "GL_DST_ALPHA";
                case GL_ONE_MINUS_DST_ALPHA:
                    return "GL_ONE_MINUS_DST_ALPHA";
                case GL_SRC_ALPHA_SATURATE:
                    return "GL_SRC_ALPHA_SATURATE";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getBlendFuncDFactorString(int dfactor) {
            switch (dfactor) {
                case GL_ZERO:
                    return "GL_ZERO";
                case GL_ONE:
                    return "GL_ONE";
                case GL_SRC_COLOR:
                    return "GL_SRC_COLOR";
                case GL_ONE_MINUS_SRC_COLOR:
                    return "GL_ONE_MINUS_SRC_COLOR";
                case GL_DST_COLOR:
                    return "GL_DST_COLOR";
                case GL_ONE_MINUS_DST_COLOR:
                    return "GL_ONE_MINUS_DST_COLOR";
                case GL_SRC_ALPHA:
                    return "GL_SRC_ALPHA";
                case GL_ONE_MINUS_SRC_ALPHA:
                    return "GL_ONE_MINUS_SRC_ALPHA";
                case GL_DST_ALPHA:
                    return "GL_DST_ALPHA";
                case GL_ONE_MINUS_DST_ALPHA:
                    return "GL_ONE_MINUS_DST_ALPHA";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getVertexPointerTypeString(int type) {
            switch (type) {
                case GL_SHORT:
                    return "GL_SHORT";
                case GL_FLOAT:
                    return "GL_FLOAT";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexParameterTargetString(int target) {
            switch (target) {
                case GL_TEXTURE_2D:
                    return "GL_TEXTURE_2D";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexParameterNameString(int pname) {
            switch (pname) {
                case GL_TEXTURE_MIN_FILTER:
                    return "GL_TEXTURE_MIN_FILTER";
                case GL_TEXTURE_MAG_FILTER:
                    return "GL_TEXTURE_MAG_FILTER";
                case GL_TEXTURE_WRAP_S:
                    return "GL_TEXTURE_WRAP_S";
                case GL_TEXTURE_WRAP_T:
                    return "GL_TEXTURE_WRAP_T";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexParameterParamString(int param) {
            switch (param) {
                case GL_NEAREST:
                    return "GL_NEAREST";
                case GL_REPEAT:
                    return "GL_REPEAT";
                case GL_LINEAR:
                    return "GL_LINEAR";
                case GL_CLAMP_TO_EDGE:
                    return "GL_CLAMP_TO_EDGE";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexEnvTargetString(int target) {
            switch (target) {
                case GL_TEXTURE_ENV:
                    return "GL_TEXTURE_ENV";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTextEnvNameString(int pname) {
            switch (pname) {
                case GL_TEXTURE_ENV_MODE:
                    return "GL_TEXTURE_ENV_MODE";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexEnvParam(int param) {
            switch (param) {
                case GL_ADD:
                    return "GL_ADD";
                case GL_MODULATE:
                    return "GL_MODULATE";
                case GL_DECAL:
                    return "GL_DECAL";
                case GL_BLEND:
                    return "GL_BLEND";
                case GL_REPLACE:
                    return "GL_REPLACE";
                case GL_TEXTURE:
                    return "GL_TEXTURE";
                case GL_SRC_COLOR:
                    return "GL_SRC_COLOR";
                case GL_ONE_MINUS_SRC_COLOR:
                    return "GL_ONE_MINUS_SRC_COLOR";
                case GL_SRC_ALPHA:
                    return "GL_SRC_ALPHA";
                case GL_ONE_MINUS_SRC_ALPHA:
                    return "GL_ONE_MINUS_SRC_ALPHA";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getHintTargetString(int target) {
            switch (target) {
                case GL_FOG_HINT:
                    return "GL_FOG_HINT";
                case GL_LINE_SMOOTH_HINT:
                    return "GL_LINE_SMOOTH_HINT";
                case GL_PERSPECTIVE_CORRECTION_HINT:
                    return "GL_PERSPECTIVE_CORRECTION_HINT";
                case GL_POINT_SMOOTH_HINT:
                    return "GL_POINT_SMOOTH_HINT";
                case GL_POLYGON_SMOOTH_HINT:
                    return "GL_POLYGON_SMOOTH_HINT";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getHintModeString(int target) {
            switch (target) {
                case GL_FASTEST:
                    return "GL_FASTEST";
                case GL_NICEST:
                    return "GL_NICEST";
                case GL_DONT_CARE:
                    return "GL_DONT_CARE";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getMatrixModeString(int mode) {
            switch (mode) {
                case GL_MODELVIEW:
                    return "GL_MODELVIEW";
                case GL_PROJECTION:
                    return "GL_PROJECTION";
                case GL_TEXTURE:
                    return "GL_TEXTURE";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getCullFaceModeString(int mode) {
            switch (mode) {
                case GL_FRONT:
                    return "GL_FRONT";
                case GL_BACK:
                    return "GL_BACK";
                case GL_FRONT_AND_BACK:
                    return "GL_FRONT_AND_BACK";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getClientStateArrayString(int array) {
            switch (array) {
                case GL_COLOR_ARRAY:
                    return "GL_COLOR_ARRAY";
                case GL_NORMAL_ARRAY:
                    return "GL_NORMAL_ARRAY";
                case GL_TEXTURE_COORD_ARRAY:
                    return "GL_TEXTURE_COORD_ARRAY";
                case GL_VERTEX_ARRAY:
                    return "GL_VERTEX_ARRAY";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getDrawArraysModeString(int array) {
            switch (array) {
                case GL_POINTS:
                    return "GL_POINTS";
                case GL_LINE_STRIP:
                    return "GL_LINE_STRIP";
                case GL_LINE_LOOP:
                    return "GL_LINE_LOOP";
                case GL_LINES:
                    return "GL_LINES";
                case GL_TRIANGLE_STRIP:
                    return "GL_TRIANGLE_STRIP";
                case GL_TRIANGLE_FAN:
                    return "GL_TRIANGLE_FAN";
                case GL_TRIANGLES:
                    return "GL_TRIANGLES";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getColorPointerTypeString(int type) {
            switch (type) {
                case GL_BYTE:
                    return "GL_BYTE";
                case GL_UNSIGNED_BYTE:
                    return "GL_UNSIGNED_BYTE";
                case GL_SHORT:
                    return "GL_SHORT";
                case GL_UNSIGNED_SHORT:
                    return "GL_UNSIGNED_SHORT";
                case GL_FLOAT:
                    return "GL_FLOAT";
                case GL_TRIANGLE_FAN:
                    return "GL_TRIANGLE_FAN";
                case GL_TRIANGLES:
                    return "GL_TRIANGLES";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getBooleanString(boolean state) {
            return state ? "GL_TRUE" : "GL_FALSE";
        }

        private static String getClearMaskString(int mask) {
            StringBuilder sb = new StringBuilder();
            if ((mask & GL_COLOR_BUFFER_BIT) != 0) {
                sb = appendWithPrefix(sb, "GL_COLOR_BUFFER_BIT");
            }
            if ((mask & GL_DEPTH_BUFFER_BIT) != 0) {
                sb = appendWithPrefix(sb, "GL_DEPTH_BUFFER_BIT");
            }
            if ((mask & GL_STENCIL_BUFFER_BIT) != 0) {
                sb = appendWithPrefix(sb, "GL_STENCIL_BUFFER_BIT");
            }
            return sb.toString();
        }

        private static String getTextEnvTargetString(int target) {
            switch (target) {
                case GL_TEXTURE_ENV:
                    return "GL_TEXTURE_ENV";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getEnableDisableCapString(int cap) {
            switch (cap) {
                case GL_ALPHA_TEST:
                    return "GL_ALPHA_TEST";
                case GL_BLEND:
                    return "GL_BLEND";
                case GL_COLOR_LOGIC_OP:
                    return "GL_COLOR_LOGIC_OP";
                case GL_COLOR_MATERIAL:
                    return "GL_COLOR_MATERIAL";
                case GL_CULL_FACE:
                    return "GL_CULL_FACE";
                case GL_DEPTH_TEST:
                    return "GL_DEPTH_TEST";
                case GL_DITHER:
                    return "GL_DITHER";
                case GL_FOG:
                    return "GL_FOG";
                case GL_LIGHTING:
                    return "GL_LIGHTING";
                case GL_MULTISAMPLE:
                    return "GL_MULTISAMPLE";
                case GL_NORMALIZE:
                    return "GL_NORMALIZE";
                case GL_POINT_SMOOTH:
                    return "GL_POINT_SMOOTH";
                case GL_RESCALE_NORMAL:
                    return "GL_RESCALE_NORMAL";
                case GL_SAMPLE_ALPHA_TO_COVERAGE:
                    return "GL_SAMPLE_ALPHA_TO_COVERAGE";
                case GL_SAMPLE_ALPHA_TO_ONE:
                    return "GL_SAMPLE_ALPHA_TO_ONE";
                case GL_SCISSOR_TEST:
                    return "GL_SCISSOR_TEST";
                case GL_STENCIL_TEST:
                    return "GL_STENCIL_TEST";
                case GL_TEXTURE_2D:
                    return "GL_TEXTURE_2D";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexImage2DTargetString(int target) {
            switch (target) {
                case GL_TEXTURE_2D:
                    return "GL_TEXTURE_2D";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexImage2DInternalFormatString(int internalformat) {
            switch (internalformat) {
                case GL_ALPHA:
                    return "GL_ALPHA";
                case GL_LUMINANCE:
                    return "GL_LUMINANCE";
                case GL_LUMINANCE_ALPHA:
                    return "GL_LUMINANCE_ALPHA";
                case GL_RGB:
                    return "GL_RGB";
                case GL_RGBA:
                    return "GL_RGBA";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getTexImage2DFormatString(int format) {
            switch (format) {
                case GL_UNSIGNED_BYTE:
                    return "GL_UNSIGNED_BYTE";
                case GL_BYTE:
                    return "GL_BYTE";
                case GL_UNSIGNED_SHORT:
                    return "GL_UNSIGNED_SHORT";
                case GL_SHORT:
                    return "GL_SHORT";
                case GL_FLOAT:
                    return "GL_FLOAT";
                case GL_UNSIGNED_SHORT_5_6_5:
                    return "GL_UNSIGNED_SHORT_5_6_5";
                case GL_UNSIGNED_SHORT_4_4_4_4:
                    return "GL_UNSIGNED_SHORT_4_4_4_4";
                case GL_UNSIGNED_SHORT_5_5_5_1:
                    return "GL_UNSIGNED_SHORT_5_5_5_1";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getBindTextureTargetString(int target) {
            switch (target) {
                case GL_TEXTURE_2D:
                    return "GL_TEXTURE_2D";
                default:
                    return "UNKNOWN";
            }
        }

        private static String getLogicOpOpcodeString(int cap) {
            switch (cap) {
                case GL_CLEAR:
                    return "GL_CLEAR";
                case GL_SET:
                    return "GL_SET";
                case GL_COPY:
                    return "GL_COPY";
                case GL_COPY_INVERTED:
                    return "GL_COPY_INVERTED";
                case GL_NOOP:
                    return "GL_NOOP";
                case GL_INVERT:
                    return "GL_INVERT";
                case GL_AND:
                    return "GL_AND";
                case GL_NAND:
                    return "GL_NAND";
                case GL_OR:
                    return "GL_OR";
                case GL_NOR:
                    return "GL_NOR";
                case GL_XOR:
                    return "GL_XOR";
                case GL_EQUIV:
                    return "GL_EQUIV";
                case GL_AND_REVERSE:
                    return "GL_AND_REVERSE";
                case GL_AND_INVERTED:
                    return "GL_AND_INVERTED";
                case GL_OR_REVERSE:
                    return "GL_OR_REVERSE";
                case GL_OR_INVERTED:
                    return "GL_OR_INVERTED";
                default:
                    return "UNKNOWN";
            }
        }

        private static StringBuilder appendWithPrefix(StringBuilder sb, String s) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(s);
            return sb;
        }
    }
}