package com.kafeidev.main;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GREWordListFormat {
	private static final String[] titles = { "flag", "word_en", "word_cn",
			"word_sound" };

	public static void main(String args[]) throws java.io.IOException,
			java.io.FileNotFoundException {
		System.out.println("hello world---begin");
		try {
			// ==================init xlsx
			Workbook wb;

			if (args.length > 0 && args[0].equals("-xls"))
				wb = new HSSFWorkbook();
			else
				wb = new XSSFWorkbook();

			Map<String, CellStyle> styles = createStyles(wb);

			Sheet sheet = wb.createSheet("Business Plan");

			// turn off gridlines
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);

			// the following three statements are required only for HSSF
			sheet.setAutobreaks(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);

			// the header row: centered text in 48pt font
			Row headerRow = sheet.createRow(0);
			headerRow.setHeightInPoints(12.75f);
			for (int i = 0; i < titles.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(titles[i]);
				cell.setCellStyle(styles.get("header"));
			}
			// freeze the first row
			sheet.createFreezePane(0, 1);

			Row row;
			Cell cell;
			int rownum = 1;
			// ================== read file

			// Get the DOM Builder Factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			// Get the DOM Builder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Load and Parse the XML document
			// document contains the complete XML as a Tree.
			Document document = builder
					.parse(ClassLoader
							.getSystemResourceAsStream("com/kafeidev/main/GRE-Wrod-7000.xml"));

			// List<Word> wordList = new ArrayList<Word>();

			// Iterating through the nodes and extracting the data.
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {

				// We have encountered an <employee> tag.
				Node node = nodeList.item(i);// item
				if (node instanceof Element) {
					Word word = new Word();
					// emp.wordCN =
					// node.getAttributes().getNamedItem("id").getNodeValue();

					NodeList childNodes = node.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node cNode = childNodes.item(j);// word,trans,tags...

						// Identifying the child tag of employee encountered.
						if (cNode instanceof Element) {
							String content = cNode.getLastChild()
									.getTextContent().trim();
							// System.out.println("content:" + content);
							String name = cNode.getNodeName();
							// System.out.println("name:"+name);
							if (name.equals("word")) {
								word.wordEN = cNode.getTextContent();
								// System.out.println("---word:"+cNode.getTextContent());
							} else if (name.equals("phonetic")) {
								// System.out.println("---phonetic:"+cNode.getTextContent());
								word.wordSound = cNode.getTextContent();
							} else if (name.equals("trans")) {
								// System.out.println("---trans:"+cNode.getTextContent());
								word.wordCN = cNode.getTextContent();
							}

						}
					}

					System.out.println("word:\n" + word);
					// wordList.add(word);
					// ==============write to the xlsx
					row = sheet.createRow(i+1);
					{// 0
						cell = row.createCell(0);
						String styleName;
						styleName = "cell_normal_centered";
						cell.setCellValue("");
						cell.setCellStyle(styles.get(styleName));
					}
					{// 1
						cell = row.createCell(1);
						String styleName;
						styleName = "cell_normal_centered";
						cell.setCellValue(word.wordEN);
						cell.setCellStyle(styles.get(styleName));
					}
					{// 2
						cell = row.createCell(2);
						String styleName;
						styleName = "cell_normal";
						cell.setCellValue(word.wordCN);
						cell.setCellStyle(styles.get(styleName));
					}
					{// 3
						cell = row.createCell(3);
						String styleName;
						styleName = "cell_normal_centered";
						cell.setCellValue(word.wordSound);
						cell.setCellStyle(styles.get(styleName));
					}
					row.setHeightInPoints( 200.0f);
					sheet.setColumnWidth(0, 256*10);
			        sheet.setColumnWidth(1, 256*80);
			        sheet.setColumnWidth(2, 256*80);
			        sheet.setColumnWidth(3, 256*20);
			        sheet.setZoom(2, 1);
					
				}

//				if (i>100) {
//					break;
//				}
			}
			   // Write the output to a file
			Calendar cal = Calendar.getInstance();
	    	cal.getTime();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	    	System.out.println( sdf.format(cal.getTime()) );
	    	
	        String file = "gre"+sdf.format(cal.getTime())+".xls";
	        if(wb instanceof XSSFWorkbook) file += "x";
	        FileOutputStream out = new FileOutputStream(file);
	        wb.write(out);
	        out.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * create a library of cell styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		DataFormat df = wb.createDataFormat();

		CellStyle style;
		Font headerFont = wb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE
				.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("header", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE
				.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("header_date", style);

		Font font1 = wb.createFont();
		font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font1);
		styles.put("cell_b", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font1);
		styles.put("cell_b_centered", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_b_date", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_g", style);

		Font font2 = wb.createFont();
		font2.setColor(IndexedColors.BLUE.getIndex());
		font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font2);
		styles.put("cell_bb", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setFont(font1);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_bg", style);

		Font font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 14);
		font3.setColor(IndexedColors.DARK_BLUE.getIndex());
		font3.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setFont(font3);
		style.setWrapText(true);
		styles.put("cell_h", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setWrapText(true);
		styles.put("cell_normal", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		styles.put("cell_normal_centered", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setWrapText(true);
		style.setDataFormat(df.getFormat("d-mmm"));
		styles.put("cell_normal_date", style);

		style = createBorderedStyle(wb);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setIndention((short) 1);
		style.setWrapText(true);
		styles.put("cell_indented", style);

		style = createBorderedStyle(wb);
		style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put("cell_blue", style);

		return styles;
	}

	private static CellStyle createBorderedStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}
}

class Word {
	String wordEN;
	String wordSound;
	String wordCN;

	@Override
	public String toString() {
		return wordEN + " [" + wordSound + "] \n" + wordCN;
	}
}
