package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
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
 * defines a {@link DataReceiver} to request data directly from the
 * OpenStreetMap API (version 0.6)
 * 
 * @deprecated Use {@link OverpassReceiver} instead
 * 			
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 * 
 * @see <a href="http://wiki.openstreetmap.org/wiki/API_v0.6">API v0.6 –
 *      OpenStreetMap Wiki</a>
 */
@Deprecated
public class OsmReceiver
implements DataReceiver
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger	LOGGER	= LogManager.getLogger(OsmReceiver.class);
										
	/**
	 * the {@link URL} of the OpenStreetMap API
	 * 
	 * @since 0.0.1
	 * 		
	 * @see <a href="https://api.openstreetmap.org/api/0.6/">Official API
	 *      URL</a>
	 */
	private final URL			apiUrl;
								
	/**
	 * constructor, with given API {@link URL}
	 * 
	 * @since 0.0.1
	 * 
	 * @param apiUrl the {@link URL} of the OpenStreetMap API (<b>Attention</b>:
	 *            It is not recommend to use the official OSMF server, this API
	 *            is primarily provided for editing the data and &quot;heavy
	 *            usage&quot; is not allowed by the
	 *            <a href="http://wiki.openstreetmap.org/wiki/API_usage_policy">
	 *            API usage policy</a>!)
	 */
	public OsmReceiver(URL apiUrl)
	{
		this.apiUrl = apiUrl;
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
	private HttpURLConnection getConnection(String request)
	throws IOException
	{
		String url = this.apiUrl.toString() + request;
		
		OsmReceiver.LOGGER.debug("Trying to connect to url: " + url);
		
		URLConnection connection = (new URL(url)).openConnection();
		connection.connect();
		
		if (connection instanceof HttpURLConnection)
		{
			OsmReceiver.LOGGER.debug("Connection successfully established.");
			
			return (HttpURLConnection)connection;
		}
		else
		{
			IOUtils.close(connection);
			
			String msg = "The URL didn't point to a connection using HTTP!";
			OsmReceiver.LOGGER.error(msg);
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
	 * 			
	 * @see OsmReceiver#parseStream(InputStream, EntityType, Collection)
	 */
	private static Set<Entity> parseStream(InputStream is, final Collection<? extends EntityType> types, final Collection<? extends Long> ids)
	throws IOException
	{
		try
		{
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
					long id = entity.getId();
					
					if (ids != null && ! (ids.contains(id)))
					{
						return;
					}
					if (types != null && ! (types.contains(entity.getType())))
					{
						return;
					}
					
					res.add(entity);
				}
			};
			
			parser.parse(is, new OsmHandler(sink, true));
			
			return res;
		}
		catch (ParserConfigurationException | SAXException e)
		{
			String msg = "An error occurred on parsing the OpenStreetMap data XML document!";
			OsmReceiver.LOGGER.error(msg, e);
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
	 * 			
	 * @see OsmReceiver#parseStream(InputStream, Collection, Collection)
	 */
	private static Set<Entity> parseStream(InputStream is, EntityType type, final Collection<? extends Long> ids)
	throws IOException
	{
		Set<EntityType> types = new HashSet<>();
		types.add(type);
		
		return OsmReceiver.parseStream(is, types, ids);
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
		return (Node)this.getEntity(id, EntityType.Node);
	}
	
	@Override
	public Way getWay(long id)
	throws IOException
	{
		return (Way)this.getEntity(id, EntityType.Way);
	}
	
	@Override
	public Relation getRel(long id)
	throws IOException
	{
		return (Relation)this.getEntity(id, EntityType.Relation);
	}
	
	/**
	 * @see <a href=
	 *      "http://wiki.openstreetmap.org/wiki/API_v0.6#Ways_for_node:_GET_.2Fapi.2F0.6.2Fnode.2F.23id.2Fways">
	 *      API v0.6 – OpenStreetMap Wiki</a>
	 */
	@Override
	public Set<Way> getWaysOf(Node node)
	throws IOException
	{
		HttpURLConnection connection = this.getConnection("node/" + node.getId() + "/ways");
		
		Set<Entity> entities = OsmReceiver.parseStream(connection.getInputStream(), EntityType.Way, null);
		IOUtils.close(connection);
		
		Set<Way> res = new HashSet<>();
		for (Entity entity : entities)
		{
			res.add((Way)entity);
		}
		
		return res;
	}
	
	@Override
	public Set<Relation> getRelsOf(Entity entity)
	throws IOException
	{
		HttpURLConnection connection = this.getConnection(entity.getType().toString().toLowerCase() + "/" + entity.getId() + "/relations");
		
		Set<Entity> entities = OsmReceiver.parseStream(connection.getInputStream(), EntityType.Relation, null);
		IOUtils.close(connection);
		
		Set<Relation> res = new HashSet<>();
		for (Entity rel : entities)
		{
			res.add((Relation)rel);
		}
		
		return res;
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
	 * 			
	 * @see <a href=
	 *      "http://wiki.openstreetmap.org/wiki/API_v0.6#Read:_GET_.2Fapi.2F0.6.2F.5Bnode.7Cway.7Crelation.5D.2F.23id">
	 *      API v0.6 – OpenStreetMap Wiki</a>
	 */
	public Entity getEntity(final long id, final EntityType type)
	throws IOException
	{
		String typeString = type.toString().toLowerCase();
		HttpURLConnection connection = this.getConnection(typeString + "/" + id);
		int statusCode = connection.getResponseCode();
		
		OsmReceiver.LOGGER.debug("OpenStreetMap API returned HTTP status code " + statusCode + ".");
		
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
			
			OsmReceiver.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		Set<Long> ids = new HashSet<>();
		ids.add(id);
		
		Set<Entity> entities = OsmReceiver.parseStream(connection.getInputStream(), type, ids);
		IOUtils.close(connection);
		
		if (entities.size() < 1)
		{
			String msg = "Couldn't received the requested " + typeString + " with id " + id + "!";
			OsmReceiver.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		return entities.iterator().next();
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
	public Set<Entity> getEntities(Collection<? extends Long> ids, EntityType type)
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
		
		HttpURLConnection connection = this.getConnection(typeString + "s?" + typeString + "s=" + idString);
		int statusCode = connection.getResponseCode();
		
		OsmReceiver.LOGGER.debug("OpenStreetMap API returned HTTP status code " + statusCode + ".");
		
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
			
			OsmReceiver.LOGGER.error(msg);
			throw new IOException(msg);
		}
		
		Set<Entity> res = OsmReceiver.parseStream(connection.getInputStream(), type, ids);
		IOUtils.close(connection);
		
		return res;
	}
}