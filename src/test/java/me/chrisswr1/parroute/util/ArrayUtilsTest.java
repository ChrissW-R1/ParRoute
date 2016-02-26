package me.chrisswr1.parroute.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * tests methods of {@link ArrayUtils}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @param <T> type of array elements
 * @since 0.0.1
 */
public class ArrayUtilsTest<T>
{
	/**
	 * Test method for {@link ArrayUtils#shift(T[], int)}.
	 */
	@Test
	public void testShift()
	{
		Integer[] array =
		{
				0,
				1,
				2,
				3,
				4,
				5,
				6,
				7
		};
		int length = array.length;
		
		Integer[] noShift = ArrayUtils.shift(array, 0);
		for (int i = 0; i < length; i++)
		{
			Assert.assertEquals(array[i], noShift[i]);
		}
		
		Integer[] fullShift = ArrayUtils.shift(array, length);
		for (int i = 0; i < length; i++)
		{
			Assert.assertEquals(array[i], fullShift[i]);
		}
		
		Integer[] shiftOneRight = ArrayUtils.shift(array, 1);
		Assert.assertEquals(array[length - 1], shiftOneRight[0]);
		for (int i = 1; i < length; i++)
		{
			Assert.assertEquals(array[i - 1], shiftOneRight[i]);
		}
		
		Integer[] shiftOneLeft = ArrayUtils.shift(array, -1);
		for (int i = 0; i < length - 1; i++)
		{
			Assert.assertEquals(array[i + 1], shiftOneLeft[i]);
		}
		Assert.assertEquals(array[0], shiftOneLeft[length - 1]);
	}
}