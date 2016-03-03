package me.chrisswr1.parroute.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import crosby.binary.osmosis.OsmosisReader;
import me.chrisswr1.parroute.DataHandler;

/**
 * defines a {@link DataReceiver}, which reads an OSM {@link File}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmFileReceiver
implements DataReceiver
{
	/**
	 * the {@link File} to read the {@link Entity}s from
	 * 
	 * @since 0.0.1
	 */
	private final File				file;
	/**
	 * the {@link OsmFileFormat} of {@link OsmFileReceiver#file}
	 * 
	 * @since 0.0.1
	 */
	private final OsmFileFormat		format;
	/**
	 * the {@link CompressionMethod} of {@link OsmFileReceiver#file}
	 * 
	 * @since 0.0.1
	 */
	private final CompressionMethod	compression;
	/**
	 * the {@link DataHandler}, in which the {@link Entity}s should be stored
	 * 
	 * @since 0.0.1
	 */
	private DataHandler				store		= null;
	/**
	 * were all {@link Entity}s already stored in the
	 * {@link OsmFileReceiver#store}?
	 * 
	 * @since 0.0.1
	 */
	private boolean					allStored	= false;
												
	/**
	 * constructor, with given {@link File}
	 * 
	 * @since 0.0.1
	 * 
	 * @param file the {@link File} to read the {@link Entity}s from
	 * @param format the {@link OsmFileFormat} of {@code file}
	 * @param compression the {@link CompressionMethod} of {@code file}
	 */
	public OsmFileReceiver(File file, OsmFileFormat format, CompressionMethod compression)
	{
		this.file = file;
		this.format = format;
		this.compression = compression;
	}
	
	/**
	 * reads the {@link Entity}s from the {@link File}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the read {@link Entity}s
	 * @throws FileNotFoundException if the {@link File} doesn't exist
	 */
	private Set<Entity> readEntities()
	throws FileNotFoundException
	{
		File file = this.getFile();
		if (file == null || ( ! (file.exists())))
		{
			throw new FileNotFoundException(file + " doesn't exist!");
		}
		
		final Set<Entity> res = new HashSet<>();
		
		RunnableSource reader;
		if (this.getFormat() == OsmFileFormat.PBF)
		{
			reader = new OsmosisReader(new FileInputStream(file));
		}
		else
		{
			reader = new XmlReader(file, true, this.getCompression());
		}
		
		final DataHandler store = this.getStore();
		final boolean storeExist = store != null;
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
				
				res.add(entity);
				
				if (storeExist)
				{
					store.store(entity);
				}
			}
		};
		
		reader.setSink(sink);
		
		Thread readerThread = new Thread(reader);
		readerThread.start();
		
		while (readerThread.isAlive())
		{
			try
			{
				readerThread.join();
			}
			catch (InterruptedException e)
			{
			}
		}
		
		if (storeExist)
		{
			this.allStored = true;
		}
		
		return res;
	}
	
	@Override
	public boolean isAllStored()
	{
		return this.allStored;
	}
	
	@Override
	public Node getNode(long id)
	throws IOException
	{
		for (Entity entity : this.readEntities())
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
		for (Entity entity : this.readEntities())
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
		for (Entity entity : this.readEntities())
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
		Set<Way> res = new HashSet<>();
		
		long nodeId = node.getId();
		for (Entity entity : this.readEntities())
		{
			if (entity instanceof Way)
			{
				Way way = (Way)entity;
				
				for (WayNode wayNode : way.getWayNodes())
				{
					if (wayNode.getNodeId() == nodeId)
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
		Set<Relation> res = new HashSet<>();
		
		long entityId = entity.getId();
		for (Entity listEntity : this.readEntities())
		{
			if (listEntity instanceof Relation)
			{
				Relation rel = (Relation)listEntity;
				
				for (RelationMember member : rel.getMembers())
				{
					if (member.getMemberId() == entityId)
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
	 * gives the {@link File} to read the {@link Entity}s from
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link File}
	 */
	public File getFile()
	{
		return this.file;
	}
	
	/**
	 * gives the {@link CompressionMethod} of the {@link File}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link CompressionMethod}
	 */
	public CompressionMethod getCompression()
	{
		return this.compression;
	}
	
	/**
	 * gives the {@link OsmFileFormat} of the {@link File}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link OsmFileFormat}
	 */
	public OsmFileFormat getFormat()
	{
		return this.format;
	}
	
	/**
	 * gives the {@link DataHandler}, in which the read {@link Entity}s should
	 * be stored
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the {@link Entity} store
	 */
	public DataHandler getStore()
	{
		return this.store;
	}
	
	/**
	 * sets the {@link DataHandler}, in which the read {@link Entity}s should be
	 * stored
	 * 
	 * @since 0.0.1
	 * 		
	 * @param store the {@link DataHandler} to store the features in
	 */
	public void setStore(DataHandler store)
	{
	}
}