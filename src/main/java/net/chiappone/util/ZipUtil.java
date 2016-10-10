package net.chiappone.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Kurtis Chiappone
 * @date 10/9/2016
 */
public class ZipUtil {

	private static final int BUFFER_SIZE = 2048;
	private static final Logger logger = LoggerFactory.getLogger( ZipUtil.class );

	/**
	 * Adds an entry to the zip file.
	 *
	 * @param zos
	 * @param unzipped
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void addZipEntry( ZipOutputStream zos, File unzipped )
			throws FileNotFoundException, IOException {

		byte[] buffer = new byte[ BUFFER_SIZE ];

		BufferedInputStream bis = new BufferedInputStream( new FileInputStream( unzipped ),
				BUFFER_SIZE );

		// Remove the absolute path so that we don't have the full path to the file in the zip
		String source = unzipped.toString().substring( unzipped.toString().lastIndexOf( "\\" ) + 1,
				unzipped.toString().length() );
		ZipEntry entry = new ZipEntry( source );
		logger.debug( "Adding zip entry: {}", source );
		zos.putNextEntry( entry );

		int count;

		while ( ( count = bis.read( buffer, 0, BUFFER_SIZE ) ) != -1 ) {

			zos.write( buffer, 0, count );

		}

		zos.closeEntry();
		bis.close();

	}

	/**
	 * Zips a file or directory.
	 *
	 * @param unzipped
	 * @param zipped
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File zip( File unzipped, File zipped ) throws FileNotFoundException, IOException {

		ZipOutputStream zos = new ZipOutputStream( new BufferedOutputStream(
				new CheckedOutputStream( new FileOutputStream( zipped ), new Adler32() ) ) );

		if ( unzipped.isDirectory() ) {

			zipped = zipDirectory( zos, unzipped, zipped );

		} else {

			zipped = zipFile( zos, unzipped, zipped );

		}

		zos.close();

		return zipped;

	}

	/**
	 * Zips a directory.
	 *
	 * @param zos
	 * @param unzipped
	 * @param zipped
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static File zipDirectory( ZipOutputStream zos, File unzipped, File zipped )
			throws FileNotFoundException, IOException {

		File[] files = unzipped.listFiles();

		if ( files != null && files.length > 0 ) {

			for ( File f : files ) {

				// FIXME This triggers a security error message in Windows

				if ( f.isDirectory() ) {

					zipDirectory( zos, f, zipped );
					continue;

				}

				addZipEntry( zos, f.getAbsoluteFile() );

			}

		}

		return zipped;

	}

	/**
	 * Zips a file.
	 *
	 * @param zos
	 * @param unzipped
	 * @param zipped
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static File zipFile( ZipOutputStream zos, File unzipped, File zipped )
			throws FileNotFoundException, IOException {

		addZipEntry( zos, unzipped );
		return zipped;

	}

}
