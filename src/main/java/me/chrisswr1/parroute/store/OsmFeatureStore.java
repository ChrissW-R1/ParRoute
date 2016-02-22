package me.chrisswr1.parroute.store;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<Long, Node>		nodes	= new HashMap<>();
	private final Map<Long, Way>		ways	= new HashMap<>();
	private final Map<Long, Relation>	rels	= new HashMap<>();
												
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
	public Map<Long, Node> getNodes()
	{
		return new HashMap<>(this.nodes);
	}
	
	/**
	 * gives all {@link Way}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @return {@link Map} of all {@link Way}s
	 */
	public Map<Long, Way> getWays()
	{
		return new HashMap<>(this.ways);
	}
	
	/**
	 * gives all {@link Relation}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @return map of all {@link Relation}s
	 */
	public Map<Long, Relation> getRels()
	{
		return new HashMap<>(this.rels);
	}
	
	public Node getNode(long id)
	{
		return this.nodes.get(id);
	}
	
	public Node putNode(long id, Node node)
	{
		return this.nodes.put(id, node);
	}
	
	public Way getWay(long id)
	{
		return this.ways.get(id);
	}
	
	public Way putWay(long id, Way way)
	{
		return this.ways.put(id, way);
	}
	
	public Relation getRel(long id)
	{
		return this.rels.get(id);
	}
	
	public Relation putRel(long id, Relation rel)
	{
		return this.rels.put(id, rel);
	}
}