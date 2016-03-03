package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import me.chrisswr1.parroute.DataHandler;

/**
 * defines a receiver of OpenStreetMap features
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public interface DataReceiver
{
	/**
	 * were all {@link Entity}s from this source already stored in the
	 * {@link DataHandler}?
	 * 
	 * @since 0.0.1
	 * 		
	 * @return <code>true</code> if all {@link Entity} were stored,
	 *         <code>false</code> otherwise
	 */
	public boolean isAllStored();
	
	/**
	 * requests a {@link Node}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Node}
	 * @return the {@link Node} with id {@code id} or <code>null</code>, if it
	 *         doesn't exist
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public Node getNode(long id)
	throws IOException;
	
	/**
	 * requests a {@link Way}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Way}
	 * @return the {@link Way} with id {@code id} or <code>null</code>, if it
	 *         doesn't exist
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public Way getWay(long id)
	throws IOException;
	
	/**
	 * requests a {@link Relation}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param id the id of the {@link Relation}
	 * @return the {@link Relation} with id {@code id} or <code>null</code>, if
	 *         it doesn't exist
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public Relation getRel(long id)
	throws IOException;
	
	/**
	 * requests all {@link Way}s on which {@code node} is a part from
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to get the {@link Way}s from
	 * @return the {@link Way}s which contains {@code node}
	 * @throws IOException if the connection couldn't established or the
	 *             response couldn't parsed
	 */
	public Set<Way> getWaysOf(Node node)
	throws IOException;
	
	/**
	 * gives all {@link Relation}s of which {@code entity} is a member
	 * 
	 * @since 0.0.1
	 * 		
	 * @param entity the {@link Entity} to get the {@link Relation}s from
	 * @return a {@link Set} of all {@link Relation}s, which have {@code entity}
	 *         as a member
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public Set<Relation> getRelsOf(Entity entity)
	throws IOException;
}