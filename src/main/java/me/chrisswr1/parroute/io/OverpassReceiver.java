package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.v0_6.impl.OsmHandler;
import org.xml.sax.SAXException;

import me.chrisswr1.parroute.DataHandler;
import me.chrisswr1.parroute.util.GeoUtils;

/**
 * defines a {@link DataReceiver}, which requests data from the Overpass API
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OverpassReceiver
implements DataReceiver
{
	/**
	 * the {@link URL} of the official Overpass API server
	 * 
	 * @since 0.0.1
	 */
	private static URL			offUrl;
								
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger	LOGGER			= LogManager.getLogger(OverpassReceiver.class);
	/**
	 * placeholder for the bbox in a Overpass QL script
	 * 
	 * @since 0.0.1
	 */
	public static final String	PH_BBOX			= "{{bbox}}";
	/**
	 * the timeout of an Overpass request (in seconds)
	 * 
	 * @since 0.0.1
	 */
	public static final int		REQUEST_TIMEOUT	= 25;
	/**
	 * count of attempts, how often the {@link OverpassReceiver} should open the
	 * request
	 * 
	 * @since 0.0.1
	 */
	public static final int		ATTEMPTS		= 3;
	/**
	 * the time to wait before retry an Overpass request (in milliseconds)
	 * 
	 * @since 0.0.1
	 */
	public static final int		RETRY_DELAY		= 1000;
												
	/**
	 * the {@link DataHandler}, in which all received {@link Entity}s will
	 * stored
	 * 
	 * @since 0.0.1
	 */
	private DataHandler			store			= null;
	/**
	 * {@link URL} of the Overpass API endpoint
	 * 
	 * @since 0.0.1
	 */
	private final URL			apiUrl;
								
	static
	{
		String offUrlString = "http://overpass-api.de/api/";
		try
		{
			OverpassReceiver.offUrl = new URL(offUrlString);
		}
		catch (MalformedURLException e)
		{
			OverpassReceiver.LOGGER.error("URL to official Overpass API endpoint is malformed: " + offUrlString, e);
		}
	}
	
	/**
	 * constructor, with given {@link DataHandler} and API {@link URL}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param apiUrl the {@link URL} of the
	 */
	public OverpassReceiver(URL apiUrl)
	{
		this.apiUrl = apiUrl;
	}
	
	/**
	 * standard constructor, which uses the official Overpass server
	 * 
	 * @since 0.0.1
	 */
	public OverpassReceiver()
	{
		this(OverpassReceiver.offUrl);
	}
	
	/**
	 * requests {@link Entity}s from the Overpass API and stores them in
	 * {@link OverpassReceiver#store}
	 * 
	 * @since 0.0.1
	 * 
	 * @param script the Overpass QL script
	 * @param bbox the bounding box of the request
	 * @return a {@link Set} of all received {@link Entity}s
	 * @throws IOException if an error occurred on request the data from the
	 *             Overpass API or the parsing failed
	 */
	private Set<Entity> requestEntities(String script, Envelope bbox)
	throws IOException
	{
		if (bbox != null)
		{
			OverpassReceiver.LOGGER.trace("Bounding box was given: " + bbox);
			
			DirectPosition lowerCorner;
			DirectPosition upperCorner;
			
			try
			{
				lowerCorner = GeoUtils.transform(bbox.getLowerCorner(), GeoUtils.OSM_CRS);
				upperCorner = GeoUtils.transform(bbox.getUpperCorner(), GeoUtils.OSM_CRS);
			}
			catch (TransformException e)
			{
				String msg = "Couldn't transform requested CRS to " + GeoUtils.OSM_CRS.getName().getCode() + "!";
				OverpassReceiver.LOGGER.error(msg, e);
				throw new IOException(msg, e);
			}
			
			String bboxString = lowerCorner.getOrdinate(0) + "," + lowerCorner.getOrdinate(1) + "," + upperCorner.getOrdinate(0) + "," + upperCorner.getOrdinate(1);
			
			script = script.replace(OverpassReceiver.PH_BBOX, bboxString);
			
			OverpassReceiver.LOGGER.trace("Replaced all " + OverpassReceiver.PH_BBOX + " with real bounding box: " + bboxString);
		}
		else
		{
			script = script.replace("(" + OverpassReceiver.PH_BBOX + ")", "");
			script = script.replace(OverpassReceiver.PH_BBOX, "");
			
			OverpassReceiver.LOGGER.debug("No bounding box was given. Removed all placeholders " + OverpassReceiver.PH_BBOX + ".");
		}
		
		script = "[out:xml][timeout:" + OverpassReceiver.REQUEST_TIMEOUT + "];(" + script + ");out meta;>;out meta qt;";
		
		try
		{
			for (int i = 0; i < OverpassReceiver.ATTEMPTS; i++)
			{
				OverpassReceiver.LOGGER.debug("Request features by the following script (attempt " + (i + 1) + "): " + script);
				
				String url = this.apiUrl.toString() + "interpreter?data=" + URLEncoder.encode(script, "UTF-8");
				OverpassReceiver.LOGGER.trace("Trying to connect to: " + url);
				
				URLConnection connection = (new URL(url)).openConnection();
				connection.connect();
				
				if (connection instanceof HttpURLConnection)
				{
					HttpURLConnection httpConnection = (HttpURLConnection)connection;
					
					int statusCode = httpConnection.getResponseCode();
					
					OverpassReceiver.LOGGER.debug("Overpass API returned HTTP status code " + statusCode + ".");
					
					if (statusCode == 400)
					{
						String msg = "An error occured during the execution of the overpass query! This is what overpass API returned:\r\n";
						msg += IOUtils.toString(httpConnection.getErrorStream());
						
						OverpassReceiver.LOGGER.error(msg);
						throw new IOException(msg);
					}
					else if (statusCode == 429 || statusCode == 504)
					{
						OverpassReceiver.LOGGER.debug("Close connection and retry after " + OverpassReceiver.RETRY_DELAY + " milliseconds.");
						
						IOUtils.close(httpConnection);
						
						long stopTime = System.currentTimeMillis() + OverpassReceiver.RETRY_DELAY;
						while (System.currentTimeMillis() < stopTime)
						{
							;
						}
						
						continue;
					}
					
					InputStream is = httpConnection.getInputStream();
					SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
					final Set<Entity> res = new HashSet<>();
					
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
							
							DataHandler store = OverpassReceiver.this.getStore();
							if (store != null)
							{
								store.store(entity);
							}
							
							res.add(entity);
						}
					};
					
					OverpassReceiver.LOGGER.trace("Start XML parsing of Overpass API response from " + url);
					parser.parse(is, new OsmHandler(sink, true));
					
					OverpassReceiver.LOGGER.trace("Close conntection to " + url + ".");
					IOUtils.close(httpConnection);
					
					return res;
				}
				else
				{
					OverpassReceiver.LOGGER.trace("Close conntection to " + url + ".");
					IOUtils.close(connection);
					
					String msg = "The URL didn't point to a connection using HTTP!";
					OverpassReceiver.LOGGER.error(msg);
					throw new IOException(msg);
				}
			}
			
			String msg = "Couldn't get a valid reponse from the Overpass API after " + OverpassReceiver.ATTEMPTS + " attempts!";
			OverpassReceiver.LOGGER.error(msg);
			throw new IOException(msg);
		}
		catch (IOException e)
		{
			String msg = "An error occurred while request the Overpass API!";
			OverpassReceiver.LOGGER.error(msg, e);
			throw new IOException(msg, e);
		}
		catch (ParserConfigurationException | SAXException e)
		{
			String msg = "An error occurred on parsing the OpenStreetMap data XML document!";
			OverpassReceiver.LOGGER.error(msg, e);
			throw new IOException(msg, e);
		}
	}
	
	@Override
	public boolean isAllStored()
	{
		return false;
	}
	
	@Override
	public Node getNode(long id)
	throws IOException
	{
		OverpassReceiver.LOGGER.trace("Request node with id " + id + ".");
		
		for (Entity entity : this.requestEntities("node(" + id + ");", null))
		{
			if (entity instanceof Node && entity.getId() == id)
			{
				return (Node)entity;
			}
		}
		
		return null;
	}
	
	@Override
	public Way getWay(long id)
	throws IOException
	{
		OverpassReceiver.LOGGER.trace("Request way with id " + id + ".");
		
		for (Entity entity : this.requestEntities("way(" + id + ");", null))
		{
			if (entity instanceof Way && entity.getId() == id)
			{
				return (Way)entity;
			}
		}
		
		return null;
	}
	
	@Override
	public Relation getRel(long id)
	throws IOException
	{
		OverpassReceiver.LOGGER.trace("Request relation with id " + id + ".");
		
		for (Entity entity : this.requestEntities("relation(" + id + ");", null))
		{
			if (entity instanceof Relation && entity.getId() == id)
			{
				return (Relation)entity;
			}
		}
		
		return null;
	}
	
	@Override
	public Set<Way> getWaysOf(Node node)
	throws IOException
	{
		OverpassReceiver.LOGGER.trace("Request all ways, on which " + node + " is a part from.");
		
		long id = node.getId();
		
		Set<Way> res = new HashSet<>();
		for (Entity entity : this.requestEntities("node(" + id + ");<;", null))
		{
			if (entity instanceof Way)
			{
				Way way = (Way)entity;
				
				for (WayNode wayNode : way.getWayNodes())
				{
					if (wayNode.getNodeId() == id)
					{
						res.add(way);
						break;
					}
				}
			}
		}
		
		return res;
	}
	
	@Override
	public Set<Relation> getRelsOf(Entity entity)
	throws IOException
	{
		OverpassReceiver.LOGGER.trace("Request all relations, which have " + entity + " as a member.");
		
		long id = entity.getId();
		
		Set<Relation> res = new HashSet<>();
		for (Entity reqEntity : this.requestEntities(entity.getType().toString().toLowerCase() + "(" + id + ");<;", null))
		{
			if (reqEntity instanceof Relation)
			{
				Relation rel = (Relation)reqEntity;
				
				for (RelationMember relMember : rel.getMembers())
				{
					if (relMember.getMemberId() == id)
					{
						res.add(rel);
						break;
					}
				}
			}
		}
		
		return res;
	}
	
	/**
	 * gives the {@link DataHandler}, in which all {@link Entity}s were stored
	 * 
	 * @since 0.0.1
	 * 
	 * @return the {@link DataHandler}, which is used for saving the received
	 *         OSM features
	 */
	public DataHandler getStore()
	{
		return this.store;
	}
	
	/**
	 * sets the {@link DataHandler} to store all received {@link Entity}s in
	 * 
	 * @since 0.0.1
	 * 		
	 * @param store the {@link DataHandler} to store the OSM features in
	 */
	public void setStore(DataHandler store)
	{
		this.store = store;
	}
}