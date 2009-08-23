/*
 * Created on 27.04.2004 by Chris
 *  
 */
package de.d3web.utilities;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Graphics
 * 
 * This is a utility class for image manipulation
 * 
 * @author Chris 27.04.2004
 */
public abstract class Graphics {
	public static BufferedImage createThumbnail(
		BufferedImage tmpImage,
		int thumbWidth,
		int thumbHeight,
		boolean keepRatio) {
		BufferedImage image = null;
		if (tmpImage != null) {
			double thumbRatio = (double) thumbWidth / (double) thumbHeight;

			int imageWidth = tmpImage.getWidth(null);
			int imageHeight = tmpImage.getHeight(null);

			if (keepRatio) {
				double imageRatio = (double) imageWidth / (double) imageHeight;
				if (thumbRatio < imageRatio) {
					thumbHeight = (int) (thumbWidth / imageRatio);
				} else {
					thumbWidth = (int) (thumbHeight * imageRatio);
				}
			}
			// draw original image to thumbnail image object and
			// scale it to the new size on-the-fly
			image =
				new BufferedImage(
					thumbWidth,
					thumbHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = image.createGraphics();
			graphics2D.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.drawImage(tmpImage, 0, 0, thumbWidth, thumbHeight, null);
			return image;
		}
		return null;
	}
}
