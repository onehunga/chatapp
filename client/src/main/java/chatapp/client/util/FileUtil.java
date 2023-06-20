package chatapp.client.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	public static FileWriter openWrite(String path) {
		File file = new File(path);

		try {
			if(!file.exists()) {
				if(file.createNewFile()) {
					return new FileWriter(file);
				}
				else {
					System.out.println("failed to create file");
				}
			} else {
				return new FileWriter(file);
			}
		} catch(IOException e) {
			System.out.println("openWrite failed: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
}
