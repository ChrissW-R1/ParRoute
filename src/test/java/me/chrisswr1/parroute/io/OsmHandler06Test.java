package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * defines test cases of the {@link OsmReceiver} {@link Class}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmHandler06Test
{
	/**
	 * the test object
	 * 
	 * @since 0.0.1
	 */
	@SuppressWarnings("deprecation")
	private OsmReceiver receiver;
	
	/**
	 * initialize the test object
	 * 
	 * @since 0.0.1
	 * 
	 * @throws MalformedURLException if the test object couldn't initialized
	 */
	@SuppressWarnings("deprecation")
	@Before
	public void setUp()
	throws MalformedURLException
	{
		this.receiver = new OsmReceiver(new URL("https://api.openstreetmap.org/api/0.6/"));
	}
	
	/**
	 * Test method for {@link OsmReceiver#getEntity(long, EntityType)}.
	 */
	@Test
	public void testRequestEntity()
	{
		try
		{
			@SuppressWarnings("deprecation")
			Entity entity = this.receiver.getEntity(269636333, EntityType.Way);
			Assert.assertEquals(269636333, entity.getId());
			Assert.assertEquals(EntityType.Way, entity.getType());
			
			boolean foundHighway = false;
			String name = "";
			Collection<Tag> tags = entity.getTags();
			for (Tag tag : tags)
			{
				if (tag.getKey().equals("highway"))
				{
					foundHighway = true;
				}
				
				if (tag.getKey().equals("name"))
				{
					name = tag.getValue();
				}
			}
			
			Assert.assertTrue(foundHighway);
			Assert.assertEquals("\u00DCberwasserstra\u00DFe", name);
		}
		catch (IOException e)
		{
			Assert.fail("Request of way 269636333 failed: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link OsmReceiver#getEntities(Collection, EntityType)} .
	 */
	@Test
	public void testRequestEntities()
	{
		Long[] ids =
		{
				269636333L,
				5881250L,
				5738250L,
				5881251L
		};
		
		try
		{
			@SuppressWarnings("deprecation")
			Set<Entity> entities = this.receiver.getEntities(new HashSet<>(Arrays.asList(ids)), EntityType.Way);
			
			Set<Long> idSet = new HashSet<>();
			for (Entity entity : entities)
			{
				idSet.add(entity.getId());
			}
			
			Assert.assertTrue(idSet.containsAll(Arrays.asList(ids)));
		}
		catch (IOException e)
		{
			Assert.fail("Request of multiple ways failed: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link OsmReceiver#getWaysOf(Node)}.
	 */
	@Test
	public void testRequestWaysOf()
	{
		long nodeId = 42907315;
		Node node = Mockito.mock(Node.class);
		Mockito.when(node.getId()).thenReturn(nodeId);
		
		Long[] wayIds =
		{
				269636333L,
				5881250L,
				5738250L,
				5881251L
		};
		
		try
		{
			@SuppressWarnings("deprecation")
			Set<Way> ways = this.receiver.getWaysOf(node);
			Assert.assertTrue(ways.size() >= 2);
			
			Set<Long> idSet = new HashSet<>();
			for (Way way : ways)
			{
				idSet.add(way.getId());
			}
			
			Assert.assertTrue(idSet.containsAll(Arrays.asList(wayIds)));
		}
		catch (IOException e)
		{
			Assert.fail("Request of ways from node " + nodeId + " failed: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link OsmReceiver#getRelsOf(Entity)}.
	 */
	@Test
	public void testRequestRelationsOf()
	{
		long wayId = 16307424;
		Way way = Mockito.mock(Way.class);
		Mockito.when(way.getType()).thenReturn(EntityType.Way);
		Mockito.when(way.getId()).thenReturn(wayId);
		
		Long[] relIds =
		{
				5633210L,
				5633244L,
				5634106L,
				5634231L,
				5634288L,
				5634287L,
				5634107L,
				5634230L,
				5634955L,
				5634956L,
				5635160L,
				5635161L,
				5635481L,
				5635482L,
				5635027L,
				5635028L,
				5635546L,
				5635547L,
				5633686L,
				5639688L
		};
		
		try
		{
			@SuppressWarnings("deprecation")
			Set<Relation> rels = this.receiver.getRelsOf(way);
			
			Set<Long> idSet = new HashSet<>();
			for (Relation rel : rels)
			{
				idSet.add(rel.getId());
			}
			
			Assert.assertTrue(idSet.containsAll(Arrays.asList(relIds)));
		}
		catch (IOException e)
		{
			Assert.fail("Request of ways from node " + wayId + " failed: " + e.getMessage());
		}
	}
}