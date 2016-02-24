package me.chrisswr1.parroute.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * stores {@link Entity}, which are received from the OpenStreetMap database
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmFeatureStore
{
	/**
	 * {@link Map} to store all {@link Entity}s
	 * 
	 * @since 0.0.1
	 */
	private final Map<Long, Entity>	entities	= new HashMap<>();
	/**
	 * {@link Set} of all {@link Node}s, which are stored in
	 * {@code OsmFeatureStore#entities}
	 * 
	 * @since 0.0.1
	 */
	private final Set<Long>			nodes		= new HashSet<>();
	/**
	 * {@link Set} of all {@link Way}s, which are stored in
	 * {@code OsmFeatureStore#entities}
	 * 
	 * @since 0.0.1
	 */
	private final Set<Long>			ways		= new HashSet<>();
	/**
	 * {@link Set} of all {@link Relation}s, which are stored in
	 * {@code OsmFeatureStore#entities}
	 * 
	 * @since 0.0.1
	 */
	private final Set<Long>			rels		= new HashSet<>();
												
	/**
	 * standard constructor
	 * 
	 * @since 0.0.1
	 */
	public OsmFeatureStore()
	{
	}
	
	/**
	 * gives all {@link Node}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @return {@link Map} of all {@link Node}s
	 */
	public Map<Long, ? extends Node> getNodes()
	{
		Map<Long, Node> res = new HashMap<>();
		
		for (long id : this.nodes)
		{
			res.put(id, (Node)this.entities.get(id));
		}
		
		return res;
	}
	
	/**
	 * gives a {@link Node} by its id
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Node}
	 * @return the {@link Node} with id {@code id} or <code>null</code>, if no
	 *         {@link Node} with this id was stored
	 * @see Map#get(Object)
	 */
	public Node getNode(long id)
	{
		if (this.nodes.contains(id))
		{
			return (Node)this.entities.get(id);
		}
		
		return null;
	}
	
	/**
	 * adds a {@link Node}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to add
	 * @return <code>false</code>, if a {@link Node} with the same id was
	 *         already stored, <code>true</code> otherwise
	 * @see Set#add(Object)
	 */
	public boolean addNode(Node node)
	{
		long id = node.getId();
		if ( ! (this.nodes.contains(id)))
		{
			this.entities.put(id, node);
			this.nodes.add(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * adds several {@link Node}s
	 * 
	 * @since 0.0.1
	 * 
	 * @param nodes the set of {@link Node}s (if there are several {@link Node}s
	 *            with the same id, only the first in the {@link Iterator} will
	 *            be added!)
	 * @return <code>true</code>, if any {@link Node} from {@code nodes} was
	 *         added
	 * @see Set#addAll(Collection)
	 */
	public boolean addNodes(Collection<? extends Node> nodes)
	{
		boolean res = false;
		
		for (Node node : nodes)
		{
			if (this.addNode(node))
			{
				res = true;
			}
		}
		
		return res;
	}
	
	/**
	 * removes a {@link Node} by its id from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Node}, which should be removed
	 * @return the {@link Node}, which was stored under {@code id}
	 * @see Map#remove(Object)
	 */
	public Node removeNode(long id)
	{
		if (this.nodes.contains(id))
		{
			this.nodes.remove(id);
			return (Node)this.entities.remove(id);
		}
		
		return null;
	}
	
	/**
	 * removes a {@link Node} from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to remove (removes also any {@link Node}
	 *            with the same id!)
	 * @return the {@link Node}, which was stored under the same id than
	 *         {@code node}
	 * @see OsmFeatureStore#removeNode(long)
	 */
	public Node removeNode(Node node)
	{
		return this.removeNode(node.getId());
	}
	
	/**
	 * gives all {@link Way}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @return {@link Map} of all {@link Way}s
	 */
	public Map<Long, ? extends Way> getWays()
	{
		Map<Long, Way> res = new HashMap<>();
		
		for (long id : this.ways)
		{
			res.put(id, (Way)this.entities.get(id));
		}
		
		return res;
	}
	
	/**
	 * gives a {@link Way} by its id
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Way}
	 * @return the {@link Way} with id {@code id} or <code>null</code>, if no
	 *         {@link Way} with this id was stored
	 * @see Map#get(Object)
	 */
	public Way getWay(long id)
	{
		if (this.ways.contains(id))
		{
			return (Way)this.entities.get(id);
		}
		
		return null;
	}
	
	/**
	 * adds a {@link Way}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param way the {@link Way} to add
	 * @return <code>false</code>, if a {@link Way} with the same id was already
	 *         stored, <code>true</code> otherwise
	 * @see Set#add(Object)
	 */
	public boolean addWay(Way way)
	{
		long id = way.getId();
		if ( ! (this.ways.contains(id)))
		{
			this.entities.put(id, way);
			this.ways.add(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * adds several {@link Way}s
	 * 
	 * @since 0.0.1
	 * 
	 * @param ways the set of {@link Way}s (if there are several {@link Way}s
	 *            with the same id, only the first in the {@link Iterator} will
	 *            be added!)
	 * @return <code>true</code>, if any {@link Way} from {@code ways} was added
	 * @see Set#addAll(Collection)
	 */
	public boolean addWays(Collection<? extends Way> ways)
	{
		boolean res = false;
		
		for (Way way : ways)
		{
			if (this.addWay(way))
			{
				res = true;
			}
		}
		
		return res;
	}
	
	/**
	 * removes a {@link Way} by its id from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Way}, which should be removed
	 * @return the {@link Way}, which was stored under {@code id}
	 * @see Map#remove(Object)
	 */
	public Way removeWay(long id)
	{
		if (this.ways.contains(id))
		{
			this.ways.remove(id);
			return (Way)this.entities.remove(id);
		}
		
		return null;
	}
	
	/**
	 * removes a {@link Way} from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param way the {@link Way} to remove (removes also any {@link Way} with
	 *            the same id!)
	 * @return the {@link Way}, which was stored under the same id than
	 *         {@code way}
	 * @see OsmFeatureStore#removeWay(long)
	 */
	public Way removeWay(Way way)
	{
		return this.removeWay(way.getId());
	}
	
	/**
	 * gives all {@link Relation}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @return map of all {@link Relation}s
	 */
	public Map<Long, ? extends Relation> getRels()
	{
		Map<Long, Relation> res = new HashMap<>();
		
		for (long id : this.rels)
		{
			res.put(id, (Relation)this.entities.get(id));
		}
		
		return res;
	}
	
	/**
	 * gives a {@link Relation} by its id
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Relation}
	 * @return the {@link Relation} with id {@code id} or <code>null</code>, if
	 *         no {@link Relation} with this id was stored
	 * @see Map#get(Object)
	 */
	public Relation getRel(long id)
	{
		if (this.rels.contains(id))
		{
			return (Relation)this.entities.get(id);
		}
		
		return null;
	}
	
	/**
	 * adds a {@link Relation}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param rel the {@link Relation} to add
	 * @return <code>false</code>, if a {@link Relation} with the same id was
	 *         already stored, <code>true</code> otherwise
	 * @see Set#add(Object)
	 */
	public boolean addRel(Relation rel)
	{
		long id = rel.getId();
		if ( ! (this.rels.contains(id)))
		{
			this.entities.put(id, rel);
			this.rels.add(id);
			return true;
		}
		
		return false;
	}
	
	/**
	 * adds several {@link Relation}s
	 * 
	 * @since 0.0.1
	 * 
	 * @param rels the set of {@link Relation}s (if there are several
	 *            {@link Relation}s with the same id, only the first in the
	 *            {@link Iterator} will be added!)
	 * @return <code>true</code>, if any {@link Relation} from {@code rels} was
	 *         added
	 * @see Set#addAll(Collection)
	 */
	public boolean addRels(Collection<? extends Relation> rels)
	{
		boolean res = false;
		
		for (Relation rel : rels)
		{
			if (this.addRel(rel))
			{
				res = true;
			}
		}
		
		return res;
	}
	
	/**
	 * removes a {@link Relation} by its id from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Relation}, which should be removed
	 * @return the {@link Relation}, which was stored under {@code id}
	 * @see Map#remove(Object)
	 */
	public Relation removeRel(long id)
	{
		if (this.rels.contains(id))
		{
			this.rels.remove(id);
			return (Relation)this.entities.remove(id);
		}
		
		return null;
	}
	
	/**
	 * removes a {@link Relation} from the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @param rel the {@link Relation} to remove (removes also any
	 *            {@link Relation} with the same id!)
	 * @return the {@link Relation}, which was stored under the same id than
	 *         {@code rel}
	 * @see OsmFeatureStore#removeRel(long)
	 */
	public Relation removeRel(Relation rel)
	{
		return this.removeRel(rel.getId());
	}
	
	/**
	 * clears the store
	 * 
	 * @since 0.0.1
	 * 		
	 * @see Collection#clear()
	 */
	public void clear()
	{
		this.nodes.clear();
		this.ways.clear();
		this.rels.clear();
	}
	
	/**
	 * gives the count of all stored {@link Entity}s
	 * 
	 * @since 0.0.1
	 * 
	 * @return the count of all stored {@link Entity}s
	 */
	public int size()
	{
		return this.nodes.size() + this.ways.size() + this.rels.size();
	}
	
	/**
	 * does this store contain nothing?
	 * 
	 * @since 0.0.1
	 * 		
	 * @return <code>true</code>, if this store is empty, <code>false</code>
	 *         otherwise
	 * @see Collection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.nodes.isEmpty() && this.ways.isEmpty() && this.rels.isEmpty();
	}
}