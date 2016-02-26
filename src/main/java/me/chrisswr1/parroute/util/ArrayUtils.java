package me.chrisswr1.parroute.util;

import java.util.Arrays;

/**
 * defines some utilities to work with arrays
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class ArrayUtils
{
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private ArrayUtils()
	{
	}
	
	/**
	 * shift all elements of an array
	 * 
	 * @since 0.0.1
	 * 
	 * @param <T> the type of the elements
	 * @param array the array to shift
	 * @param count the count of steps to shift. Use a positive value for right
	 *            shift and a negative for shift to left
	 * @return a new array with the shifted elements
	 */
	public static <T> T[] shift(T[] array, int count)
	{
		int length = array.length;
		T[] res = Arrays.copyOf(array, length);
		
		if (count == 0)
		{
			return res;
		}
		
		for (int i = 0; i < length; i++)
		{
			int srcIdx = i - count;
			
			while (srcIdx < 0)
			{
				srcIdx += length;
			}
			while (srcIdx > (length - 1))
			{
				srcIdx -= length;
			}
			
			res[i] = array[srcIdx];
		}
		
		return res;
	}
}