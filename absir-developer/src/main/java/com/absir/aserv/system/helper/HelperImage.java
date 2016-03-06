/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-6-13 下午6:05:09
 */
package com.absir.aserv.system.helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.absir.core.helper.HelperFile;

/**
 * @author absir
 * 
 */
public class HelperImage {

	/**
	 * @param pressFile
	 * @param targetFile
	 * @param x
	 * @param y
	 * @param formatName
	 * @param destFile
	 */
	public final static void pressImage(File pressFile, File targetFile, int x, int y, String formatName, File destFile) throws Exception {
		Image targetImage = ImageIO.read(targetFile);
		int width = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(targetImage, 0, 0, width, height, null);
		Image pressImage = ImageIO.read(pressFile);
		int pressWidth = pressImage.getWidth(null);
		int pressHeight = pressImage.getHeight(null);
		graphics.drawImage(pressImage, (width - pressWidth) / 2, (height - pressHeight) / 2, pressWidth, pressHeight, null);
		graphics.dispose();
		ImageIO.write(image, formatName, HelperFile.openOutputStream(destFile));
	}

	/**
	 * @param pressText
	 * @param targetFile
	 * @param color
	 * @param font
	 * @param x
	 * @param y
	 * @param formatName
	 * @param destFile
	 * @throws Exception
	 */
	public static void pressText(String pressText, File targetFile, Color color, Font font, int x, int y, String formatName, File destFile) throws Exception {
		Image targetImage = ImageIO.read(targetFile);
		int width = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(targetImage, 0, 0, width, height, null);
		if (color != null) {
			graphics.setColor(color);
		}

		if (font != null) {
			graphics.setFont(font);
		}

		int fontSize = graphics.getFont().getSize();
		graphics.drawString(pressText, width - fontSize - x, height - fontSize / 2 - y);
		graphics.dispose();
		ImageIO.write(image, formatName, HelperFile.openOutputStream(destFile));
	}

}
