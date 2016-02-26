package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.v0_6.impl.OsmHandler;
import org.xml.sax.SAXException;

import me.chrisswr1.parroute.util.GeoUtils;

/**
 * defines a {@link DataHandler}, which requests data from the Overpass API
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OverpassHandler
implements DataHandler
{
	/**
	 * the {@link URL} of the official Overpass API server
	 * 
	 * @since 0.0.1
	 */
	private static URL										offUrl;
															
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger								LOGGER					= LogManager.getLogger(OverpassHandler.class);
	/**
	 * the default {@link CoordinateReferenceSystem} of the OpenStreetMap
	 * database
	 * 
	 * @since 0.0.1
	 */
	public static final CoordinateReferenceSystem			OSM_CRS;
	/**
	 * placeholder for the bbox in a Overpass QL script
	 * 
	 * @since 0.0.1
	 */
	public static final String								PH_BBOX					= "{{bbox}}";
	/**
	 * the timeout of an Overpass request (in seconds)
	 * 
	 * @since 0.0.1
	 */
	public static final int									REQUEST_TIMEOUT			= 25;
	/**
	 * count of attempts, how often the {@link OverpassHandler} should open the
	 * request
	 * 
	 * @since 0.0.1
	 */
	public static final int									ATTEMPTS				= 3;
	/**
	 * the time to wait before retry an Overpass request (in milliseconds)
	 * 
	 * @since 0.0.1
	 */
	public static final int									RETRY_DELAY				= 1000;
																					
	/**
	 * {@link URL} of the Overpass API endpoint
	 * 
	 * @since 0.0.1
	 */
	private final URL										apiUrl;
															
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
																					
	static
	{
		CoordinateReferenceSystem crs;
		try
		{
			crs = CRS.decode("EPSG:4326");
		}
		catch (FactoryException e)
		{
			crs = DefaultGeographicCRS.WGS84;
		}
		OSM_CRS = crs;
		
		try
		{
			OverpassHandler.offUrl = new URL("https://overpass-api.de/api/");
		}
		catch (MalformedURLException e)
		{
		}
	}
	
	/**
	 * constructor, with given API {@link URL}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param apiUrl the {@link URL} of the
	 */
	public OverpassHandler(URL apiUrl)
	{
		this.apiUrl = apiUrl;
		
		this.relsOfEntity.put(EntityType.Node, new HashSetValuedHashMap<Long, Long>());
		this.relsOfEntity.put(EntityType.Way, new HashSetValuedHashMap<Long, Long>());
		this.relsOfEntity.put(EntityType.Relation, new HashSetValuedHashMap<Long, Long>());
		
		this.allRelsOfEntityStored.put(EntityType.Node, new HashSet<Long>());
		this.allRelsOfEntityStored.put(EntityType.Way, new HashSet<Long>());
		this.allRelsOfEntityStored.put(EntityType.Relation, new HashSet<Long>());
	}
	
	/**
	 * standard constructor, which uses the official Overpass server
	 * 
	 * @since 0.0.1
	 */
	public OverpassHandler()
	{
		this(OverpassHandler.offUrl);
	}
	
	/**
	 * requests {@link Entity}s from the Overpass API and stores them in
	 * {@link OverpassHandler#nodes}, {@link OverpassHandler#ways} and
	 * {@link OverpassHandler#rels}
	 * 
	 * @since 0.0.1
	 * 
	 * @param script the Overpass QL script
	 * @param bbox the bounding box of the request
	 * @throws IOException if an error occurred on request the data from the
	 *             Overpass API or the parsing failed
	 */
	private void requestEntities(String script, Envelope bbox)
	throws IOException
	{
		if (bbox != null)
		{
			DirectPosition lowerCorner;
			DirectPosition upperCorner;
			
			try
			{
				lowerCorner = GeoUtils.transform(bbox.getLowerCorner(), OverpassHandler.OSM_CRS);
				upperCorner = GeoUtils.transform(bbox.getUpperCorner(), OverpassHandler.OSM_CRS);
			}
			catch (TransformException e)
			{
				String msg = "Couldn't transform requested CRS to " + OverpassHandler.OSM_CRS.getName().getCode() + "!";
				OverpassHandler.LOGGER.error(msg, e);
				throw new IOException(msg, e);
			}
			
			String bboxString = lowerCorner.getOrdinate(0) + "," + lowerCorner.getOrdinate(1) + "," + upperCorner.getOrdinate(0) + "," + upperCorner.getOrdinate(1);
			
			script = script.replace(OverpassHandler.PH_BBOX, bboxString);
		}
		else
		{
			script = script.replace("(" + OverpassHandler.PH_BBOX + ")", "");
			script = script.replace(OverpassHandler.PH_BBOX, "");
		}
		
		script = "[out:xml][timeout:" + OverpassHandler.REQUEST_TIMEOUT + "];(" + script + ");out meta;>;out meta qt;";
		
		try
		{
			for (int i = 0; i < OverpassHandler.ATTEMPTS; i++)
			{
				OverpassHandler.LOGGER.debug("Request features from the Overpass API with the following script: " + script);
				
				URLConnection connection = (new URL(this.apiUrl.toString() + "interpreter?data=" + URLEncoder.encode(script, "UTF-8"))).openConnection();
				connection.connect();
				
				if (connection instanceof HttpURLConnection)
				{
					HttpURLConnection httpConnection = (HttpURLConnection)connection;
					
					int statusCode = httpConnection.getResponseCode();
					
					OverpassHandler.LOGGER.debug("Overpass API returned HTTP status code " + statusCode + ".");
					
					if (statusCode == 400)
					{
						String msg = "An error occured during the execution of the overpass query! This is what overpass API returned:\r\n";
						msg += IOUtils.toString(httpConnection.getErrorStream());
						
						OverpassHandler.LOGGER.error(msg);
						throw new IOException(msg);
					}
					else if (statusCode == 429 || statusCode == 504)
					{
						IOUtils.close(httpConnection);
						
						OverpassHandler.LOGGER.debug("Close connection and retry after " + OverpassHandler.RETRY_DELAY + " milliseconds.");
						
						long stopTime = System.currentTimeMillis() + OverpassHandler.RETRY_DELAY;
						while (System.currentTimeMillis() < stopTime)
						{
							;
						}
						
						continue;
					}
					
					InputStream is = httpConnection.getInputStream();
					SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
					
					Sink sink = new Sink()
					{
						@Override
						public void release()
						{
						}
						
						@Override
						public void complete()
						{
						}
						
						@Override
						public void initialize(Map<String, Object> metaData)
						{
						}
						
						@Override
						public void process(EntityContainer entityContainer)
						{
							Entity entity = entityContainer.getEntity();
							long id = entity.getId();
							
							if (entity instanceof Node)
							{
								Node node = (Node)entity;
								OverpassHandler.this.nodes.put(id, node);
							}
							else if (entity instanceof Way)
							{
								Way way = (Way)entity;
								OverpassHandler.this.ways.put(id, way);
								
								for (WayNode wayNode : way.getWayNodes())
								{
									OverpassHandler.this.waysOfNode.put(wayNode.getNodeId(), id);
								}
							}
							else if (entity instanceof Relation)
							{
								Relation rel = (Relation)entity;
								OverpassHandler.this.rels.put(id, rel);
								
								for (RelationMember member : rel.getMembers())
								{
									OverpassHandler.this.relsOfEntity.get(member.getMemberType()).put(member.getMemberId(), id);
								}
							}
						}
					};
					
					parser.parse(is, new OsmHandler(sink, true));
				}
				else
				{
					IOUtils.close(connection);
					throw new IOException("The URL didn't point to a connection using HTTP!");
				}
			}
			
			throw new IOException("Couldn't get a valid reponse from the Overpass API after " + OverpassHandler.ATTEMPTS + " attempts!");
		}
		catch (IOException e)
		{
			String msg = "An error occurred while request the Overpass API!";
			OverpassHandler.LOGGER.error(msg, e);
			throw new IOException(msg, e);
		}
		catch (ParserConfigurationException | SAXException e)
		{
			String msg = "An error occurred on parsing the OpenStreetMap data XML document!";
			OverpassHandler.LOGGER.error(msg, e);
			throw new IOException(msg, e);
		}
	}
	
	/**
	 * gives a {@link Node} from the store, or request it from the API
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
	throws IOException
	{
		if ( ! (this.nodes.containsKey(id)))
		{
			String script = "node(" + id + ");";
			this.requestEntities(script, null);
		}
		
		return this.nodes.get(id);
	}
	
	/**
	 * gives a {@link Way} from the store, or request it from the API
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
	throws IOException
	{
		if ( ! (this.ways.containsKey(id)))
		{
			String script = "way(" + id + ");";
			this.requestEntities(script, null);
		}
		
		return this.ways.get(id);
	}
	
	/**
	 * gives a {@link Relation} from the store, or request it from the API
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
	throws IOException
	{
		if ( ! (this.rels.containsKey(id)))
		{
			String script = "relation(" + id + ");";
			this.requestEntities(script, null);
		}
		
		return this.rels.get(id);
	}
	
	@Override
	public Map<? extends Long, ? extends Way> getWaysOfNode(Node node)
	throws IOException
	{
		long id = node.getId();
		
		if ( ! (this.allWaysOfNodeStored.contains(id)))
		{
			String script = "node(" + id + ");<;";
			this.requestEntities(script, null);
			this.allWaysOfNodeStored.add(id);
		}
		
		Map<Long, Way> res = new HashMap<>();
		for (long wayId : this.waysOfNode.get(id))
		{
			res.put(wayId, this.getWay(wayId));
		}
		
		return res;
	}
	
	/**
	 * gives all {@link Relation}s of which {@code entity} is a member
	 * 
	 * @since 0.0.1
	 * 		
	 * @param entity the {@link Entity} to get the {@link Relation}s from
	 * @return a {@link Map} of all {@link Relation}s, which have {@code entity}
	 *         as a member
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public Map<? extends Long, ? extends Relation> getRelsOfEntity(Entity entity)
	throws IOException
	{
		long id = entity.getId();
		EntityType type = entity.getType();
		
		Set<Long> allStoredSet = this.allRelsOfEntityStored.get(type);
		if ( ! (allStoredSet.contains(id)))
		{
			String script = type.toString().toLowerCase() + "(" + id + ");<;";
			this.requestEntities(script, null);
			allStoredSet.add(id);
		}
		
		Map<Long, Relation> res = new HashMap<>();
		for (long relId : this.relsOfEntity.get(type).get(id))
		{
			res.put(relId, this.getRel(relId));
		}
		
		return res;
	}
}