package me.chrisswr1.parroute.algorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import me.chrisswr1.parroute.util.MixedMultiKeyMap;

/**
 * sample implementation of the A* algorithm
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class AStar
{
	private Map<Long, Double>			h			= new HashMap<>();
	private MultiKeyMap<Long, Double>	c			= new MixedMultiKeyMap<>();
	private SetValuedMap<Long, Long>	successors	= new HashSetValuedHashMap<>();
													
	private Map<Long, Double>			g			= new HashMap<>();
	private Map<Long, Long>				predecessor	= new HashMap<>();
													
	private Map<Long, Double>			openList	= new HashMap<>();
	private Set<Long>					closedList	= new HashSet<>();
													
	/**
	 * standard constructor
	 * 
	 * @since 0.0.1
	 */
	public AStar()
	{
		this.h.put(1L, 530.2);
		this.h.put(2L, 501.8);
		this.h.put(3L, 473.5);
		this.h.put(4L, 457.3);
		this.h.put(5L, 437.2);
		this.h.put(6L, 420.2);
		this.h.put(7L, 399.9);
		this.h.put(8L, 394.8);
		this.h.put(9L, 403.6);
		this.h.put(10L, 455.0);
		this.h.put(11L, 446.1);
		this.h.put(12L, 373.0);
		this.h.put(13L, 327.4);
		this.h.put(14L, 294.8);
		this.h.put(15L, 276.4);
		this.h.put(16L, 343.8);
		this.h.put(17L, 338.5);
		this.h.put(18L, 250.2);
		this.h.put(19L, 201.4);
		this.h.put(20L, 292.4);
		this.h.put(21L, 220.5);
		this.h.put(22L, 183.2);
		this.h.put(23L, 175.4);
		this.h.put(24L, 147.6);
		this.h.put(25L, 129.6);
		this.h.put(26L, 125.5);
		this.h.put(27L, 120.7);
		this.h.put(28L, 120.9);
		this.h.put(29L, 173.3);
		this.h.put(30L, 153.2);
		this.h.put(31L, 123.9);
		this.h.put(32L, 99.9);
		this.h.put(33L, 84.8);
		this.h.put(34L, 64.6);
		this.h.put(35L, 293.3);
		this.h.put(36L, 265.1);
		this.h.put(37L, 220.0);
		this.h.put(38L, 209.9);
		this.h.put(39L, 202.0);
		this.h.put(40L, 185.3);
		this.h.put(41L, 180.8);
		this.h.put(42L, 174.6);
		this.h.put(43L, 181.2);
		this.h.put(44L, 150.7);
		this.h.put(45L, 125.3);
		this.h.put(46L, 519.2);
		this.h.put(47L, 504.2);
		this.h.put(48L, 489.6);
		this.h.put(49L, 475.2);
		this.h.put(50L, 432.7);
		this.h.put(51L, 376.1);
		this.h.put(52L, 314.8);
		this.h.put(53L, 273.9);
		this.h.put(54L, 245.5);
		this.h.put(55L, 234.1);
		this.h.put(56L, 203.8);
		this.h.put(57L, 478.7);
		this.h.put(58L, 482.4);
		this.h.put(59L, 479.6);
		this.h.put(61L, 395.9);
		this.h.put(62L, 333.9);
		this.h.put(63L, 190.6);
		this.h.put(64L, 173.2);
		this.h.put(65L, 142.2);
		this.h.put(66L, 109.5);
		this.h.put(67L, 96.1);
		this.h.put(68L, 86.4);
		this.h.put(69L, 39.1);
		this.h.put(70L, 21.8);
		this.h.put(71L, 0.0);
		this.h.put(72L, 370.4);
		this.h.put(73L, 322.0);
		
		this.c.put(1L, 2L, 29.8);
		this.c.put(2L, 3L, 36.9);
		this.c.put(3L, 4L, 25.1);
		this.c.put(4L, 5L, 33.7);
		this.c.put(5L, 6L, 37.9);
		this.c.put(6L, 7L, 46.9);
		this.c.put(7L, 8L, 106.8);
		this.c.put(8L, 9L, 46.4);
		this.c.put(10L, 11L, 10.3);
		this.c.put(11L, 12L, 80.3);
		this.c.put(12L, 13L, 52.2);
		this.c.put(13L, 14L, 48.1);
		this.c.put(14L, 15L, 33.7);
		this.c.put(16L, 17L, 7.0);
		this.c.put(17L, 18L, 101.0);
		this.c.put(18L, 19L, 61.6);
		this.c.put(20L, 21L, 77.2);
		this.c.put(21L, 22L, 40.5);
		this.c.put(22L, 23L, 8.2);
		this.c.put(23L, 24L, 34.8);
		this.c.put(65L, 25L, 21.6);
		this.c.put(25L, 26L, 35.1);
		this.c.put(26L, 27L, 32.3);
		this.c.put(27L, 28L, 7.8);
		this.c.put(29L, 30L, 21.7);
		this.c.put(30L, 31L, 33.4);
		this.c.put(31L, 32L, 30.0);
		this.c.put(32L, 33L, 21.5);
		this.c.put(33L, 34L, 23.7);
		this.c.put(35L, 36L, 55.8);
		this.c.put(37L, 38L, 26.5);
		this.c.put(38L, 39L, 15.0);
		this.c.put(39L, 40L, 29.8);
		this.c.put(40L, 41L, 6.2);
		this.c.put(41L, 42L, 7.0);
		this.c.put(42L, 27L, 54.2);
		this.c.put(43L, 44L, 30.5);
		this.c.put(44L, 45L, 38.1);
		this.c.put(1L, 46L, 11.8);
		this.c.put(46L, 47L, 16.5);
		this.c.put(47L, 48L, 16.2);
		this.c.put(48L, 49L, 16.0);
		this.c.put(49L, 10L, 28.8);
		this.c.put(10L, 50L, 31.9);
		this.c.put(50L, 51L, 77.9);
		this.c.put(51L, 16L, 43.6);
		this.c.put(16L, 52L, 40.1);
		this.c.put(52L, 20L, 34.5);
		this.c.put(20L, 53L, 27.8);
		this.c.put(53L, 54L, 41.5);
		this.c.put(54L, 55L, 17.5);
		this.c.put(55L, 56L, 45.4);
		this.c.put(56L, 29L, 52.9);
		this.c.put(3L, 57L, 16.2);
		this.c.put(57L, 58L, 17.1);
		this.c.put(58L, 59L, 11.6);
		this.c.put(59L, 49L, 13.1);
		this.c.put(6L, 61L, 24.5);
		this.c.put(61L, 62L, 62.9);
		this.c.put(62L, 14L, 39.8);
		this.c.put(14L, 36L, 30.9);
		this.c.put(36L, 37L, 49.3);
		this.c.put(37L, 19L, 25.5);
		this.c.put(19L, 63L, 13.7);
		this.c.put(63L, 64L, 20.9);
		this.c.put(64L, 24L, 28.6);
		this.c.put(24L, 65L, 6.2);
		this.c.put(65L, 45L, 19.9);
		this.c.put(45L, 66L, 18.0);
		this.c.put(66L, 67L, 14.8);
		this.c.put(67L, 68L, 10.1);
		this.c.put(68L, 34L, 21.9);
		this.c.put(34L, 69L, 25.6);
		this.c.put(69L, 70L, 17.3);
		this.c.put(70L, 71L, 21.8);
		this.c.put(7L, 72L, 30.2);
		this.c.put(72L, 73L, 48.9);
		this.c.put(73L, 15L, 46.3);
		this.c.put(2L, 49L, 42.5);
		this.c.put(13L, 35L, 43.8);
		this.c.put(35L, 18L, 61.0);
		this.c.put(63L, 23L, 52.2);
		this.c.put(22L, 43L, 30.3);
		this.c.put(43L, 56L, 83.8);
		this.c.put(25L, 67L, 50.8);
		this.c.put(68L, 33L, 17.5);
		
		this.successors.put(1L, 46L);
		this.successors.put(2L, 49L);
		this.successors.put(3L, 57L);
		this.successors.put(4L, 5L);
		this.successors.put(5L, 6L);
		this.successors.put(6L, 5L);
		this.successors.put(7L, 6L);
		this.successors.put(8L, 7L);
		this.successors.put(9L, 8L);
		this.successors.put(10L, 50L);
		this.successors.put(11L, 12L);
		this.successors.put(12L, 13L);
		this.successors.put(13L, 35L);
		this.successors.put(14L, 36L);
		this.successors.put(15L, 73L);
		this.successors.put(16L, 52L);
		this.successors.put(17L, 16L);
		this.successors.put(18L, 19L);
		this.successors.put(19L, 63L);
		this.successors.put(20L, 53L);
		this.successors.put(21L, 22L);
		this.successors.put(22L, 23L);
		this.successors.put(23L, 24L);
		this.successors.put(24L, 65L);
		this.successors.put(25L, 65L);
		this.successors.put(26L, 25L);
		this.successors.put(27L, 26L);
		this.successors.put(28L, 27L);
		this.successors.put(29L, 30L);
		this.successors.put(30L, 29L);
		this.successors.put(31L, 30L);
		this.successors.put(32L, 31L);
		this.successors.put(33L, 32L);
		this.successors.put(34L, 69L);
		this.successors.put(35L, 18L);
		this.successors.put(36L, 37L);
		this.successors.put(37L, 19L);
		this.successors.put(38L, 37L);
		this.successors.put(39L, 40L);
		this.successors.put(40L, 41L);
		this.successors.put(41L, 42L);
		this.successors.put(42L, 27L);
		this.successors.put(43L, 56L);
		this.successors.put(44L, 45L);
		this.successors.put(45L, 44L);
		this.successors.put(46L, 47L);
		this.successors.put(47L, 48L);
		this.successors.put(48L, 49L);
		this.successors.put(49L, 10L);
		this.successors.put(50L, 51L);
		this.successors.put(51L, 16L);
		this.successors.put(52L, 20L);
		this.successors.put(53L, 54L);
		this.successors.put(54L, 55L);
		this.successors.put(55L, 56L);
		this.successors.put(56L, 29L);
		this.successors.put(57L, 58L);
		this.successors.put(58L, 59L);
		this.successors.put(59L, 49L);
		this.successors.put(61L, 62L);
		this.successors.put(62L, 14L);
		this.successors.put(63L, 23L);
		this.successors.put(64L, 24L);
		this.successors.put(65L, 45L);
		this.successors.put(66L, 67L);
		this.successors.put(67L, 68L);
		this.successors.put(68L, 33L);
		this.successors.put(69L, 70L);
		this.successors.put(70L, 71L);
		this.successors.put(71L, 70L);
		this.successors.put(72L, 73L);
		this.successors.put(73L, 15L);
		this.successors.put(1L, 2L);
		this.successors.put(2L, 3L);
		this.successors.put(3L, 4L);
		this.successors.put(4L, 3L);
		this.successors.put(5L, 4L);
		this.successors.put(6L, 61L);
		this.successors.put(7L, 72L);
		this.successors.put(8L, 9L);
		this.successors.put(10L, 11L);
		this.successors.put(11L, 10L);
		this.successors.put(12L, 11L);
		this.successors.put(13L, 14L);
		this.successors.put(14L, 15L);
		this.successors.put(15L, 14L);
		this.successors.put(16L, 17L);
		this.successors.put(17L, 18L);
		this.successors.put(18L, 35L);
		this.successors.put(19L, 37L);
		this.successors.put(20L, 21L);
		this.successors.put(21L, 20L);
		this.successors.put(22L, 21L);
		this.successors.put(23L, 63L);
		this.successors.put(25L, 67L);
		this.successors.put(26L, 27L);
		this.successors.put(27L, 28L);
		this.successors.put(29L, 56L);
		this.successors.put(30L, 31L);
		this.successors.put(31L, 32L);
		this.successors.put(32L, 33L);
		this.successors.put(33L, 34L);
		this.successors.put(34L, 68L);
		this.successors.put(35L, 36L);
		this.successors.put(36L, 14L);
		this.successors.put(37L, 38L);
		this.successors.put(38L, 39L);
		this.successors.put(39L, 38L);
		this.successors.put(40L, 39L);
		this.successors.put(41L, 40L);
		this.successors.put(42L, 41L);
		this.successors.put(43L, 44L);
		this.successors.put(44L, 43L);
		this.successors.put(45L, 66L);
		this.successors.put(46L, 1L);
		this.successors.put(47L, 46L);
		this.successors.put(48L, 47L);
		this.successors.put(49L, 59L);
		this.successors.put(50L, 10L);
		this.successors.put(51L, 50L);
		this.successors.put(52L, 16L);
		this.successors.put(53L, 20L);
		this.successors.put(54L, 53L);
		this.successors.put(55L, 54L);
		this.successors.put(56L, 43L);
		this.successors.put(57L, 3L);
		this.successors.put(58L, 57L);
		this.successors.put(59L, 58L);
		this.successors.put(61L, 6L);
		this.successors.put(62L, 61L);
		this.successors.put(63L, 64L);
		this.successors.put(64L, 63L);
		this.successors.put(65L, 25L);
		this.successors.put(66L, 45L);
		this.successors.put(67L, 25L);
		this.successors.put(68L, 34L);
		this.successors.put(69L, 34L);
		this.successors.put(70L, 69L);
		this.successors.put(72L, 7L);
		this.successors.put(73L, 72L);
		this.successors.put(2L, 1L);
		this.successors.put(3L, 2L);
		this.successors.put(6L, 7L);
		this.successors.put(7L, 8L);
		this.successors.put(10L, 49L);
		this.successors.put(13L, 12L);
		this.successors.put(14L, 62L);
		this.successors.put(16L, 51L);
		this.successors.put(18L, 17L);
		this.successors.put(19L, 18L);
		this.successors.put(20L, 52L);
		this.successors.put(23L, 22L);
		this.successors.put(24L, 64L);
		this.successors.put(25L, 26L);
		this.successors.put(27L, 42L);
		this.successors.put(33L, 68L);
		this.successors.put(34L, 33L);
		this.successors.put(35L, 13L);
		this.successors.put(36L, 35L);
		this.successors.put(37L, 36L);
		this.successors.put(43L, 22L);
		this.successors.put(45L, 65L);
		this.successors.put(49L, 2L);
		this.successors.put(56L, 55L);
		this.successors.put(63L, 19L);
		this.successors.put(65L, 24L);
		this.successors.put(67L, 66L);
		this.successors.put(68L, 67L);
		this.successors.put(14L, 13L);
		this.successors.put(24L, 23L);
		this.successors.put(49L, 48L);
	}
	
	/**
	 * main method
	 * 
	 * @since 0.0.1
	 * 		
	 * @param args arguments from the console
	 */
	public static void main(String[] args)
	{
		AStar aStar = new AStar();
		
		aStar.calc(1, 71);
	}
	
	public static <K, V extends Comparable<V>> Entry<K, V> getMin(Map<K, V> map)
	{
		return Collections.min(map.entrySet(), new Comparator<Entry<K, V>>()
		{
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
		});
	}
	
	private double g(long id)
	{
		double res;
		
		try
		{
			res = this.g.get(id);
			if (Double.isNaN(res))
			{
				res = 0;
			}
		}
		catch (NullPointerException e)
		{
			res = 0;
		}
		
		return res;
	}
	
	public void printRoute(long dest)
	{
		System.out.println("The A* have found the following route:");
		
		LinkedHashSet<Long> nodeList = new LinkedHashSet<>();
		
		long lastNode = dest;
		while (true)
		{
			nodeList.add(lastNode);
			try
			{
				lastNode = this.predecessor.get(lastNode);
			}
			catch (NullPointerException e)
			{
				break;
			}
		}
		
		Iterator<Long> iter = (new LinkedList<>(nodeList)).descendingIterator();
		while (iter.hasNext())
		{
			System.out.print(iter.next() + ", ");
		}
	}
	
	public void calc(long start, long dest)
	{
		this.openList.put(start, 0D);
		
		do
		{
			long currentNode = AStar.getMin(this.openList).getKey();
			this.openList.remove(currentNode);
			
			if (currentNode == dest)
			{
				this.printRoute(dest);
				return;
			}
			
			this.closedList.add(currentNode);
			
			this.expand(currentNode);
		}
		while ( ! (this.openList.isEmpty()));
		
		System.out.println("No route found from " + start + " to " + dest + "!");
	}
	
	public void expand(long currentNode)
	{
		for (long successor : this.successors.get(currentNode))
		{
			if (this.closedList.contains(successor))
			{
				continue;
			}
			
			double tentative_g = this.g(currentNode) + this.c.get(currentNode, successor);
			
			if (this.openList.containsKey(successor) && tentative_g >= this.g(successor))
			{
				continue;
			}
			
			this.predecessor.put(successor, currentNode);
			this.g.put(successor, tentative_g);
			
			double f = tentative_g + this.h.get(successor);
			this.openList.put(successor, f);
		}
	}
}