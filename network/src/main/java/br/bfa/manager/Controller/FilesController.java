package br.bfa.manager.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.bfa.manager.Service.ConfigurationService;

@Controller
public class FilesController {

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/files/regulation", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadFile(HttpServletResponse response) {
		File initialFile = new File(context.getRealPath("/files/CheckList.pdf"));
		InputStream targetStream;
		response.setContentType("application/pdf");
		 response.setHeader("Content-Disposition", "attachment; filename=" + "checklist.pdf");
		try {
			targetStream = new FileInputStream(initialFile);
			org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/files/sumula", method = RequestMethod.GET)
	@ResponseBody
	public void downloadFileSumula(HttpServletResponse response) {
		File initialFile = new File(context.getRealPath("/files/Sumula.pdf"));
		InputStream targetStream;
		response.setContentType("application/pdf");
		 response.setHeader("Content-Disposition", "attachment; filename=" + "sumula.pdf");
		try {
			targetStream = new FileInputStream(initialFile);
			org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
