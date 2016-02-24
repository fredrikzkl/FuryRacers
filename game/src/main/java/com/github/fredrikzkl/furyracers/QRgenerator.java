package com.github.fredrikzkl.furyracers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class QRgenerator {
	
	int backColor = 0xCC0000;
	int frontColor = 0x0;
	
	public void genQR(String IPtoController){
		ByteArrayOutputStream out = QRCode.from(IPtoController)
                .to(ImageType.PNG).withColor(frontColor, backColor).stream();

		try {
			FileOutputStream fout = new FileOutputStream(new File(
			"QRcode/controllerQR.JPG"));
			
			fout.write(out.toByteArray());
			fout.flush();
			fout.close();
		
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
	}
}
