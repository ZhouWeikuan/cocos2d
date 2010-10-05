package org.cocos2d.opengl;

import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.utils.BufferProvider;

public class CCPVRTexture {
	private static final int PVR_TEXTURE_FLAG_TYPE_MASK	= 0x0ff;
	private static final char[] gPVRTexIdentifier = "PVR!".toCharArray();

	private static final int kPVRTextureFlagTypePVRTC_2 = 24;
	private static final int kPVRTextureFlagTypePVRTC_4 = 25;

	static class PVRTexHeader {
		ByteBuffer bb;
		public PVRTexHeader(ByteBuffer b) {
			bb = b;
			bb.rewind();
			bb.order(ByteOrder.LITTLE_ENDIAN);
		}
		
		public int headerLength() {
			return bb.getInt(0 * 4);
		}
		
		public int height() {
			return bb.getInt(1 * 4);
		}
		
		public int width() {
			return bb.getInt(2 * 4);
		}
		
		public int numMipmaps() {
			return bb.getInt(3 * 4);
		}
		
		public int flags() {
			return bb.getInt(4 * 4);
		}
		
		public int dataLength() {
			return bb.getInt(5 * 4);
		}
		
		public int bpp() {
			return bb.getInt(6 * 4);
		}
		
		public int bitmaskRed() {
			return bb.getInt(7 * 4);
		}
		
		public int bitmaskGreen() {
			return bb.getInt(8 * 4);
		}
		
		public int bitmaskBlue() {
			return bb.getInt(9 * 4);
		}
		
		public int bitmaskAlpha() {
			return bb.getInt(10 * 4);
		}
		
		public int pvrTag() {
			return bb.getInt(11 * 4);
		}
		
		public int numSurfs() {
			return bb.getInt(12 * 4);
		}
		
		public static final int SIZE = 13 * 4;
	}
	
	ArrayList<Buffer> _imageData;
	
	int _name[];	
	
	int _width;
	public int getWidth() {
		return _width;
	}
	public void setWidth(int width) {
		_width = width;
	}
	
	int _height;
	public int getHeight() {
		return _height;
	}
	public void setHeight(int height) {
		_height = height;
	}
	
	int _internalFormat;
	public int getInternalFormat() {
		return _internalFormat;
	}
	public void setInternalFormat(int internalFormat) {
		_internalFormat = internalFormat;
	}
	
	boolean _hasAlpha;
	public boolean hasAlpha() {
		return _hasAlpha;
	}
	public void setHasAlpha(boolean hasAlpha) {
		_hasAlpha = hasAlpha;
	}
		
	// cocos2d integration
	boolean _retainName;
	public boolean getRetainName(){
		return _retainName;
	}
	public void setRatainName(boolean retainName) {
		_retainName = retainName;
	}
	
	

