/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.exception.VyiyunException;

/**
 * 图像工具类
 * 
 * @author tf
 * 
 * @date 2015年6月29日 上午10:41:30
 * @version 1.0
 */
public class ImageUtil {

	private static ImageUtil instance;

	public static ImageUtil getInstance() {
		if (null == instance) {
			synchronized (ImageUtil.class) {
				if (null == instance) {
					instance = new ImageUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 图片缩放处理类
	 * 
	 * @param sourceFile
	 *            源图片文件
	 * @param targetFile
	 *            缩放后存储的文件
	 * @throws Exception
	 */
	public void zoomImage(File sourceFile, File targetFile) throws Exception {
		int width = Integer.valueOf(ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, "image_zomm_width", "150"));
		int height = Integer.valueOf(ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, "image_zomm_height", "150"));
		double ratio = 0.1;

		List<String> imageTypes = ConfigUtil.getStringArray(Constants.VYIYUN_CONFIG_PATH, "image_type", "png");
		String suffix = sourceFile.getName().substring(sourceFile.getName().lastIndexOf('.') + 1);
		if (imageTypes.contains(suffix)) {
			BufferedImage ufferedImage = ImageIO.read(sourceFile);
			Image image = ufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			if ((ufferedImage.getHeight() > width) || (ufferedImage.getWidth() > height)) {
				if (ufferedImage.getHeight() > ufferedImage.getWidth())
					ratio = (double) width / ufferedImage.getHeight();
				else
					ratio = (double) height / ufferedImage.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			image = op.filter(ufferedImage, null);
			ImageIO.write((BufferedImage) image, suffix, targetFile);

		} else {
			throw new VyiyunException("1001000", new String[] { suffix });
		}

	}
}
