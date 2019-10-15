/**
 * 
 */
package com.yishuifengxiao.common.validation.code.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.validation.entity.ImageCode;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;

/**
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class ImageCodeGenerator implements CodeGenerator {
	/**
	 * 默认验证码的长度
	 */
	private final static int DEFAULT_LENGTH = 4;
	/**
	 * x方向上默认的内边距
	 */
	private final static int DEFAULT_X_PADDING = 6;

	/**
	 * y方向上默认的内边距
	 */
	private final static int DEFAULT_Y_PADDING = 8;
	private CodeProperties codeProperties;

	@Override
	public ImageCode generate(ServletWebRequest servletWebRequest) {
		// 图形验证码的宽度
		int width = ServletRequestUtils.getIntParameter(servletWebRequest.getRequest(), "width",
				codeProperties.getImage().getWidth());
		// 图形验证码的高度
		int height = ServletRequestUtils.getIntParameter(servletWebRequest.getRequest(), "height",
				codeProperties.getImage().getHeight());
		// 图形验证码的长度
		int length = codeProperties.getImage().getLength() > 0 ? codeProperties.getImage().getLength() : DEFAULT_LENGTH;
		// 生成一个图片对象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		Random random = new Random();

		g.setColor(codeProperties.getImage().getFringe() ? getRandColor(200, 250) : Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setFont(new Font("Times New Roman", Font.ITALIC, 23));
		// 生成干扰条纹
		if (codeProperties.getImage().getFringe()) {
			g.setColor(getRandColor(160, 200));
			for (int i = 0; i < 155; i++) {
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				int xl = random.nextInt(12);
				int yl = random.nextInt(12);
				g.drawLine(x, y, x + xl, y + yl);
			}
		}

		// 生成四位的随机数
		String sRand = "";

		for (int i = 0; i < length; i++) {

			int xCoordinate = ((width - DEFAULT_X_PADDING) / length) * i + DEFAULT_X_PADDING;
			int yCoordinate = height - DEFAULT_Y_PADDING;
			//绘制文字
			String rand = generate(xCoordinate, yCoordinate, codeProperties.getImage().isContainLetter(),
					codeProperties.getImage().isContainNumber(), g);
			sRand += rand;

		}

		g.dispose();
		return new ImageCode(codeProperties.getImage().getExpireIn(), sRand, image);
	}

	/**
	 * 根据要求生成字符并将字符绘制到底片上
	 * 
	 * @param xCoordinate     绘制时的x坐标
	 * @param yCoordinate     绘制时的y坐标
	 * @param isContainLetter 是否包含字母
	 * @param isContainNumber 是否包含数字
	 * @param g
	 * @return 绘制到底片上的文字
	 */
	private String generate(int xCoordinate, int yCoordinate, boolean isContainLetter, boolean isContainNumber,
			Graphics g) {
		Random random = new Random();
		String rand = "";
		if (isContainLetter && !isContainNumber) {
			// 只包含字母
			rand = RandomStringUtils.random(1, false, false);
		} else if (isContainNumber && !isContainLetter) {
			// 只包含数字
			rand = RandomUtils.nextInt(0, 10) + "";
		} else {
			rand = RandomStringUtils.random(1, false, true);
		}
		// 判断是否出现空白字符
		rand = StringUtils.isNotBlank(rand) ? rand : random.nextInt(10) + "";
		// 字符的颜色随机
		g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString(rand, xCoordinate, yCoordinate);
		return rand;
	}

	/**
	 * 生成随机背景条纹
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public ImageCodeGenerator(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public ImageCodeGenerator() {

	}

}