	private boolean unpackPVRData(ByteBuffer data) {
		boolean success = false;
		PVRTexHeader header = null;
		int flags, pvrTag;
		int dataLength = 0, dataOffset = 0, dataSize = 0;
		int blockSize = 0, widthBlocks = 0, heightBlocks = 0;
		int width = 0, height = 0, bpp = 4;
		int formatFlags;
		
		header = new PVRTexHeader(data);
		data.rewind();
		data.order(ByteOrder.LITTLE_ENDIAN);		
		// pvrTag = CFSwapInt32LittleToHost(header->pvrTag);
		pvrTag = header.pvrTag();
		
		if ((int)gPVRTexIdentifier[0] != ((pvrTag >>  0) & 0xff) ||
			(int)gPVRTexIdentifier[1] != ((pvrTag >>  8) & 0xff) ||
			(int)gPVRTexIdentifier[2] != ((pvrTag >> 16) & 0xff) ||
			(int)gPVRTexIdentifier[3] != ((pvrTag >> 24) & 0xff))
		{
			return false;
		}
		
		// flags = CFSwapInt32LittleToHost(header->flags);
		flags = header.flags();
		formatFlags = flags & PVR_TEXTURE_FLAG_TYPE_MASK;
		
		if (formatFlags == kPVRTextureFlagTypePVRTC_4 || formatFlags == kPVRTextureFlagTypePVRTC_2)
		{
			_imageData.clear();
			
			if (formatFlags == kPVRTextureFlagTypePVRTC_4)
				_internalFormat = GL10.GL_COMPRESSED_TEXTURE_FORMATS; // GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG;
			else if (formatFlags == kPVRTextureFlagTypePVRTC_2)
				_internalFormat = GL10.GL_COMPRESSED_TEXTURE_FORMATS; // GL_COMPRESSED_RGBA_PVRTC_2BPPV1_IMG;
		
			// _width = width = CFSwapInt32LittleToHost(header->width);
			_width = width = header.width();
			// _height = height = CFSwapInt32LittleToHost(header->height);
			_height = height = header.height();
			
			if ( header.bitmaskAlpha() != 0 /*CFSwapInt32LittleToHost(header->bitmaskAlpha)*/ )
				_hasAlpha = true;
			else
				_hasAlpha = false;
			
			dataLength = header.dataLength(); // CFSwapInt32LittleToHost(header->dataLength);
			
			// bytes = ((uint8_t *)[data bytes]) + sizeof(PVRTexHeader);
			
			// Calculate the data size for each texture level and respect the minimum number of blocks
			while (dataOffset < dataLength)
			{
				if (formatFlags == kPVRTextureFlagTypePVRTC_4)
				{
					blockSize = 4 * 4; // Pixel by pixel block size for 4bpp
					widthBlocks = width / 4;
					heightBlocks = height / 4;
					bpp = 4;
				}
				else
				{
					blockSize = 8 * 4; // Pixel by pixel block size for 2bpp
					widthBlocks = width / 8;
					heightBlocks = height / 4;
					bpp = 2;
				}
				
				// Clamp to minimum number of blocks
				if (widthBlocks < 2)
					widthBlocks = 2;
				if (heightBlocks < 2)
					heightBlocks = 2;

				dataSize = widthBlocks * heightBlocks * ((blockSize  * bpp) / 8);
				ByteBuffer bb = BufferProvider.allocateDirect(dataSize);
				bb.put(data.array(), dataOffset + PVRTexHeader.SIZE, dataSize);
				
				// [_imageData addObject:[NSData dataWithBytes:bytes+dataOffset length:dataSize]];
				_imageData.add(bb);
				
				dataOffset += dataSize;
				
				width = Math.max(width >> 1, 1);
				height = Math.max(height >> 1, 1);
			}
					  
			success = true;
		}
		
		return success;
	}


	public boolean createGLTexture() {
		int width = _width;
		int height = _height;
		Buffer data;
		int err;
		
		GL10 gl = CCDirector.gl;
		if ( _imageData != null && !_imageData.isEmpty())	{
			if (_name != null)
				gl.glDeleteTextures(1, _name, 0);
			
			gl.glGenTextures(1, _name, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, _name[0]);
		}

		for (int i=0; i < _imageData.size(); i++) {
			data = _imageData.get(i);
			gl.glCompressedTexImage2D(GL10.GL_TEXTURE_2D, i, _internalFormat, width, height, 0, data.capacity(), data);
						
			err = gl.glGetError();
			if (err != GL10.GL_NO_ERROR) {
				String str = String.format("Error uploading compressed texture level: %d. glError: 0x%04X", i, err);
				ccMacros.CCLOG("CCPVRTexture", str);
				return false;
			}
			
			width = Math.max(width >> 1, 1);
			height = Math.max(height >> 1, 1);
		}
		
		_imageData.clear();
		
		return true;
	}


	protected CCPVRTexture(String path) {
		super();
		
		ByteBuffer data = BufferProvider.bufferFromFile(path);
		_imageData = new ArrayList<Buffer>(10);

		_name = new int[1];
		_width = _height = 0;
		_internalFormat = GL10.GL_COMPRESSED_TEXTURE_FORMATS; // GL_COMPRESSED_RGBA_PVRTC_4BPPV1_IMG;
		_hasAlpha = false;

		_retainName = false; // cocos2d integration

		if (data == null || ! unpackPVRData(data) || !createGLTexture() ) {
			ccMacros.CCLOG("CCPVRTexture", "Can't create texture from path: " + path);
		}
	}

	public static CCPVRTexture pvrTexture(String path) {
		return new CCPVRTexture(path); 
	}

	public static CCPVRTexture pvrTexture(URL url) {
		if (url.getFile().equals("")) {
			return null;
		}
		
		return CCPVRTexture.pvrTexture(url.getPath());
	}


	@Override
	public void finalize() throws Throwable	{
		// CCLOGINFO( @"cocos2d: deallocing %@", self);

		_imageData = null;
		
		if (_name != null && ! _retainName ) {
			CCDirector.gl.glDeleteTextures(1, _name, 0);
		}
		
		super.finalize();
	}

}
