package me.chrisswr1.parroute.util;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.AbstractHashedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * defines a {@link MultiKeyMap}, in which the order of the keys is irrelevant
 * <br>
 * Thus {@code map.get(key1, key2)} returns the same as
 * {@code map.get(key2, key1)}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 * 		
 * @param <K> the type of the keys
 * @param <V> the value type
 * @see MultiKeyMap
 */
public class MixedMultiKeyMap<K, V>
extends MultiKeyMap<K, V>
{
	/**
	 * serial version unique identifier
	 * 
	 * @since 0.0.1
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * standard constructor
	 * 
	 * @since 0.0.1
	 * 		
	 * @see MultiKeyMap#MultiKeyMap()
	 */
	public MixedMultiKeyMap()
	{
	}
	
	/**
	 * constructor that decorates the specified map
	 * 
	 * @since 0.0.1
	 * 		
	 * @param map the map to decorate
	 * @see MultiKeyMap#MultiKeyMap(AbstractHashedMap)
	 */
	public MixedMultiKeyMap(AbstractHashedMap<MultiKey<? extends K>, V> map)
	{
		super(map);
	}
	
	@Override
	public V get(Object key1, Object key2)
	{
		if (super.containsKey(key2, key1))
		{
			return super.get(key2, key1);
		}
		else
		{
			return super.get(key1, key2);
		}
	}
	
	@Override
	public boolean containsKey(Object key1, Object key2)
	{
		if (super.containsKey(key1, key2))
		{
			return true;
		}
		else
		{
			return super.containsKey(key2, key1);
		}
	}
	
	@Override
	public V put(K key1, K key2, V value)
	{
		V res = this.removeMultiKey(key1, key2);
		
		super.put(key1, key2, value);
		
		return res;
	}
	
	@Override
	public V removeMultiKey(Object key1, Object key2)
	{
		V res = null;
		
		Object[] keys =
		{
				key1,
				key2
		};
		for (int i = 0; i < keys.length; i++)
		{
			boolean found = super.containsKey(keys[0], keys[1]);
			V removed = super.removeMultiKey(keys[0], keys[1]);
			
			if (found)
			{
				res = removed;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return res;
	}
	
	@Override
	public V get(Object key1, Object key2, Object key3)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2]))
			{
				return super.get(keys[0], keys[1], keys[2]);
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return super.get(key1, key2, key3);
	}
	
	@Override
	public boolean containsKey(Object key1, Object key2, Object key3)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2]))
			{
				return true;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return false;
	}
	
	@Override
	public V put(K key1, K key2, K key3, V value)
	{
		V res = this.removeMultiKey(key1, key2, key3);
		
		super.put(key1, key2, key3, value);
		
		return res;
	}
	
	@Override
	public V removeMultiKey(Object key1, Object key2, Object key3)
	{
		V res = null;
		
		Object[] keys =
		{
				key1,
				key2,
				key3
		};
		for (int i = 0; i < keys.length; i++)
		{
			boolean found = super.containsKey(keys[0], keys[1], keys[2]);
			V removed = super.removeMultiKey(keys[0], keys[1], keys[2]);
			
			if (found)
			{
				res = removed;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return res;
	}
	
	@Override
	public V get(Object key1, Object key2, Object key3, Object key4)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2], keys[3]))
			{
				return super.get(keys[0], keys[1], keys[2], keys[3]);
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return super.get(key1, key2, key3, key4);
	}
	
	@Override
	public boolean containsKey(Object key1, Object key2, Object key3, Object key4)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2], keys[3]))
			{
				return true;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return false;
	}
	
	@Override
	public V put(K key1, K key2, K key3, K key4, V value)
	{
		V res = this.removeMultiKey(key1, key2, key3, key4);
		
		super.put(key1, key2, key3, key4, value);
		
		return res;
	}
	
	@Override
	public V removeMultiKey(Object key1, Object key2, Object key3, Object key4)
	{
		V res = null;
		
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4
		};
		for (int i = 0; i < keys.length; i++)
		{
			boolean found = super.containsKey(keys[0], keys[1], keys[2], keys[3]);
			V removed = super.removeMultiKey(keys[0], keys[1], keys[2], keys[3]);
			
			if (found)
			{
				res = removed;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return res;
	}
	
	@Override
	public V get(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4,
				key5
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2], keys[3], keys[4]))
			{
				return super.get(keys[0], keys[1], keys[2], keys[3], keys[4]);
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return super.get(key1, key2, key3, key4, key5);
	}
	
	@Override
	public boolean containsKey(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4,
				key5
		};
		for (int i = 0; i < keys.length; i++)
		{
			if (super.containsKey(keys[0], keys[1], keys[2], keys[3], keys[4]))
			{
				return true;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return false;
	}
	
	@Override
	public V put(K key1, K key2, K key3, K key4, K key5, V value)
	{
		V res = this.removeMultiKey(key1, key2, key3, key4, key5);
		
		super.put(key1, key2, key3, key4, key5, value);
		
		return res;
	}
	
	@Override
	public V removeMultiKey(Object key1, Object key2, Object key3, Object key4, Object key5)
	{
		V res = null;
		
		Object[] keys =
		{
				key1,
				key2,
				key3,
				key4,
				key5
		};
		for (int i = 0; i < keys.length; i++)
		{
			boolean found = super.containsKey(keys[0], keys[1], keys[2], keys[3], keys[4]);
			V removed = super.removeMultiKey(keys[0], keys[1], keys[2], keys[3], keys[4]);
			
			if (found)
			{
				res = removed;
			}
			
			keys = ArrayUtils.shift(keys, 1);
		}
		
		return res;
	}
}