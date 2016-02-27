package me.chrisswr1.parroute.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * defines some utility methods to work with {@link Collection}s
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class CollectionUtils
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger LOGGER = LogManager.getLogger(CollectionUtils.class);
	
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private CollectionUtils()
	{
	}
	
	/**
	 * creates a {@link Comparator}, which compares the values of a {@link Map}
	 * 
	 * @since 0.0.1
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the value type
	 * @param map the {@link Map} to get the {@link Comparator} for
	 * @return the generated {@link Comparator}
	 */
	private static <K, V extends Comparable<V>> Comparator<Entry<K, V>> getMapComperator(Map<K, V> map)
	{
		return new Comparator<Entry<K, V>>()
		{
			@Override
			public int compare(Entry<K, V> entry1, Entry<K, V> entry2)
			{
				return entry1.getValue().compareTo(entry2.getValue());
			}
		};
	}
	
	/**
	 * gives the {@link Entry} with the lowest value in {@code map}
	 * 
	 * @since 0.0.1
	 * 
	 * @param <K> the type of keys in {@code map}
	 * @param <V> the value type
	 * @param map the {@link Map} to get the minimum {@link Entry} from
	 * @return the {@link Entry} with the minimum value
	 */
	public static <K, V extends Comparable<V>> Entry<K, V> min(Map<K, V> map)
	{
		return Collections.min(map.entrySet(), CollectionUtils.getMapComperator(map));
	}
	
	/**
	 * gives the {@link Entry} with the lowest value in {@code map}
	 * 
	 * @since 0.0.1
	 * 
	 * @param <K> the type of keys in {@code map}
	 * @param <V> the value type
	 * @param map the {@link Map} to get the minimum {@link Entry} from
	 * @return the {@link Entry} with the minimum value
	 */
	public static <K, V extends Comparable<V>> Entry<K, V> max(Map<K, V> map)
	{
		return Collections.max(map.entrySet(), CollectionUtils.getMapComperator(map));
	}
}