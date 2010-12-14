package org.cocos2d.grid;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCTexture2D;
/** FBO class that grabs the the contents of the screen */
public class CCGrabber {
	int		fbo[] = new int[1];
	int		oldFBO[] = new int[1];

	public CCGrabber() {
		// generate FBO
		if (CCDirector.gl instanceof GL11ExtensionPack) {
			GL11ExtensionPack gl = (GL11ExtensionPack)CCDirector.gl;
			try {
				gl.glGenFramebuffersOES(1, fbo, 0);
			} catch (Exception e) {

			}
		}
	}

	public void grab(CCTexture2D texture){
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
			return;
		}
		GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
		gl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO, 0);

		try {
			// bind
			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo[0]);

			// associate texture with FBO
			gl.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
					GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, 
					GL10.GL_TEXTURE_2D, texture.name(), 0);

			// check if it worked (probably worth doing :) )
			int status = gl.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
			if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
				return;
				// throw new Exception("Frame Grabber: Could not attach texture to framebuffer");
			}

			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO[0]);
		} catch (Exception e) {

		}
	}

	public void beforeRender(CCTexture2D texture) {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
			return;
		}
		GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
		gl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO, 0);
		try {
			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, fbo[0]);

			// BUG XXX: doesn't work with RGB565.
			((GL10)gl).glClearColor(0,0,0,0);

			// BUG #631: To fix #631, uncomment the lines with #631
			// Warning: But it CCGrabber won't work with 2 effects at the same time
			//	glClearColor(0.0f,0.0f,0.0f,1.0f);	// #631

			((GL10)gl).glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	

			//	glColorMask(TRUE, TRUE, TRUE, FALSE);	// #631
		} catch (Exception e) {

		}
	}

	public void afterRender(CCTexture2D texture) {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
			return;
		}
		GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
		try {
			gl.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO[0]);
		} catch (Exception e) {

		}
		//	glColorMask(TRUE, TRUE, TRUE, TRUE);	// #631
	}

	@Override
	public void finalize() throws Throwable  {
		if (!(CCDirector.gl instanceof GL11ExtensionPack)) {
        } else {
            GL11ExtensionPack gl = (GL11ExtensionPack) CCDirector.gl;
            ccMacros.CCLOGINFO("cocos2d: deallocing %@", this.toString());
            try {
                gl.glDeleteFramebuffersOES(1, fbo, 0);
            } catch (Exception e) {

            }
        }
        super.finalize();
	}
}

