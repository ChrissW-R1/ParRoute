package me.chrisswr1.parroute;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import me.chrisswr1.parroute.entities.Oneway;
import me.chrisswr1.parroute.io.DataReceiver;

/**
 * stores all received OpenStreetMap features and provides additional methods
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class DataHandler
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger								LOGGER					= LogManager.getLogger(DataHandler.class);
																					
	/**
	 * the {@link DataReceiver} to get the OpenStreetMap features from
	 * 
	 * @since 0.0.1
	 */
	private final DataReceiver								receiver;
															
	/**
	 * stores all received {@link Node}s
	 * 
	 * @since 0.0.1
	 */
	private final Map<Long, Node>							nodes					= new HashMap<>();
	/**
	 * stores all received {@link Way}s
	 * 
	 * @since 0.0.1
	 */
	private final Map<Long, Way>							ways					= new HashMap<>();
	/**
	 * stores all received {@link Relation}s
	 * 
	 * @since 0.0.1
	 */
	private final Map<Long, Relation>						rels					= new HashMap<>();
	/**
	 * stores mappings of {@link Way}s, which a {@link Node} is a member of
	 * 
	 * @since 0.0.1
	 */
	private final SetValuedMap<Long, Long>					waysOfNode				= new HashSetValuedHashMap<>();
	/**
	 * stores mappings of {@link Relation}s, which an {@link Entity} is a member
	 * of
	 * 
	 * @since 0.0.1
	 */
	private final Map<EntityType, SetValuedMap<Long, Long>>	relsOfEntity			= new HashMap<>();
																					
	/**
	 * stores, from which {@link Node}s the {@link Way}s were already received
	 * 
	 * @since 0.0.1
	 */
	private final Set<Long>									allWaysOfNodeStored		= new HashSet<>();
	/**
	 * stores, from which {@link Entity}s the {@link Relation}s were already
	 * received
	 * 
	 * @since 0.0.1
	 */
	private final Map<EntityType, Set<Long>>				allRelsOfEntityStored	= new HashMap<>();
																					
	/**
	 * constructor, with given {@link DataReceiver}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param receiver the {@link DataReceiver} to get the {@link Entity}s from
	 */
	public DataHandler(DataReceiver receiver)
	{
		this.receiver = receiver;
		
		this.relsOfEntity.put(EntityType.Node, new HashSetValuedHashMap<Long, Long>());
		this.relsOfEntity.put(EntityType.Way, new HashSetValuedHashMap<Long, Long>());
		this.relsOfEntity.put(EntityType.Relation, new HashSetValuedHashMap<Long, Long>());
		
		this.allRelsOfEntityStored.put(EntityType.Node, new HashSet<Long>());
		this.allRelsOfEntityStored.put(EntityType.Way, new HashSet<Long>());
		this.allRelsOfEntityStored.put(EntityType.Relation, new HashSet<Long>());
	}
	
	/**
	 * gives the {@link DataReceiver}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link DataReceiver} to get the {@link Entity}s from
	 */
	public DataReceiver getReceiver()
	{
		return this.receiver;
	}
	
	/**
	 * stores an {@link Entity} and all indices of it
	 * 
	 * @since 0.0.1
	 * 
	 * @param entity the {@link Entity} to store
	 * @return the previous stored {@link Entity}, with the same
	 *         {@link EntityType} and id or <code>null</code>, if there was
	 *         previously nothing stored
	 * 
	 * @see DataHandler#store(Collection)
	 */
	public Entity store(Entity entity)
	{
		if (entity != null)
		{
			long id = entity.getId();
			
			DataHandler.LOGGER.trace("Staving " + entity + ".");
			
			if (entity instanceof Node)
			{
				DataHandler.LOGGER.trace("Found node with id " + id + ".");
				Node node = (Node)entity;
				
				DataHandler.LOGGER.trace("Save " + node + " itself.");
				return this.nodes.put(id, node);
			}
			if (entity instanceof Way)
			{
				DataHandler.LOGGER.trace("Found way with id " + id + ".");
				Way way = (Way)entity;
				
				for (WayNode wayNode : way.getWayNodes())
				{
					long nodeId = wayNode.getNodeId();
					
					DataHandler.LOGGER.trace("Store node with id " + nodeId + " as a member of " + way + ".");
					this.waysOfNode.put(nodeId, id);
				}
				
				DataHandler.LOGGER.trace("Save " + way + " itself.");
				return this.ways.put(id, way);
			}
			if (entity instanceof Relation)
			{
				DataHandler.LOGGER.trace("Found relation with id " + id + ".");
				Relation rel = (Relation)entity;
				
				for (RelationMember member : rel.getMembers())
				{
					EntityType memberType = member.getMemberType();
					long memberId = member.getMemberId();
					
					DataHandler.LOGGER.trace("Store " + memberType + " with id " + memberId + " as a member of " + rel + ".");
					this.relsOfEntity.get(memberType).put(memberId, id);
				}
				
				DataHandler.LOGGER.trace("Save " + rel + " itself.");
				return this.rels.put(id, rel);
			}
		}
		
		DataHandler.LOGGER.error(entity + " have a unkown type or is null, so it couldn't stored!");
		return null;
	}
	
	/**
	 * stores a {@link Collection} of {@link Entity}s
	 * 
	 * @since 0.0.1
	 * 
	 * @param entities all {@link Entity}s to store
	 * 
	 * @see DataHandler#store(Entity)
	 */
	public void store(Collection<? extends Entity> entities)
	{
		for (Entity entity : entities)
		{
			this.store(entity);
		}
	}
	
	/**
	 * gives a {@link Node} from the store
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Node}
	 * @return the {@link Node} with id {@code id} or <code>null</code>, if it
	 *         doesn't exist
	 * @throws IOException if the requested {@link Relation}s couldn't received
	 */
	public Node getNode(long id)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get node with id " + id + ".");
		
		if ( ! (this.nodes.containsKey(id)))
		{
			DataHandler.LOGGER.debug("Node with id " + id + " is currently not stored. Requesting it.");
			this.store(this.getReceiver().getNode(id));
		}
		
		return this.nodes.get(id);
	}
	
	/**
	 * gives a {@link Way} from the store
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Way}
	 * @return the {@link Way} with id {@code id} or <code>null</code>, if it
	 *         doesn't exist
	 * @throws IOException if the requested {@link Relation}s couldn't received
	 */
	public Way getWay(long id)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get way with id " + id + ".");
		
		if ( ! (this.ways.containsKey(id)))
		{
			DataHandler.LOGGER.debug("Way with id " + id + " is currently not stored. Requesting it.");
			this.store(this.getReceiver().getWay(id));
		}
		
		return this.ways.get(id);
	}
	
	/**
	 * gives a {@link Relation} from the store
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the {@link Relation}
	 * @return the {@link Relation} with id {@code id} or <code>null</code>, if
	 *         it doesn't exist
	 * @throws IOException if the requested {@link Relation}s couldn't received
	 */
	public Relation getRel(long id)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get relation with id " + id + ".");
		
		if ( ! (this.rels.containsKey(id)))
		{
			DataHandler.LOGGER.debug("Relation with id " + id + " is currently not stored. Requesting it.");
			this.store(this.getReceiver().getRel(id));
		}
		
		return this.rels.get(id);
	}
	
	/**
	 * requests all {@link Way}s on which {@code node} is a part from
	 * 
	 * @since 0.0.1
	 * 
	 * @param node the {@link Node} to get the {@link Way}s from
	 * @return the {@link Way}s which contains {@code node}
	 * @throws IOException if the requested {@link Relation}s couldn't received
	 */
	public Set<Way> getWaysOfNode(Node node)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get all ways, on which " + node + " is a part from.");
		
		long id = node.getId();
		
		if ( ! (this.allWaysOfNodeStored.contains(id)))
		{
			DataHandler.LOGGER.debug("Not sure, if all ways of " + node + " were stored. Requesting them.");
			this.store(this.getReceiver().getWaysOfNode(node));
			
			DataHandler.LOGGER.trace("Mark all ways of " + node + " as already stored.");
			this.allWaysOfNodeStored.add(id);
		}
		
		Set<Way> res = new HashSet<>();
		for (long wayId : this.waysOfNode.get(id))
		{
			res.add(this.getWay(wayId));
		}
		
		return res;
	}
	
	/**
	 * gives all {@link Relation}s of which {@code entity} is a member
	 * 
	 * @since 0.0.1
	 * 
	 * @param entity the {@link Entity} to get the {@link Relation}s from
	 * @return a {@link Set} of all {@link Relation}s, which have {@code entity}
	 *         as a member
	 * @throws IOException if the requested {@link Relation}s couldn't received
	 */
	public Set<Relation> getRelsOfEntity(Entity entity)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get all relations, which have " + entity + " as a member.");
		
		long id = entity.getId();
		EntityType type = entity.getType();
		
		Set<Long> allStoredSet = this.allRelsOfEntityStored.get(type);
		if ( ! (allStoredSet.contains(id)))
		{
			DataHandler.LOGGER.debug("Not sure, if all relations of " + entity + " were stored. Requesting them.");
			this.store(this.getReceiver().getRelsOfEntity(entity));
			
			DataHandler.LOGGER.trace("Mark all relations of " + entity + " as already stored.");
			allStoredSet.add(id);
		}
		
		Set<Relation> res = new HashSet<>();
		for (long relId : this.relsOfEntity.get(type).get(id))
		{
			res.add(this.getRel(relId));
		}
		
		return res;
	}
	
	/**
	 * gives all indices of a {@link Node} in a {@link Way}
	 * 
	 * @since 0.0.1
	 * 
	 * @param node the {@link Node} to get the indices of
	 * @param way the {@link Way} in which to search {@code node} for
	 * @return a {@link Set} of all indices of {@code node} in {@code way}
	 */
	public Set<Integer> getIndices(Node node, Way way)
	{
		DataHandler.LOGGER.trace("Get all indices of " + node + " in " + way + ".");
		
		long nodeId = node.getId();
		
		Set<Integer> res = new HashSet<>();
		int idx = 0;
		for (WayNode wayNode : way.getWayNodes())
		{
			if (wayNode.getNodeId() == nodeId)
			{
				res.add(idx);
			}
			
			idx++;
		}
		
		return res;
	}
	
	/**
	 * gives all direct neighbors of a {@link Node} in a specific {@link Way}
	 * 
	 * @since 0.0.1
	 * 
	 * @param node the {@link Node} to get the neighbors from
	 * @param way the {@link Way} to search in
	 * @return a {@link Set} of all neighbors of {@code node} in {@code way}
	 * @throws IOException if the necessary data couldn't get
	 */
	public Set<Node> getNeighbors(Node node, Way way)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get all direct neighbors of " + node + " in " + way + ".");
		
		Set<Node> res = new HashSet<>();
		Oneway oneway = Oneway.get(way);
		
		for (int idx : this.getIndices(node, way))
		{
			List<WayNode> wayNodes = way.getWayNodes();
			
			if (oneway != Oneway.INDICATED)
			{
				int leftIdx = idx - 1;
				if (leftIdx >= 0)
				{
					long leftId = wayNodes.get(leftIdx).getNodeId();
					
					if (leftId != node.getId())
					{
						Node leftNode = this.getNode(leftId);
						
						DataHandler.LOGGER.trace("Found " + leftNode + " as a neighbor before " + node + " in " + way + ".");
						
						res.add(leftNode);
					}
				}
			}
			
			if (oneway != Oneway.OPPOSITE)
			{
				int rightIdx = idx + 1;
				if (rightIdx < wayNodes.size())
				{
					long rightId = wayNodes.get(rightIdx).getNodeId();
					
					if (rightId != node.getId())
					{
						Node rightNode = this.getNode(rightId);
						
						DataHandler.LOGGER.trace("Found " + rightNode + " as a neighbor after " + node + " in " + way + ".");
						
						res.add(rightNode);
					}
				}
			}
		}
		
		return res;
	}
	
	/**
	 * gives all direct neighbors of a {@link Node}
	 * 
	 * @since 0.0.1
	 * 
	 * @param node the {@link Node} to search the neighbors for
	 * @return a {@link Set} of all found neighbors
	 * @throws IOException if the necessary data couldn't get
	 */
	public Set<Node> getNeighbours(Node node)
	throws IOException
	{
		DataHandler.LOGGER.trace("Get all direct neighbors of " + node + " in all ways.");
		
		Set<Node> res = new HashSet<>();
		
		for (Way way : this.getWaysOfNode(node))
		{
			res.addAll(this.getNeighbors(node, way));
		}
		
		return res;
	}
	
	/**
	 * gives all {@link Way}s, which are a direct connection from {@code from}
	 * to {@code to}
	 * 
	 * @since 0.0.1
	 * 
	 * @param from the start {@link Node} of the connection
	 * @param to the destination {@link Node} of the connection
	 * @return a {@link Set}, which contains all direct connections
	 * @throws IOException if the necessary data couldn't get
	 */
	public Set<Way> getConnectionWays(Node from, Node to)
	throws IOException
	{
		Set<Way> ways = new HashSet<>(this.getWaysOfNode(from));
		ways.retainAll(this.getWaysOfNode(to));
		
		Set<Way> res = new HashSet<>();
		for (Way way : ways)
		{
			if (this.getNeighbors(from, way).contains(to))
			{
				res.add(way);
			}
		}
		
		return res;
	}
	
	/**
	 * gives a {@link Set} of all stored {@link Entity}s
	 * 
	 * @since 0.0.1
	 * 
	 * @return a {@link Set} of all stored {@link Entity}s bundled to
	 *         {@link EntityContainer}s
	 */
	public Set<EntityContainer> getContainers()
	{
		Set<EntityContainer> res = new LinkedHashSet<>();
		
		for (Node node : this.nodes.values())
		{
			res.add(new NodeContainer(node));
		}
		for (Way way : this.ways.values())
		{
			res.add(new WayContainer(way));
		}
		for (Relation rel : this.rels.values())
		{
			res.add(new RelationContainer(rel));
		}
		
		return res;
	}
}