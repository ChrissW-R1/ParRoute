package me.chrisswr1.parroute.util;

/**
 * provides a collection of methods to work with {@link String}s
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class StringUtils
{
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private StringUtils()
	{
	}
	
	/**
	 * checks, if one {@link String} contains another, independent of the case
	 * 
	 * @since 0.0.1
	 * 		
	 * @param string the {@link String} to search in
	 * @param subString the {@link String} to search for
	 * @return <code>true</code> if {@code string} contains {@code subString}
	 */
	public static boolean containsIgnoreCase(String string, String subString)
	{
		return string.toLowerCase().contains(subString.toLowerCase()) || string.toUpperCase().contains(subString.toUpperCase());
	}
}