package br.bfa.manager.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;

import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Imports;
import br.bfa.manager.entity.Team;

public class XLSUtil {

	public static File generateXLS(Team team, String path) {
		try {
			File fileOut = new File(path + File.separator + team.getName().replaceAll(" ", "") + ".xls");
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Lista de Atletas");
			worksheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 20));

			// index from 0,0... cell A1 is cell(0,0)
			HSSFRow row1 = worksheet.createRow((short) 0);

			HSSFCell cellA1 = row1.createCell((short) 0);
			cellA1.setCellValue(team.getName());
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
			cellA1.setCellStyle(cellStyle);

			int i = 2;
			for (Athlete athlete : team.getAthletes()) {
				HSSFRow nRow = worksheet.createRow((short) i++);

				HSSFCell cellB1 = nRow.createCell((short) 0);
				cellB1.setCellValue(athlete.getName());
				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB1.setCellStyle(cellStyle);

				HSSFCell cellB2 = nRow.createCell((short) 1);
				cellB2.setCellValue(" ");
				if (athlete.getHeight() != null) {
					cellB2.setCellValue(athlete.getHeight());

				}

				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB2.setCellStyle(cellStyle);

				HSSFCell cellB3 = nRow.createCell((short) 2);

				cellB3.setCellValue(" ");
				if (athlete.getWeight() != null) {
					cellB3.setCellValue(athlete.getWeight());

				}

				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB3.setCellStyle(cellStyle);

				HSSFCell cellB4 = nRow.createCell((short) 3);

				cellB4.setCellValue(" ");
				if (athlete.getDtBirth() != null) {
					cellB4.setCellValue(athlete.getDtBirth().toString());
				}

				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB4.setCellStyle(cellStyle);

				HSSFCell cellB5 = nRow.createCell((short) 4);
				cellB5.setCellValue(" ");
				if (athlete.getDtStart() != null) {
					cellB5.setCellValue(athlete.getDtStart().toString());
				}

				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB5.setCellStyle(cellStyle);

				HSSFCell cellB6 = nRow.createCell((short) 5);
				cellB6.setCellValue("Não Assinado");
				if (athlete.getContractSigned() != null && athlete.getContractSigned()) {
					cellB6.setCellValue("Assinado");
				}

				cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
				cellB6.setCellStyle(cellStyle);

			}

