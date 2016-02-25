package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.v0_6.impl.OsmHandler;
import org.xml.sax.SAXException;

/**
 * defines a collection of methods to request data from the OpenStreetMap API in
 * version 0.6
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 * 		
 * @see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6">API v0.6 –
 *      OpenStreetMap Wiki</a>
 */
public class OsmHandler06
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger	LOGGER	= LogManager.getLogger(OsmHandler06.class);
	/**
	 * the {@link URL} of the OpenStreetMap API
	 * 
	 * @since 0.0.1
	 */
	public static String		API_URL	= "https://api.openstreetmap.org/api/0.6/";
										
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
	 * parse an {@link InputStream} with OpenStreetMap data XML document
	 * 
	 * @since 0.0.1
	 * 
	 * @param is the {@link InputStream} to parse
	 * @param types list of all {@link EntityType}s, which should be parsed (use
	 *            <code>null</code> to parse all {@link EntityType}s)
	 * @param ids list of all ids, which should be parsed (use <code>null</code>
	 *            to parse all ids)
	 * @return a {@link Map}, which contains all parsed {@link Entity}s
	 * @throws IOException if an error occurred while parsing the document
	 * @see OsmHandler06#parseStream(InputStream, EntityType, Collection)
	 */
	private static Map<Long, ? extends Entity> parseStream(InputStream is, final Collection<? extends EntityType> types, final Collection<? extends Long> ids)
	throws IOException
	{
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			final Map<Long, Entity> res = new HashMap<>();
			
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
					
					if (ids != null && ! (ids.contains(id)))
					{
						return;
					}
					if (types != null && ! (types.contains(entity.getType())))
					{
						return;
					}
					
					res.put(id, entity);
				}
			};
			
			parser.parse(is, new OsmHandler(sink, true));
			
			return res;
		}
		catch (ParserConfigurationException | SAXException e)
		{
			String msg = "An error occurred on parsing the OpenStreetMap data XML document!";
			OsmHandler06.LOGGER.error(msg, e);
			throw new IOException(msg, e);
		}
	}
	
	/**
	 * parse an {@link InputStream} and filters only one {@link EntityType}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param is the {@link InputStream} to parse
	 * @param type the {@link EntityType} to filter
	 * @param ids the ids to filter
	 * @return a {@link Map} of all parsed {@link Entity}s, filtered by
	 *         {@code type} and {@code ids}
	 * @throws IOException if the parsing failed
	 * @see OsmHandler06#parseStream(InputStream, Collection, Collection)
	 */
	private static Map<Long, ? extends Entity> parseStream(InputStream is, EntityType type, final Collection<? extends Long> ids)
	throws IOException
	{
		Set<EntityType> types = new HashSet<>();
		types.add(type);
		
		return OsmHandler06.parseStream(is, types, ids);
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
	 * @see <a href=
	 *      "http://wiki.openstreetmap.org/wiki/API_v0.6#Read:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">
	 *      API v0.6 – OpenStreetMap Wiki</a>
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
		
		Set<Long> ids = new HashSet<>();
		ids.add(id);
		
		Map<Long, ? extends Entity> entities = OsmHandler06.parseStream(connection.getInputStream(), type, ids);
		IOUtils.close(connection);
		
		if (entities.size() < 1)
		{
			String msg = "Couldn't received the requested " + typeString + " with id " + id + "!";
			OsmHandler06.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		return entities.values().iterator().next();
	}
	
	/**
	 * requests a list of {@link Entity}s
	 * 
	 * @since 0.0.1
	 * 
	 * @param ids the ids of the requested {@link Entity}s
	 * @param type the {@link EntityType} of the requested {@link Entity}s
	 * @return a {@link Map} of all received {@link Entity}s
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public static Map<Long, ? extends Entity> requestEntities(Collection<Long> ids, EntityType type)
	throws IOException
	{
		String typeString = type.toString().toLowerCase();
		
		String delimiter = ",";
		String idString = "";
		for (long id : ids)
		{
			idString += id + delimiter;
		}
		while (idString.endsWith(delimiter))
		{
			idString = idString.substring(0, idString.length() - delimiter.length());
		}
		
		HttpURLConnection connection = OsmHandler06.getConnection(typeString + "s?" + typeString + "s=" + idString);
		int statusCode = connection.getResponseCode();
		
		OsmHandler06.LOGGER.debug("OpenStreetMap API returned HTTP status code " + statusCode + ".");
		
		if (statusCode >= 400)
		{
			String msg;
			if (statusCode == 404)
			{
				msg = "No " + typeString + " with any of the ids " + idString + " couldn't found!";
			}
			else if (statusCode == 410)
			{
				msg = "All " + typeString + " with the ids " + idString + " were deleted!";
			}
			else
			{
				msg = "An unkown error occurred on requesting " + typeString + "s with ids " + idString + "!";
			}
			
			msg += "\r\n\tThe API reported:\r\n\t" + IOUtils.toString(connection.getErrorStream());
			
			OsmHandler06.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		Map<Long, ? extends Entity> res = OsmHandler06.parseStream(connection.getInputStream(), type, ids);
		IOUtils.close(connection);
		
		return res;
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
	 * @see <a href=
	 *      "http://wiki.openstreetmap.org/wiki/API_v0.6#Ways_for_node:_GET_.2Fapi.2F0.6.2Fnode.2F.23id.2Fways">
	 *      API v0.6 – OpenStreetMap Wiki</a>
	 */
	public static Map<Long, ? extends Way> requestWaysOfNode(Node node)
	throws IOException
	{
		HttpURLConnection connection = OsmHandler06.getConnection("node/" + node.getId() + "/ways");
		
		Map<Long, ? extends Entity> entities = OsmHandler06.parseStream(connection.getInputStream(), EntityType.Way, null);
		IOUtils.close(connection);
		
		Map<Long, Way> res = new HashMap<>();
		for (long id : entities.keySet())
		{
			res.put(id, (Way)entities.get(id));
		}
		
		return res;
	}
	
	/**
	 * requests all {@link Relation}, which {@code entity} is a member from
	 * 
	 * @since 0.0.1
	 * 		
	 * @param entity the {@link Entity} to get the {@link Relation}s from
	 * @return a {@link Map} of all {@link Relation}s, which have {@code entity}
	 *         as member
	 * @throws IOException if the connection couldn't established or the parsing
	 *             failed
	 */
	public static Map<Long, ? extends Relation> requestRelationsOfEntity(Entity entity)
	throws IOException
	{
		HttpURLConnection connection = OsmHandler06.getConnection(entity.getType().toString().toLowerCase() + "/" + entity.getId() + "/relations");
		
		Map<Long, ? extends Entity> entities = OsmHandler06.parseStream(connection.getInputStream(), EntityType.Relation, null);
		IOUtils.close(connection);
		
		Map<Long, Relation> res = new HashMap<>();
		for (long id : entities.keySet())
		{
			res.put(id, (Relation)entities.get(id));
		}
		
		return res;
	}
}