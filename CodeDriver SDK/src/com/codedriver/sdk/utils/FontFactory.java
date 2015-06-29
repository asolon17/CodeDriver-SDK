package com.codedriver.sdk.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Font loading utility used to load <code>Fonts</code> specified
 * by a configuration file.
 * 
 * @author Alexander C. Solon
 * @since CDSDK 0.1r0
 * @threadsafety Thread safe
 */
public class FontFactory {
	
	// Used to make the code more portable. Set this to the desired location
	// to load the font configuration file from.
	private static final String FONT_CONFIG_FILE_LOCATION = "config/fonts.properties";
	
	// Map of the loaded fonts and their font names
	private static HashMap<String, Font> loaded_fonts;
	
	/**
	 * Returns an array of font names representing the fonts 
	 * associated with the <code>FontFactory</code>. All returned
	 * values are valid font names to use when creating
	 * <code>Fonts</code> from the <code>FontFactory</code>.
	 * 
	 * @return An array of font names
	 */
	public static synchronized String[] getRegisteredFontNames() {
		return (String[]) loaded_fonts.keySet().toArray(new String[loaded_fonts.keySet().size()]);
	}
	
	/**
	 * Returns an array of font names representing the fonts loaded
	 * by the system. All returned values are valid font names to use
	 * when creating <code>Fonts</code> from the <code>FontFactory</code>.
	 * 
	 * @return An array of font names
	 */
	public static String[] getSystemFontNames() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}
	
	/**
	 * Create and load a new font based on the specified parameters.
	 * <p>
	 * <b>Note:</b> Fonts loaded from the <code>FontFactory</code> are
	 * checked <u>before</u> the system fonts.
	 * 
	 * @param name The desired font name
	 * @param style The desired font style
	 * @param size The desired font size
	 * @return The desired font; <code>null</code> if the font doesn't exist.
	 */
	public static synchronized Font createFont(String name, int style, int size) {
		if (loaded_fonts.containsKey(name)) {
			return loaded_fonts.get(name).deriveFont(style, size);
		} else {
			String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();
			for (String s : fontNames) {
				if (s.equals(name)) {
					return new Font(name, style, size);
				}
			}
			return null;
		}
	}

	/**
	 * Re-loads fonts associated with the <code>FontFactory</code>.
	 */
	public static synchronized void reloadFonts() {
		loadFonts();
	}
	
	// Static Initializer
	static {
		loadFonts();
	}
	
	private static synchronized void loadFonts() {
		loaded_fonts = new HashMap<String, Font>();
		Properties font_config = new Properties();

		try {
			FileInputStream fileIn = new FileInputStream(
					new File(FONT_CONFIG_FILE_LOCATION));
			font_config.load(fileIn);
			fileIn.close();
		} catch (IOException e) {
			System.err.println("Warning: Failed to load font configuration!");
			System.err.println(e.getMessage());
		}

		for (Object key : font_config.keySet()) {
			try {
				FileInputStream fileIn = new FileInputStream(
						new File(font_config.getProperty((String) key)));
				Font f = Font.createFont(Font.TRUETYPE_FONT, fileIn);

				loaded_fonts.put((String)key, f);
			} catch (FontFormatException | IOException e) {
				System.err.println("Warning: Failed to load font \"" + (String)key + "\"!");
				System.err.println(e.getMessage());
			}
		}
	}
}
