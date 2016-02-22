package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.v0_6.impl.OsmHandler;
import org.xml.sax.SAXException;

/**
 * defines a collection of methods to request data from the OpenStreetMap API
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmHandler06
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger	LOGGER					= LogManager.getLogger(OsmHandler06.class);
	/**
	 * the {@link URL} of the OpenStreetMap API
	 * 
	 * @since 0.0.1
	 */
	public static String		API_URL					= "https://api.openstreetmap.org/api/0.6/";
	/**
	 * the default error message of a parsing error on the API response
	 * 
	 * @since 0.0.1
	 */
	public static String		RESPONSE_PARSING_ERROR	= "An error occurred on parsing the API response!";
														
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private OsmHandler06()
	{
	}
	
	/**
	 * creates a {@link HttpURLConnection} of the given API request
	 * 
	 * @since 0.0.1
	 * 		
	 * @param request the request for the API
	 * @return the {@link HttpURLConnection}
	 * @throws IOException if the connection couldn't established
	 */
	private static HttpURLConnection getConnection(String request)
	throws IOException
	{
		String url = OsmHandler06.API_URL + request;
		
		OsmHandler06.LOGGER.debug("Trying to connect to url: " + url);
		
		URLConnection connection = (new URL(url)).openConnection();
		connection.connect();
		
		if (connection instanceof HttpURLConnection)
		{
			OsmHandler06.LOGGER.debug("Connection successfully established.");
			
			return (HttpURLConnection)connection;
		}
		else
		{
			IOUtils.close(connection);
			
			String msg = "The URL didn't point to a connection using HTTP!";
			OsmHandler06.LOGGER.error(msg);
			throw new IOException(msg);
		}
	}
	
	/**
	 * requests a OpenStreetMap feature by its id
	 * 
	 * @since 0.0.1
	 * 
	 * @param id the id of the requested {@link Entity}
	 * @param type the {@link EntityType} of the requested feature
	 * @return the requested feature
	 * @throws IOException if the requested {@link Entity} doesn't exist or if a
	 *             connection to the API couldn't established
	 */
	public static Entity requestEntity(final long id, final EntityType type)
	throws IOException
	{
		String typeString = type.toString().toLowerCase();
		HttpURLConnection connection = OsmHandler06.getConnection(typeString + "/" + id);
		int statusCode = connection.getResponseCode();
		
		OsmHandler06.LOGGER.debug("OpenStreetMap API returned HTTP status code " + statusCode + ".");
		
		if (statusCode >= 400)
		{
			String msg;
			if (statusCode == 404)
			{
				msg = "A " + typeString + " with id " + id + " couldn't found!";
			}
			else if (statusCode == 410)
			{
				msg = "The " + typeString + " with id " + id + " was deleted!";
			}
			else
			{
				msg = "An unkown error occurred on requesting a " + typeString + " with id " + id + "!";
			}
			
			msg += "\r\n\tThe API reported:\r\n\t" + IOUtils.toString(connection.getErrorStream());
			
			OsmHandler06.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		try
		{
			InputStream is = connection.getInputStream();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			final Map<Long, Entity> entityMap = new HashMap<>();
			
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
					long entityId = entity.getId();
					
					if (entityId == id && entity.getType() == type)
					{
						entityMap.put(entityId, entity);
					}
				}
			};
			
			parser.parse(is, new OsmHandler(sink, true));
			
			if (entityMap.size() < 1)
			{
				String msg = "Couldn't received the requested " + typeString + " with id " + id + "!";
				OsmHandler06.LOGGER.error(msg);
				throw new IOException(msg);
			}
			
			Entity res = entityMap.values().iterator().next();
			
			IOUtils.close(connection);
			
			return res;
		}
		catch (ParserConfigurationException | SAXException e)
		{
			OsmHandler06.LOGGER.error(OsmHandler06.RESPONSE_PARSING_ERROR, e);
			throw new IOException(OsmHandler06.RESPONSE_PARSING_ERROR, e);
		}
	}
	
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
	public static Map<Long, Way> requestWaysOfNode(Node node)
	throws IOException
	{
		HttpURLConnection connection = OsmHandler06.getConnection("node/" + node.getId() + "/ways");
		
		try
		{
			InputStream is = connection.getInputStream();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			final Map<Long, Way> res = new HashMap<>();
			
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
					
					if (entity instanceof Way)
					{
						Way way = (Way)entity;
						
						res.put(entity.getId(), way);
					}
				}
			};
			
			parser.parse(is, new OsmHandler(sink, true));
			
			IOUtils.close(connection);
			
			return res;
		}
		catch (ParserConfigurationException | SAXException e)
		{
			OsmHandler06.LOGGER.error(OsmHandler06.RESPONSE_PARSING_ERROR, e);
			throw new IOException(OsmHandler06.RESPONSE_PARSING_ERROR, e);
		}
	}
}