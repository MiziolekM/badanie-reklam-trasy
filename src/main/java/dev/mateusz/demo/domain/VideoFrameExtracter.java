package dev.mateusz.demo.domain;

import org.jcodec.api.FrameGrab8Bit;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.RgbToBgr8Bit;
import org.jcodec.scale.Transform8Bit;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Klasa służy do wycinania klatki z pliku 
 */
public class VideoFrameExtracter {

	public File createThumbnailFromVideo(File file, double sec) throws IOException, JCodecException {
		Picture8Bit frame = FrameGrab8Bit.getFrameAtSec(file, sec);
		File outputfile = new File("tempframe" + sec + ".png");
		ImageIO.write(toBufferedImage8Bit(frame), "png", outputfile);
		return outputfile;
	}

	private BufferedImage toBufferedImage8Bit(Picture8Bit src) {
		if (src.getColor() != ColorSpace.RGB) {
			Transform8Bit transform = ColorUtil.getTransform8Bit(src.getColor(), ColorSpace.RGB);
			if (transform == null) {
				throw new IllegalArgumentException("Unsupported input colorspace: " + src.getColor());
			}
			Picture8Bit out = Picture8Bit.create(src.getWidth(), src.getHeight(), ColorSpace.RGB);
			transform.transform(src, out);
			new RgbToBgr8Bit().transform(out, out);
			src = out;
		}
		BufferedImage dst = new BufferedImage(src.getCroppedWidth(), src.getCroppedHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		if (src.getCrop() == null)
			toBufferedImage8Bit2(src, dst);
		else
			toBufferedImageCropped8Bit(src, dst);
		return dst;
	}

	private void toBufferedImage8Bit2(Picture8Bit src, BufferedImage dst) {
		byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
		byte[] srcData = src.getPlaneData(0);
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (srcData[i] + 128);
		}
	}

	private static void toBufferedImageCropped8Bit(Picture8Bit src, BufferedImage dst) {
		byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
		byte[] srcData = src.getPlaneData(0);
		int dstStride = dst.getWidth() * 3;
		int srcStride = src.getWidth() * 3;
		for (int line = 0, srcOff = 0, dstOff = 0; line < dst.getHeight(); line++) {
			for (int id = dstOff, is = srcOff; id < dstOff + dstStride; id += 3, is += 3) {
				data[id] = (byte) (srcData[is] + 128);
				data[id + 1] = (byte) (srcData[is + 1] + 128);
				data[id + 2] = (byte) (srcData[is + 2] + 128);
			}
			srcOff += srcStride;
			dstOff += dstStride;
		}
	}
}