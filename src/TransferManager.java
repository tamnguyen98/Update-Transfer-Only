import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TransferManager {
	private String _src, _dest;

	public TransferManager(String src, String dest) {
		this._dest = dest;
		this._src = src;
	}

	private String getNewFilePath(String filePath) {
		// Edit src's file path to know where to transfer to new destination
		int srcLen = this._src.length();
		String relativePath = filePath.substring(srcLen);
		return this._dest + relativePath;
	}

	public ArrayList<Path> transferFiles(ArrayList<Path> files) {
		int i = 1;
		ArrayList<Path> failedTransfer = new ArrayList<Path>();
		for (Path f : files) {
			String newLocation = getNewFilePath(f.toString());
			try {
				Path newPath = Paths.get(newLocation);
				c2cTransfer(f, newPath); // attempt 3
//				Files.copy(f, newPath, StandardCopyOption.REPLACE_EXISTING); // Attempt 2
//				FileUtils.copy(f.toFile(), newPath);
				System.out.printf("(%d/%d) Transfering %s to %s\n", i++, files.size(), f.getFileName(), newLocation);
			} catch (Exception e) {
				System.err.printf("Error transferring file %s to %s\n", f.toString(), newLocation);
				System.err.println("\tError msg: " + e.getMessage());
				failedTransfer.add(f);
			}
		}
		return (failedTransfer.size() == 0) ? null : failedTransfer;
	}

	// https://howtodoinjava.com/java7/nio/java-nio-2-0-how-to-transfer-copy-data-between-channels/
	private void c2cTransfer(Path source, Path dest) throws Exception {
		// Get channel for output file
		FileOutputStream fos = new FileOutputStream(dest.toFile());
		WritableByteChannel targetChannel = fos.getChannel();

		// Get channel for input files
		FileInputStream fis = new FileInputStream(source.toString());
		FileChannel inputChannel = fis.getChannel();

		// Transfer data from input channel to output channel
		inputChannel.transferTo(0, inputChannel.size(), targetChannel);

		// close the input channel
		inputChannel.close();
		fis.close();

		// finally close the target channel
		targetChannel.close();
		fos.close();
	}

	private void remoteTransfer(ArrayList<Path> files) {
		// TODO: make function and public
	}
}
