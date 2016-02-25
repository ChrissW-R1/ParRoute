package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * defines test cases of the {@link OsmHandler06} {@link Class}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmHandler06Test
{
	/**
	 * Test method for {@link OsmHandler06#requestEntity(long, EntityType)}.
	 */
	@Test
	public void testRequestEntity()
	{
		try
		{
			Entity entity = OsmHandler06.requestEntity(269636333, EntityType.Way);
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
	 * Test method for
	 * {@link OsmHandler06#requestEntities(Collection, EntityType)}.
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
			Map<Long, ? extends Entity> entities = OsmHandler06.requestEntities(new HashSet<>(Arrays.asList(ids)), EntityType.Way);
			Set<Long> keys = entities.keySet();
			
			for (long id : ids)
			{
				Assert.assertTrue(keys.contains(id));
			}
		}
		catch (IOException e)
		{
			Assert.fail("Request of multiple ways failed: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link OsmHandler06#requestWaysOfNode(Node)}.
	 */
	@Test
	public void testRequestWaysOfNode()
	{
		long nodeId = 42907315;
		Node node = Mockito.mock(Node.class);
		Mockito.when(node.getId()).thenReturn(nodeId);
		
		try
		{
			Map<Long, ? extends Way> ways = OsmHandler06.requestWaysOfNode(node);
			Assert.assertTrue(ways.size() >= 2);
			
			Set<Long> keys = ways.keySet();
			
			Assert.assertTrue(keys.contains(269636333L));
			Assert.assertTrue(keys.contains(5881250L));
			Assert.assertTrue(keys.contains(5738250L));
			Assert.assertTrue(keys.contains(5881251L));
		}
		catch (IOException e)
		{
			Assert.fail("Request of ways from node " + nodeId + " failed: " + e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link OsmHandler06#requestRelationsOfEntity(Entity)}.
	 */
	@Test
	public void testRequestRelationsOfEntity()
	{
		long wayId = 16307424;
		Way way = Mockito.mock(Way.class);
		Mockito.when(way.getType()).thenReturn(EntityType.Way);
		Mockito.when(way.getId()).thenReturn(wayId);
		
		try
		{
			Map<Long, ? extends Relation> rels = OsmHandler06.requestRelationsOfEntity(way);
			Set<Long> keys = rels.keySet();
			
			Assert.assertTrue(keys.contains(5633210L));
			Assert.assertTrue(keys.contains(5633244L));
			Assert.assertTrue(keys.contains(5634106L));
			Assert.assertTrue(keys.contains(5634231L));
			Assert.assertTrue(keys.contains(5634288L));
			Assert.assertTrue(keys.contains(5634287L));
			Assert.assertTrue(keys.contains(5634107L));
			Assert.assertTrue(keys.contains(5634230L));
			Assert.assertTrue(keys.contains(5634955L));
			Assert.assertTrue(keys.contains(5634956L));
			Assert.assertTrue(keys.contains(5635160L));
			Assert.assertTrue(keys.contains(5635161L));
			Assert.assertTrue(keys.contains(5635481L));
			Assert.assertTrue(keys.contains(5635482L));
			Assert.assertTrue(keys.contains(5635027L));
			Assert.assertTrue(keys.contains(5635028L));
			Assert.assertTrue(keys.contains(5635546L));
			Assert.assertTrue(keys.contains(5635547L));
			Assert.assertTrue(keys.contains(5633686L));
			Assert.assertTrue(keys.contains(5639688L));
		}
		catch (IOException e)
		{
			Assert.fail("Request of ways from node " + wayId + " failed: " + e.getMessage());
		}
	}
}