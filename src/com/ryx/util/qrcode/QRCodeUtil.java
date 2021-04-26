package com.ryx.util.qrcode;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ryx.dao.HibernateSessionFactory;

/**
 * 二维码工具类
 * 
 */
public class QRCodeUtil {

	private static final String CHARSET = "utf-8";
	// 宽度
	private static final int WIDTH = 880;
	// 高度
	private static final int HEIGHT = 1380;

	// 二维码尺寸
	private static final int QRCODE_SIZE = 512;
	// 图标宽度
	private static final int LOGOWIDTH = 100;
	// 图标高度
	private static final int LOGOHEIGHT = 100;

	public static BufferedImage createImage(String content, String imgPath,
			boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
						: 0xFFFFFFFF);
			}
		}
		if (imgPath == null || "".equals(imgPath)) {
			return image;
		}
		// 插入图片
		QRCodeUtil.insertImage(image, imgPath, needCompress);
		return image;
	}

	/**
	 * 插入LOGO
	 * 
	 * @param source
	 *            二维码图片
	 * @param imgPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source, String imgPath,
			boolean needCompress) throws Exception {
		File file = new File(imgPath);
		if (!file.exists()) {
			System.err.println("" + imgPath + "   该文件不存在！");
			return;
		}
		Image src = ImageIO.read(new File(imgPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > LOGOWIDTH) {
				width = LOGOWIDTH;
			}
			if (height > LOGOHEIGHT) {
				height = LOGOHEIGHT;
			}
			Image image = src.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

//	// 名片分享加背景
//	public static void addBackGroundCard(BufferedImage image, String backimg,
//			String outimg) {
//		try {
//			// 切面圆角
//			int width = image.getWidth();
//			int height = image.getHeight();
//			int radius = (image.getWidth() + image.getHeight()) / 30;
//			BufferedImage srcImage = new BufferedImage(width, height,
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2d = srcImage.createGraphics();
//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);
//			g2d.fillRoundRect(0, 0, width, height, radius, radius);
//			g2d.setComposite(AlphaComposite.SrcIn);
//			g2d.drawImage(image, 0, 0, width, height, null);
//
//			BufferedImage bg = ImageIO.read(new File(backimg));// 获取背景图片
//			Graphics2D g = bg.createGraphics();
//			g.drawImage(srcImage, 100, 100, WIDTH, HEIGHT, null);
//			g.dispose();
//			bg.flush();
//			image.flush();
//			ImageIO.write(bg, "jpg", new File(outimg));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	// 加背景
	public static void addBackGround(BufferedImage image, String backimg,
			String outimg ,String userName , String prodUuid) {
		int left = 40;
		int top = 1100;
		
		try {
			Session session = HibernateSessionFactory.getSession();
			String sql = " select left_size,top_size,bg_path from c##db_rongyixin.T_RYX_TGHB_PROPERTIES t where t.prod_uuid=? ";
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, prodUuid);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, String>> list = query.list();
			if (list != null && list.size() > 0) {
				Map map = (Map) list.get(0);
				left = Integer.parseInt((String)map.get("LEFT_SIZE"));
				top = Integer.parseInt((String)map.get("TOP_SIZE"));;
				backimg += map.get("BG_PATH");;
			} else {
				backimg += "back/tghb_back.png";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateSessionFactory.closeSession();
		}
		
		try {
				BufferedImage bg = ImageIO.read(new File(backimg));// 获取背景图片
				Graphics2D g = bg.createGraphics();
				int width = 185;
				int height = 185;
				
//				if("paxyd".equals(prodUuid)||"xwkd".equals(prodUuid)){
//					left = 480;
//					top = 1095;
//				}
//				if("fsd".equals(prodUuid)){
//					left = 480;
//					top = 1100;
//				}
//				if("fjd".equals(prodUuid)){
//					left = 530;
//					top = 1100;
//				} 
//				
//				if("ghd".equals(prodUuid) || "snwsd".equals(prodUuid)){
//					left = 450;
//					top = 1050;
//				} 
//				
//				if("wsd".equals(prodUuid)){
//					left = 480;
//					top = 1070;
//				}
				
				g.drawImage(image, left, top, width, height, null);
				g.dispose();
				bg.flush();
				image.flush();
				ImageIO.write(bg, "jpg", new File(outimg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 加背景
		public static void addBackGround(BufferedImage image, String backimg,
			String outimg, String userName) {
		try {
			BufferedImage bg = ImageIO.read(new File(backimg));// 获取背景图片
			Graphics2D g = bg.createGraphics();
			int width = 185;
			int height = 185;
			if (userName != null && !"".equals(userName) && !"null".equals(userName)) {
				Font font = new Font("黑体", Font.PLAIN, 30);
				g.setFont(font);
				g.drawString(userName, 40, 1078);
			}
			g.drawImage(image, 40, 1100, width, height, null);
			g.dispose();
			bg.flush();
			image.flush();
			ImageIO.write(bg, "jpg", new File(outimg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
