/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.build.compute;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.definition.value.Source;
import com.bstek.ureport.definition.value.ValueType;
import com.bstek.ureport.definition.value.ZxingValue;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.BindDataListExpressionData;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.data.ObjectListExpressionData;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.model.Image;
import com.bstek.ureport.utils.ToolUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Jacky.gao
 * @since 2017年3月27日
 */
public class ZxingValueCompute implements ValueCompute {
	private static final int BLACK = 0xff000000;  
    private static final int WHITE = 0xFFFFFFFF;
	@Override
	public List<BindData> compute(Cell cell, Context context) {
		List<BindData> list=new ArrayList<BindData>();
		ZxingValue value = (ZxingValue)cell.getValue();
		String format=value.getFormat();
		BarcodeFormat barcodeForamt=BarcodeFormat.QR_CODE;
		if(StringUtils.isNotBlank(format)){
			barcodeForamt=BarcodeFormat.valueOf(format);			
		}
		int w=value.getWidth();
		int h=value.getHeight();
		Source source=value.getSource();
		if(source.equals(Source.text)){
			String data=value.getValue();
			Image image=buildImage(barcodeForamt,data,w,h,value.isCodeDisplay());
			list.add(new BindData(image));
		}else{
			Expression expression=value.getExpression();
			ExpressionData<?> data=expression.execute(cell,cell, context);
			if(data instanceof BindDataListExpressionData){
				BindDataListExpressionData listData=(BindDataListExpressionData)data;
				List<BindData> bindDataList=listData.getData();
				for(BindData bindData:bindDataList){
					Object obj=bindData.getValue();
					if(obj==null)obj="";
					if(!ToolUtils.isEmpty(obj)){
						Image image=buildImage(barcodeForamt,obj.toString(),w,h,value.isCodeDisplay());
						list.add(new BindData(image));
					}
				}
			}else if(data instanceof ObjectExpressionData){
				ObjectExpressionData exprData=(ObjectExpressionData)data;
				Object obj=exprData.getData();
				if(obj==null){
					obj="";
				}else if(obj instanceof String){
					String text=obj.toString();
					if(text.startsWith("\"") && text.endsWith("\"")){
						text=text.substring(1,text.length()-1);
					}
					obj=text;
				}
				if(!ToolUtils.isEmpty(obj)){
					Image image=buildImage(barcodeForamt,obj.toString(),w,h,value.isCodeDisplay());
					list.add(new BindData(image));
				}
			}else if(data instanceof ObjectListExpressionData){
				ObjectListExpressionData listExprData=(ObjectListExpressionData)data;
				List<?> listData=listExprData.getData();
				for(Object obj:listData){
					if(obj==null){
						obj="";
					}else if(obj instanceof String){
						String text=obj.toString();
						if(text.startsWith("\"") && text.endsWith("\"")){
							text=text.substring(1,text.length()-1);
						}
						obj=text;
					}
					if(!ToolUtils.isEmpty(obj)){
						Image image=buildImage(barcodeForamt,obj.toString(),w,h,value.isCodeDisplay());
						list.add(new BindData(image));
					}
				}
			}
		}
		return list;
	}

	/**
	 * 生成条形码
	 * @param format
	 * @param data
	 * @param w
	 * @param h
	 * @return
	 */
	private Image buildImage(BarcodeFormat format,String data,int w,int h, boolean codeDisplay){
        try{
        	Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();  
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN,0);
            if(format.equals(BarcodeFormat.QR_CODE)){
            	hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);            	
            }
            BitMatrix matrix = new MultiFormatWriter().encode(data,format, w, h,hints);
//            int width = matrix.getWidth();
//            int height = matrix.getHeight();
//            BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
//            for (int x = 0; x < width; x++) {
//            	for (int y = 0; y < height; y++) {
//            		image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
//            	}
//            }
//			MatrixToImageWriter.toBufferedImage(matrix);
////            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////            ImageIO.write(image, "png", outputStream);
////            byte[] bytes=outputStream.toByteArray();
////            String base64Data=Base64Utils.encodeToString(bytes);
////            IOUtils.closeQuietly(outputStream);
////            return new Image(base64Data,w,h);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			if(codeDisplay){
				ImageIO.write(insertWords(MatrixToImageWriter.toBufferedImage(matrix),data), "png", outputStream);
				byte[] bytes=outputStream.toByteArray();
				String base64Data=Base64Utils.encodeToString(bytes);
				IOUtils.closeQuietly(outputStream);
				return new Image(base64Data,w,h+50);
			}else{
				ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", outputStream);
				byte[] bytes=outputStream.toByteArray();
				String base64Data=Base64Utils.encodeToString(bytes);
				IOUtils.closeQuietly(outputStream);
				return new Image(base64Data,w,h+50);
			}
        }catch(Exception ex){
        	throw new ReportComputeException(ex);
        }
	}


	/**
	 * 设置 Graphics2D 属性  （抗锯齿）
	 *
	 * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
	 */
	private static void setGraphics2D(Graphics2D g2d) {
		// 消除画图锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// 消除文字锯齿
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		g2d.setStroke(s);
	}

	/**
	 * 设置背景为白色
	 *
	 * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
	 */
	private static void setColorWhite(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		//填充整个屏幕
		g2d.fillRect(0, 0, 302, 113);
		//设置笔刷
		g2d.setColor(Color.BLACK);
	}

	/**
	 * 把带logo的二维码下面加上文字
	 *
	 * @param image 条形码图片
	 * @param words 文字
	 * @return 返回BufferedImage
	 * @author fxbin
	 */
	public static BufferedImage insertWords(BufferedImage image, String words) {
		// 新的图片，把带logo的二维码下面加上文字
		if (StringUtils.isNotEmpty(words)) {
			int height = image.getHeight()+40;
			int width = image.getWidth();

			BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = outImage.createGraphics();
			// 抗锯齿
			setGraphics2D(g2d);
			// 设置白色
			setColorWhite(g2d);

			// 画条形码到新的面板
			g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			// 画文字到新的面板
			Color color = new Color(0, 0, 0);
			g2d.setColor(color);
			// 字体、字型、字号
			g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
			//文字长度
			int strWidth = g2d.getFontMetrics().stringWidth(words);
			//总长度减去文字长度的一半  （居中显示）
			int wordStartX = (image.getWidth() - strWidth) / 2;
			//height + (outImage.getHeight() - height) / 2 + 12
			int wordStartY = image.getHeight() + 20;
			// time 文字长度
//			int timeWidth = 1;
			// rightFirstWords 文字长度
//			int rightFirstWordsWidth = image.getWidth() - g2d.getFontMetrics().stringWidth(RIGHT_FIRST_WORDS);
			// rightSecondWords 文字长度
//			int rightSecondWordsWidth = image.getWidth() - g2d.getFontMetrics().stringWidth(RIGHT_SECOND_WORDS);
			// 画文字
			g2d.drawString(words, wordStartX, wordStartY);
//			g2d.drawString(time, timeWidth, wordStartY + 18);
//			g2d.drawString(RIGHT_FIRST_WORDS, rightFirstWordsWidth, wordStartY + 18);
//			g2d.drawString(RIGHT_SECOND_WORDS, rightSecondWordsWidth, wordStartY + 36);
			g2d.dispose();
			outImage.flush();
			return outImage;
		}
		return null;
	}

	@Override
	public ValueType type() {
		return ValueType.zxing;
	}
}
