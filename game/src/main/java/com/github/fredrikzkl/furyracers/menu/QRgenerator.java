package com.github.fredrikzkl.furyracers.menu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class QRgenerator {
	
	public void genQR(String IPtoController){
		
		int backgroundColor = 0xEFEFEF;
		int frontColor = 0x0;
		
		ByteArrayOutputStream out = QRCode.from("http://" + IPtoController)
                .to(ImageType.PNG).withColor(frontColor, backgroundColor).stream();

		try {
			FileOutputStream fout = new FileOutputStream(new File(
			"games/furyracers/assets/QRcode/controllerQR.JPG"));
			
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