			workbook.write(fileOut);
			workbook.close();
			return fileOut;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File generateXLSImports(List<Imports> imports, String path) {
		try {
			File fileOut = new File(path + File.separator + "imports" + ".xls");
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Lista de Estrangeiros");

			int i = 0;
			for (Imports iAthlete : imports) {
				HSSFRow nRow = worksheet.createRow((short) i++);

				HSSFCell cellB1 = nRow.createCell((short) 0);
				cellB1.setCellValue(iAthlete.getAthlete().getName());

				cellB1 = nRow.createCell((short) 1);
				cellB1.setCellValue(iAthlete.getAthlete().getExperience().getName());

				cellB1 = nRow.createCell((short) 2);
				cellB1.setCellValue(iAthlete.getAthlete().getExperience().getPoints());

				cellB1 = nRow.createCell((short) 3);
				cellB1.setCellValue(iAthlete.getAthlete().getTeam().getName());

			}

			workbook.write(fileOut);
			workbook.close();
			return fileOut;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File generateSumula(Team team, String path, List<Athlete> athletes) {
		try {
			File fileOut = new File(
					path + File.separator + "sumulas" + File.separator + team.getName().replaceAll(" ", "") + ".xls");
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Lista de Atletas");
			worksheet.getPrintSetup().setFooterMargin(2.2);
			worksheet.getPrintSetup().setHeaderMargin(2.2);

			CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 5, 0, 4);
			worksheet.addMergedRegion(cellRangeAddress);
			cellRangeAddress = new CellRangeAddress(6, 7, 0, 4);
			worksheet.addMergedRegion(cellRangeAddress);

			HSSFRegionUtil.setBorderTop(BorderStyle.MEDIUM.getCode(), cellRangeAddress, worksheet,
					worksheet.getWorkbook());
			HSSFRegionUtil.setBorderLeft(BorderStyle.MEDIUM.getCode(), cellRangeAddress, worksheet,
					worksheet.getWorkbook());
			HSSFRegionUtil.setBorderRight(BorderStyle.MEDIUM.getCode(), cellRangeAddress, worksheet,
					worksheet.getWorkbook());
			HSSFRegionUtil.setBorderBottom(BorderStyle.MEDIUM.getCode(), cellRangeAddress, worksheet,
					worksheet.getWorkbook());

			// FileInputStream obtains input bytes from the image file
			String[] basePath = path.split("WEB-INF");

			String pathCabecalho = basePath[0] + File.separator + "sumulas" + File.separator + "cabecalhoSumula.gif";
			System.out.println(pathCabecalho);
			System.out.println(path);
			InputStream inputStream = new FileInputStream(pathCabecalho);

			// Get the contents of an InputStream as a byte[].
			byte[] bytes = IOUtils.toByteArray(inputStream);
			// Adds a picture to the workbook
			int pictureIdx = worksheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			// close the input stream
			inputStream.close();

			// Returns an object that handles instantiating concrete classes
			CreationHelper helper = worksheet.getWorkbook().getCreationHelper();

			// Creates the top-level drawing patriarch.
			Drawing drawing = worksheet.createDrawingPatriarch();

			// Create an anchor that is attached to the worksheet
			ClientAnchor anchor = helper.createClientAnchor();
			// set top-left corner for the image
			anchor.setCol1(0);
			anchor.setRow1(0);

			// Creates a picture
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			// Reset the image to the original size

			HSSFRow titleRow = worksheet.createRow((short) 6);
			HSSFCell cell = titleRow.createCell((short) 0);
			HSSFCellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
			cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
			cellStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setShrinkToFit(Boolean.TRUE);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			Font font = cell.getSheet().getWorkbook().createFont();
			font.setBold(Boolean.TRUE);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle.setBorderTop(BorderStyle.MEDIUM);
			cellStyle.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle.setBorderRight(BorderStyle.MEDIUM);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("Atletas");

			HSSFRow header = worksheet.createRow((short) 8);
			cell = header.createCell((short) 0);
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));
			cell.setCellValue("");

			cell = header.createCell((short) 1);
			cell.setCellValue("Jersey #");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 2);
			cell.setCellValue("Nome");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 3);
			cell.setCellValue("RG/Passport");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 4);
			cell.setCellValue("Assinatura");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			int cellNumber = 1;
			int i = 9;
			// int importsNumber = 0;

			for (Athlete athlete : athletes) {
				HSSFRow nRow = worksheet.createRow((short) i++);

				cell = nRow.createCell((short) 0);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(cellNumber++);

				cell = nRow.createCell((short) 1);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(" ");

				cell = nRow.createCell((short) 2);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(athlete.getName());

				cell = nRow.createCell((short) 3);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(athlete.getLegalId2());

				cell = nRow.createCell((short) 4);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue("");
			}
			i++;
			cellRangeAddress = new CellRangeAddress(i, i + 1, 0, 4);
			worksheet.addMergedRegion(cellRangeAddress);

			HSSFRow staff = worksheet.createRow((short) i);
			cell = staff.createCell((short) 0);
			cellStyle = cell.getSheet().getWorkbook().createCellStyle();
			cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
			cellStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setShrinkToFit(Boolean.TRUE);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			font = cell.getSheet().getWorkbook().createFont();
			font.setBold(Boolean.TRUE);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.MEDIUM);
			cellStyle.setBorderTop(BorderStyle.MEDIUM);
			cellStyle.setBorderLeft(BorderStyle.MEDIUM);
			cellStyle.setBorderRight(BorderStyle.MEDIUM);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("Staff");

			i = i + 2;
			header = worksheet.createRow((short) i++);
			cell = header.createCell((short) 0);
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));
			cell.setCellValue("");

			cell = header.createCell((short) 1);
			cell.setCellValue("Função");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 2);
			cell.setCellValue("Nome");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 3);
			cell.setCellValue("RG/Passport");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

			cell = header.createCell((short) 4);
			cell.setCellValue("Assinatura");
			cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));
			cellNumber = 1;
			for (int a = 0; a < 15; a++) {
				HSSFRow nRow = worksheet.createRow((short) i++);

				cell = nRow.createCell((short) 0);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(cellNumber++);

				cell = nRow.createCell((short) 1);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue(" ");

				cell = nRow.createCell((short) 2);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue("");

				cell = nRow.createCell((short) 3);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue("");

				cell = nRow.createCell((short) 4);
				cell.setCellStyle(createCellStyleBold(cell, IndexedColors.WHITE));
				cell.setCellValue("");

			}

			worksheet.setColumnWidth(0, 5 * 256);
			worksheet.setColumnWidth(1, 5 * 256);
			worksheet.setColumnWidth(2, 28 * 256);
			worksheet.setColumnWidth(3, 9 * 256);
			worksheet.setColumnWidth(4, 35 * 256);
			pict.resize();
			workbook.write(fileOut);
			workbook.close();
			return fileOut;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static HSSFCellStyle createCellStyleBold(HSSFCell cell, IndexedColors color) {
		HSSFCellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
		cellStyle.setFillForegroundColor(color.index);
		cellStyle.setFillBackgroundColor(color.index);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setShrinkToFit(Boolean.TRUE);
		Font font = cell.getSheet().getWorkbook().createFont();
		font.setBold(Boolean.TRUE);
		cellStyle.setFont(font);
		cellStyle.setBorderBottom(BorderStyle.MEDIUM);
		cellStyle.setBorderTop(BorderStyle.MEDIUM);
		cellStyle.setBorderLeft(BorderStyle.MEDIUM);
		cellStyle.setBorderRight(BorderStyle.MEDIUM);
		return cellStyle;
	}

	public static File generateXlsAthletes(List<Athlete> athletes, String path) {
		File fileOut = new File(path + File.separator + "athletes" + ".xls");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet worksheet = workbook.createSheet("Lista de Atletas");
		worksheet.getPrintSetup().setLandscape(true);
		worksheet.getPrintSetup().setFooterMargin(2.2);
		worksheet.getPrintSetup().setHeaderMargin(2.2);

		HSSFRow header = worksheet.createRow((short) 0);

		HSSFCell cell = header.createCell((short) 0);
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));
		cell.setCellValue("");

		cell = header.createCell((short) 1);
		cell.setCellValue("Nome");
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

		cell = header.createCell((short) 2);
		cell.setCellValue("Equipe");
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

		cell = header.createCell((short) 3);
		cell.setCellValue("Posição");
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

		cell = header.createCell((short) 4);
		cell.setCellValue("Peso");
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

		cell = header.createCell((short) 5);
		cell.setCellValue("Altura");
		cell.setCellStyle(createCellStyleBold(cell, IndexedColors.GREY_25_PERCENT));

		int rowNumber = 1;
		int i = 1;

		for (Athlete athlete : athletes) {
			HSSFRow nRow = worksheet.createRow((short) i++);

			cell = nRow.createCell((short) 0);
			cell.setCellValue(rowNumber++);

			cell = nRow.createCell((short) 2);
			cell.setCellValue(athlete.getName());

			cell = nRow.createCell((short) 3);
			cell.setCellValue(athlete.getTeam().getName());

			cell = nRow.createCell((short) 4);
			cell.setCellValue("");
			if (athlete.getPositions() != null && !athlete.getPositions().isEmpty()) {
				cell.setCellValue(athlete.getPositions().get(0).toString());
			}

			cell = nRow.createCell((short) 5);
			cell.setCellValue(athlete.getWeight());

			cell = nRow.createCell((short) 6);
			cell.setCellValue(athlete.getHeight());

		}
		worksheet.setColumnWidth(1, 5 * 256);
		worksheet.setColumnWidth(2, 28 * 256);
		worksheet.setColumnWidth(3, 20 * 256);
		worksheet.setColumnWidth(4, 15 * 256);
		try {
			workbook.write(fileOut);
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileOut;
	}

	public static File zip(List<File> files, String filename) {
		File zipfile = new File(filename);
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		try {
			// create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			// compress the files
			for (File file : files) {
				FileInputStream in = new FileInputStream(file.getAbsolutePath());
				// add ZIP entry to output stream
				out.putNextEntry(new ZipEntry(file.getName()));
				// transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				// complete the entry
				out.closeEntry();
				in.close();
			}
			// complete the ZIP file
			out.close();
			return zipfile;
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

}
