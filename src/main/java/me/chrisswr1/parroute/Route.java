package me.chrisswr1.parroute;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import me.chrisswr1.parroute.io.OverpassHandler;
import me.chrisswr1.parroute.util.CollectionUtils;
import me.chrisswr1.parroute.util.GeoUtils;

/**
 * defines a route between two {@link Node}s
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class Route
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger		LOGGER		= LogManager.getLogger(Route.class);
												
	/**
	 * the {@link OverpassHandler} to get the {@link Entity}s from
	 * 
	 * @since 0.0.1
	 */
	private final OverpassHandler	dataHandler;
	/**
	 * the {@link Node}, where the {@link Route} begins
	 * 
	 * @since 0.0.1
	 */
	private final Node				start;
	/**
	 * the destination {@link Node} of the {@link Route}
	 * 
	 * @since 0.0.1
	 */
	private final Node				dest;
	/**
	 * if a calculation was ever called
	 * 
	 * @since 0.0.1
	 */
	private boolean					notCalc		= true;
												
	/**
	 * stores the predecessor of already proceeded {@link Node}s
	 * 
	 * @since 0.0.1
	 */
	private Map<Long, Long>			predecessor	= new HashMap<>();
	/**
	 * stores all loaded {@link Node} id, which have to be proceeded
	 * 
	 * @since 0.0.1
	 */
	private Map<Long, Double>		openList	= new HashMap<>();
	/**
	 * stores all {@link Node} id, to which the shortest route was already found
	 * 
	 * @since 0.0.1
	 */
	private Set<Long>				closedList	= new HashSet<>();
	/**
	 * stores the calculated costs of the proceeded {@link Node}s
	 * 
	 * @since 0.0.1
	 */
	private Map<Long, Double>		calcCosts	= new HashMap<>();
												
	/**
	 * standard constructor
	 * 
	 * @since 0.0.1
	 * 		
	 * @param dataHandler the {@link OverpassHandler} to get the {@link Entity}s
	 *            from
	 * @param start the {@link Node}, where the {@link Route} should begin
	 * @param dest the destination, which should be reached by the {@link Route}
	 */
	public Route(OverpassHandler dataHandler, Node start, Node dest)
	{
		this.dataHandler = dataHandler;
		this.start = start;
		this.dest = dest;
	}
	
	@Override
	public String toString()
	{
		long destId = this.getDest().getId();
		String res = this.getStart().getId() + " --> " + destId + ": ";
		
		if (this.notCalc)
		{
			return res + "Not calculated until yet!";
		}
		
		LinkedHashSet<Long> path = new LinkedHashSet<>();
		long preId = this.predecessor.get(this.getDest().getId());
		while (true)
		{
			path.add(preId);
			
			try
			{
				preId = this.predecessor.get(preId);
			}
			catch (NullPointerException e)
			{
				break;
			}
		}
		
		if (path.isEmpty())
		{
			return res + "Couldn't found any route!";
		}
		
		Iterator<Long> iter = (new LinkedList<>(path)).descendingIterator();
		while (iter.hasNext())
		{
			res += iter.next() + ", ";
		}
		
		return res + destId;
	}
	
	/**
	 * calculates the distance between two {@link Node}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node1 the first {@link Node}
	 * @param node2 the second {@link Node}
	 * @return the distance between {@code node1} and {@code node2}
	 */
	private static double calcDistance(Node node1, Node node2)
	{
		return GeoUtils.getDistance(GeoUtils.toPosition(node1), GeoUtils.toPosition(node2));
	}
	
	/**
	 * gives the {@link OverpassHandler} to get the {@link Entity}s from
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link OverpassHandler} to get the {@link Entity}s from
	 */
	public OverpassHandler getDataHandler()
	{
		return this.dataHandler;
	}
	
	/**
	 * gives the {@link Node}, where the {@link Route} starts
	 * 
	 * @since 0.0.1
	 * 
	 * @return the start {@link Node}
	 */
	public Node getStart()
	{
		return this.start;
	}
	
	/**
	 * gives the destination of the {@link Route}
	 * 
	 * @since 0.0.1
	 * 
	 * @return the destination
	 */
	public Node getDest()
	{
		return this.dest;
	}
	
	/**
	 * gives the calculated costs of {@code node}<br>
	 * If no costs stored a <code>0</code> will be returned
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to get the calculated costs from
	 * @return the calculated costs
	 */
	private double getCalcCosts(Node node)
	{
		double res;
		
		try
		{
			res = this.calcCosts.get(node.getId());
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
	
	/**
	 * expands the {@link Route#openList} with the neighbor {@link Node}s of
	 * {@code nodeId}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to get the neighbors from
	 * @throws IOException if some {@link Entity}s couldn't got from the
	 *             {@link Route#dataHandler}
	 */
	private void expand(Node node)
	throws IOException
	{
		OverpassHandler dataHandler = this.getDataHandler();
		
		for (Node successor : dataHandler.getNeighbours(node))
		{
			long successorId = successor.getId();
			
			if (this.closedList.contains(successorId))
			{
				continue;
			}
			
			double tentative_g = this.getCalcCosts(node) + Route.calcDistance(node, successor);
			
			if (this.openList.containsKey(successorId) && tentative_g >= this.getCalcCosts(successor))
			{
				continue;
			}
			
			this.predecessor.put(successorId, node.getId());
			this.calcCosts.put(successorId, tentative_g);
			
			this.openList.put(successorId, tentative_g + Route.calcDistance(successor, this.getDest()));
		}
	}
	
	/**
	 * calculates the {@link Route}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return <code>true</code> if a path was found, <code>false</code>
	 *         otherwise
	 * @throws IOException if some {@link Entity}s couldn't got from the
	 *             {@link Route#dataHandler}
	 */
	public boolean calc()
	throws IOException
	{
		this.openList.put(this.getStart().getId(), 0D);
		this.notCalc = false;
		
		do
		{
			long currentNode = CollectionUtils.min(this.openList).getKey();
			Route.LOGGER.debug("Proceeding node " + currentNode);
			this.openList.remove(currentNode);
			
			Node node = this.getDataHandler().getNode(currentNode);
			double lat = node.getLatitude();
			double lng = node.getLongitude();
			double diff = 0.0000001;
			URLConnection connection = (new URL("http://127.0.0.1:8111/load_and_zoom?left=" + (lng - diff) + "&top=" + (lat + diff) + "&right=" + (lng + diff) + "&bottom=" + (lat - diff) + "&select=node" + currentNode)).openConnection();
			connection.connect();
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				httpConnection.getResponseCode();
			}
			else
			{
				Route.LOGGER.error("No HTTP endpoint found on JOSM!");
			}
			IOUtils.close(connection);
			
			if (currentNode == this.getDest().getId())
			{
				return true;
			}
			
			this.closedList.add(currentNode);
			this.expand(node);
		}
		while ( ! (this.openList.isEmpty()));
		
		return false;
	}
}