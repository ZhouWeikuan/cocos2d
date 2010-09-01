package org.cocos2d.opengl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TGA {
    private static final String LOG_TAG = TGA.class.getSimpleName();


    public static class ImageTGA {
        TGAError status;
        int type, pixelDepth;

        /**
         * map width
         */
        public int width;

        /**
         * map height
         */
        public int height;

        /**
         * raw data
         */
        public byte[] imageData;
        int flipped;
    }

    public enum TGAError {
        TGA_OK,
        TGA_ERROR_FILE_OPEN,
        TGA_ERROR_READING_FILE,
        TGA_ERROR_INDEXED_COLOR,
        TGA_ERROR_MEMORY,
        TGA_ERROR_COMPRESSED_FILE,
    }

    private static void loadHeader(InputStream f, ImageTGA info) throws IOException {

        f.read();

        f.read();

        // type must be 2 or 3
        info.type = (byte) f.read();

        f.read();
        f.read();
        f.read();
        f.read();
        f.read();
        f.read();
        f.read();
        f.read();
        f.read();

        info.width = (f.read() & 0xff) | ((f.read() & 0xff) << 8);
        info.height = (f.read() & 0xff) | ((f.read() & 0xff) << 8);

        info.pixelDepth = f.read() & 0xff;


        int garbage = f.read();

        info.flipped = 0;

        if ((garbage & 0x20) != 0) info.flipped = 1;

    }


    private static void loadImageData(InputStream f, ImageTGA info) throws IOException {

        int mode, total, i;
        byte aux;

        // mode equal the number of components for each pixel
        mode = info.pixelDepth / 8;
        // total is the number of unsigned chars we'll have to read
        total = info.height * info.width * mode;

        f.read(info.imageData, 0, total);

        // mode=3 or 4 implies that the image is RGB(A). However TGA
        // stores it as BGR(A) so we'll have to swap R and B.
        if (mode >= 3)
            for (i = 0; i < total; i += mode) {
                aux = info.imageData[i];
                info.imageData[i] = info.imageData[i + 2];
                info.imageData[i + 2] = aux;
            }
    }

    // loads the RLE encoded image pixels. You shouldn't call this function directly
    private static void loadRLEImageData(InputStream f, ImageTGA info) throws IOException {
        int mode, total, i, index = 0;
        byte[] aux = new byte[4];
        int runlength = 0;
        boolean skip = false;
        int flag = 0;

        // mode equal the number of components for each pixel
        mode = info.pixelDepth / 8;
        // total is the number of unsigned chars we'll have to read
        total = info.height * info.width;

        for (i = 0; i < total; i++) {
            // if we have a run length pending, run it
            if (runlength != 0) {
                // we do, update the run length count
                runlength--;
                skip = (flag != 0);
            } else {
                // otherwise, read in the run length token
                if ((runlength = f.read()) == -1)
                    return;

                // see if it's a RLE encoded sequence
                flag = runlength & 0x80;
                if (flag != 0) runlength -= 128;
                skip = false;
            }

            // do we need to skip reading this pixel?
            if (!skip) {
                // no, read in the pixel data
                if (f.read(aux, 0, mode) != mode)
                    return;

                // mode=3 or 4 implies that the image is RGB(A). However TGA
                // stores it as BGR(A) so we'll have to swap R and B.
                if (mode >= 3) {
                    byte tmp;

                    tmp = aux[0];
                    aux[0] = aux[2];
                    aux[2] = tmp;
                }
            }

            // add the pixel to our image
            memcpy(info.imageData, index, aux, 0, mode);

            index += mode;
        }
    }

    private static void flipImage(ImageTGA info) {
        // mode equal the number of components for each pixel
        int mode = info.pixelDepth / 8;
        int rowbytes = info.width * mode;
        byte[] row = new byte[rowbytes];

        for (int y = 0; y < (info.height / 2); y++) {
            memcpy(row, 0, info.imageData, y * rowbytes, rowbytes);
            memcpy(info.imageData, y * rowbytes, info.imageData, (info.height - (y + 1)) * rowbytes, rowbytes);
            memcpy(info.imageData, (info.height - (y + 1)) * rowbytes, row, 0, rowbytes);
        }

        info.flipped = 0;
    }


    private static void memcpy(byte[] dst, int to, byte[] src, int from, int len) {
        System.arraycopy(src, from, dst, to, len);
//        for (int i = 0; i < len; i++) {
//            dst[i + to] = src[i + from];
//        }
    }

    // this is the function to call when we want to load an image
    public static ImageTGA load(InputStream is) throws IOException {
        ImageTGA info;
        int mode, total;

        // allocate memory for the info struct
        info = new ImageTGA();

        BufferedInputStream file;

        try {
            file = new BufferedInputStream(is);
        } catch (Exception e) {
            info.status = TGAError.TGA_ERROR_FILE_OPEN;
            return (info);
        }

        // load the header
        try {
            loadHeader(file, info);
        } catch (Exception e) {
            info.status = TGAError.TGA_ERROR_READING_FILE;
            file.close();
            return info;
        }

        // check if the image is color indexed
        if (info.type == 1) {
            info.status = TGAError.TGA_ERROR_INDEXED_COLOR;
            file.close();
            return info;
        }
        // check for other types (compressed images)
        if ((info.type != 2) && (info.type != 3) && (info.type != 10)) {
            info.status = TGAError.TGA_ERROR_COMPRESSED_FILE;
            file.close();
            return info;
        }

        // mode equals the number of image components
        mode = info.pixelDepth / 8;
        // total is the number of unsigned chars to read
        total = info.height * info.width * mode;
        
        // allocate memory for image pixels
        info.imageData = new byte[total];

        // finally load the image pixels
        try {
            if (info.type == 10)
                loadRLEImageData(file, info);
            else
                loadImageData(file, info);
        } catch (Exception e) {
            info.status = TGAError.TGA_ERROR_READING_FILE;
            file.close();
            return info;
        }
        file.close();
        info.status = TGAError.TGA_OK;

        if (info.flipped != 0) {
            flipImage(info);
            if (info.flipped != 0)
                info.status = TGAError.TGA_ERROR_MEMORY;
        }

        return info;
    }

    // converts RGB to greyscale
    public static void RGBtogreyscale(ImageTGA info) {

        int mode, i, j;

        byte[] newImageData;

        // if the image is already greyscale do nothing
        if (info.pixelDepth == 8)
            return;

        // compute the number of actual components
        mode = info.pixelDepth / 8;

        // allocate an array for the new image data
        newImageData = new byte[info.height * info.width];

        // convert pixels: greyscale = o.30 * R + 0.59 * G + 0.11 * B
        for (i = 0, j = 0; j < info.width * info.height; i += mode, j++)
            newImageData[j] = (byte) (0.30 * info.imageData[i] +
                    0.59 * info.imageData[i + 1] +
                    0.11 * info.imageData[i + 2]);


        //free old image data
        info.imageData = null;

        // reassign pixelDepth and type according to the new image type
        info.pixelDepth = 8;
        info.type = 3;
        // reassing imageData to the new array.
        info.imageData = newImageData;
    }

    // releases the memory used for the image
    public static void destroy(ImageTGA info) {

        if (info != null) {
            if (info.imageData != null)
                info.imageData = null;
        }
    }

}

