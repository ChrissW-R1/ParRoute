package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
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
	 * Test method for
	 * {@link me.chrisswr1.parroute.io.OsmHandler06#requestEntity(long, org.openstreetmap.osmosis.core.domain.v0_6.EntityType)}
	 * .
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
	 * {@link me.chrisswr1.parroute.io.OsmHandler06#getWaysOfNode(org.openstreetmap.osmosis.core.domain.v0_6.Node)}
	 * .
	 */
	@Test
	public void testGetWaysOfNode()
	{
		try
		{
			Entity entity = OsmHandler06.requestEntity(42907315, EntityType.Node);
			
			Node node = null;
			if (entity instanceof Node)
			{
				node = (Node)entity;
			}
			
			Map<Long, Way> ways = OsmHandler06.requestWaysOfNode(node);
			Assert.assertTrue(ways.size() >= 2);
			
			boolean id269636333 = false;
			boolean id5881250 = false;
			boolean id5738250 = false;
			boolean id5881251 = false;
			
			for (long id : ways.keySet())
			{
				if (id == 269636333)
				{
					id269636333 = true;
				}
				if (id == 5881250)
				{
					id5881250 = true;
				}
				if (id == 5738250)
				{
					id5738250 = true;
				}
				if (id == 5881251)
				{
					id5881251 = true;
				}
			}
			
			Assert.assertTrue(id269636333);
			Assert.assertTrue(id5881250);
			Assert.assertTrue(id5738250);
			Assert.assertTrue(id5881251);
		}
		catch (IOException e)
		{
			Assert.fail("Request of ways from node 42907315 failed: " + e.getMessage());
		}
	}
}