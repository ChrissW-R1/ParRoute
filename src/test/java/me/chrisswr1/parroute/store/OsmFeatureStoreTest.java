package me.chrisswr1.parroute.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * class to test {@link OsmFeatureStore}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class OsmFeatureStoreTest
{
	/**
	 * id of {@link OsmFeatureStoreTest#node}
	 * 
	 * @since 0.0.1
	 */
	private static final long	nodeId	= 123451;
	/**
	 * id of {@link OsmFeatureStoreTest#way}
	 * 
	 * @since 0.0.1
	 */
	private static final long	wayId	= OsmFeatureStoreTest.nodeId + 11;
	/**
	 * id of {@link OsmFeatureStoreTest#rel}
	 * 
	 * @since 0.0.1
	 */
	private static final long	relId	= OsmFeatureStoreTest.wayId + 11;
										
	/**
	 * test object
	 * 
	 * @since 0.0.1
	 */
	private OsmFeatureStore		store;
								
	/**
	 * mocked {@link Node}
	 * 
	 * @since 0.0.1
	 */
	private Node				node;
	/**
	 * mocked {@link Way}
	 * 
	 * @since 0.0.1
	 */
	private Way					way;
	/**
	 * mocked {@link Relation}
	 * 
	 * @since 0.0.1
	 */
	private Relation			rel;
	/**
	 * {@link Set} of all used ids
	 * 
	 * @since 0.0.1
	 */
	private Set<Long>			rndLongs;
								
	/**
	 * generates a new unique random id
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the generated id
	 */
	public long newRndId()
	{
		Random rnd = new Random();
		long res;
		
		do
		{
			res = rnd.nextLong();
		}
		while (res > 0 && ! (this.rndLongs.contains(res)));
		
		this.rndLongs.add(res);
		return res;
	}
	
	/**
	 * initialize the test object
	 * 
	 * @since 0.0.1
	 */
	@Before
	public void setUp()
	{
		this.rndLongs = new LinkedHashSet<>();
		this.rndLongs.add(OsmFeatureStoreTest.nodeId);
		this.rndLongs.add(OsmFeatureStoreTest.wayId);
		this.rndLongs.add(OsmFeatureStoreTest.relId);
		
		this.store = new OsmFeatureStore();
		
		this.node = Mockito.mock(Node.class);
		Mockito.when(this.node.getId()).thenReturn(OsmFeatureStoreTest.nodeId);
		this.store.addNode(this.node);
		
		this.way = Mockito.mock(Way.class);
		Mockito.when(this.way.getId()).thenReturn(OsmFeatureStoreTest.wayId);
		this.store.addWay(this.way);
		
		this.rel = Mockito.mock(Relation.class);
		Mockito.when(this.rel.getId()).thenReturn(OsmFeatureStoreTest.relId);
		this.store.addRel(this.rel);
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getNodes()}.
	 */
	@Test
	public void testGetNodes()
	{
		Map<Long, ? extends Node> nodes = this.store.getNodes();
		
		Assert.assertEquals(1, nodes.size());
		Assert.assertSame(this.node, nodes.get(OsmFeatureStoreTest.nodeId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getNode(long)}.
	 */
	@Test
	public void testGetNode()
	{
		Assert.assertSame(this.node, this.store.getNode(OsmFeatureStoreTest.nodeId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addNode(Node)}.
	 */
	@Test
	public void testAddNode()
	{
		Node otherNode = Mockito.mock(Node.class);
		Mockito.when(otherNode.getId()).thenReturn(this.newRndId());
		Assert.assertTrue(this.store.addNode(otherNode));
		
		Node sameNode = Mockito.mock(Node.class);
		Mockito.when(sameNode.getId()).thenReturn(OsmFeatureStoreTest.nodeId);
		Assert.assertFalse(this.store.addNode(sameNode));
		
		Assert.assertFalse(this.store.addNode(this.node));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addNodes(Collection)}.
	 */
	@Test
	public void testAddNodes()
	{
		List<Node> nodes = new ArrayList<>();
		nodes.add(this.node);
		
		int max = 10;
		for (int i = 0; i < max; i++)
		{
			Node rndNode = Mockito.mock(Node.class);
			Mockito.when(rndNode.getId()).thenReturn(this.newRndId());
			nodes.add(rndNode);
		}
		
		Node sameNode = Mockito.mock(Node.class);
		Mockito.when(sameNode.getId()).thenReturn(OsmFeatureStoreTest.nodeId);
		nodes.add(sameNode);
		
		nodes.add(this.node);
		
		Assert.assertTrue(this.store.addNodes(nodes));
		
		Assert.assertFalse(this.store.getNodes().values().containsAll(nodes));
		nodes.remove(sameNode);
		Assert.assertTrue(this.store.getNodes().values().containsAll(nodes));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeNode(long)}.
	 */
	@Test
	public void testRemoveNodeLong()
	{
		Assert.assertSame(this.node, this.store.removeNode(OsmFeatureStoreTest.nodeId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeNode(Node)}. Tested with
	 * same {@link Node} as stored.
	 */
	@Test
	public void testRemoveNodeNodeSameObject()
	{
		Assert.assertSame(this.node, this.store.removeNode(this.node));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeNode(Node)}. Tested with a
	 * different {@link Node}, but the same id.
	 */
	@Test
	public void testRemoveNodeNodeSameId()
	{
		Node otherNode = Mockito.mock(Node.class);
		Mockito.when(otherNode.getId()).thenReturn(OsmFeatureStoreTest.nodeId);
		
		Assert.assertSame(this.node, this.store.removeNode(otherNode));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeNode(Node)}. Tested with a
	 * different {@link Node} and a different id.
	 */
	@Test
	public void testRemoveNodeNodeOtherId()
	{
		Node otherNode = Mockito.mock(Node.class);
		Mockito.when(otherNode.getId()).thenReturn(this.newRndId());
		
		Assert.assertSame(null, this.store.removeNode(otherNode));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getWays()}.
	 */
	@Test
	public void testGetWays()
	{
		Map<Long, ? extends Way> ways = this.store.getWays();
		
		Assert.assertEquals(1, ways.size());
		Assert.assertSame(this.way, ways.get(OsmFeatureStoreTest.wayId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getWay(long)}.
	 */
	@Test
	public void testGetWay()
	{
		Assert.assertSame(this.way, this.store.getWay(OsmFeatureStoreTest.wayId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addWay(Way)}.
	 */
	@Test
	public void testAddWay()
	{
		Way otherWay = Mockito.mock(Way.class);
		Mockito.when(otherWay.getId()).thenReturn(this.newRndId());
		Assert.assertTrue(this.store.addWay(otherWay));
		
		Way sameWay = Mockito.mock(Way.class);
		Mockito.when(sameWay.getId()).thenReturn(OsmFeatureStoreTest.wayId);
		Assert.assertFalse(this.store.addWay(sameWay));
		
		Assert.assertFalse(this.store.addWay(this.way));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addWays(Collection)}.
	 */
	@Test
	public void testAddWays()
	{
		List<Way> ways = new ArrayList<>();
		ways.add(this.way);
		
		int max = 10;
		for (int i = 0; i < max; i++)
		{
			Way rndWay = Mockito.mock(Way.class);
			Mockito.when(rndWay.getId()).thenReturn(this.newRndId());
			ways.add(rndWay);
		}
		
		Way sameWay = Mockito.mock(Way.class);
		Mockito.when(sameWay.getId()).thenReturn(OsmFeatureStoreTest.wayId);
		ways.add(sameWay);
		
		ways.add(this.way);
		
		Assert.assertTrue(this.store.addWays(ways));
		
		Assert.assertFalse(this.store.getWays().values().containsAll(ways));
		ways.remove(sameWay);
		Assert.assertTrue(this.store.getWays().values().containsAll(ways));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeWay(long)}.
	 */
	@Test
	public void testRemoveWayLong()
	{
		Assert.assertSame(this.way, this.store.removeWay(OsmFeatureStoreTest.wayId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeWay(Way)}. Tested with same
	 * {@link Way} as stored.
	 */
	@Test
	public void testRemoveWayWaySameObject()
	{
		Assert.assertSame(this.way, this.store.removeWay(this.way));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeWay(Way)}. Tested with a
	 * different {@link Way}, but the same id.
	 */
	@Test
	public void testRemoveWayWaySameId()
	{
		Way otherWay = Mockito.mock(Way.class);
		Mockito.when(otherWay.getId()).thenReturn(OsmFeatureStoreTest.wayId);
		
		Assert.assertSame(this.way, this.store.removeWay(otherWay));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeWay(Way)}. Tested with a
	 * different {@link Way} and a different id.
	 */
	@Test
	public void testRemoveWayWayOtherId()
	{
		Way otherWay = Mockito.mock(Way.class);
		Mockito.when(otherWay.getId()).thenReturn(this.newRndId());
		
		Assert.assertSame(null, this.store.removeWay(otherWay));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getRels()}.
	 */
	@Test
	public void testGetRels()
	{
		Map<Long, ? extends Relation> rels = this.store.getRels();
		
		Assert.assertEquals(1, rels.size());
		Assert.assertSame(this.rel, rels.get(OsmFeatureStoreTest.relId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#getRel(long)}.
	 */
	@Test
	public void testGetRel()
	{
		Assert.assertSame(this.rel, this.store.getRel(OsmFeatureStoreTest.relId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addRel(Relation)}.
	 */
	@Test
	public void testAddRel()
	{
		Relation otherRel = Mockito.mock(Relation.class);
		Mockito.when(otherRel.getId()).thenReturn(this.newRndId());
		Assert.assertTrue(this.store.addRel(otherRel));
		
		Relation sameRel = Mockito.mock(Relation.class);
		Mockito.when(sameRel.getId()).thenReturn(OsmFeatureStoreTest.relId);
		Assert.assertFalse(this.store.addRel(sameRel));
		
		Assert.assertFalse(this.store.addRel(this.rel));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#addRels(Collection)}.
	 */
	@Test
	public void testAddRels()
	{
		List<Relation> rels = new ArrayList<>();
		rels.add(this.rel);
		
		int max = 10;
		for (int i = 0; i < max; i++)
		{
			Relation rndRel = Mockito.mock(Relation.class);
			Mockito.when(rndRel.getId()).thenReturn(this.newRndId());
			rels.add(rndRel);
		}
		
		Relation sameRel = Mockito.mock(Relation.class);
		Mockito.when(sameRel.getId()).thenReturn(OsmFeatureStoreTest.relId);
		rels.add(sameRel);
		
		rels.add(this.rel);
		
		Assert.assertTrue(this.store.addRels(rels));
		
		Assert.assertFalse(this.store.getRels().values().containsAll(rels));
		rels.remove(sameRel);
		Assert.assertTrue(this.store.getRels().values().containsAll(rels));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeRel(long)}.
	 */
	@Test
	public void testRemoveRelLong()
	{
		Assert.assertSame(this.rel, this.store.removeRel(OsmFeatureStoreTest.relId));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeRel(Relation)}. Tested with
	 * same {@link Relation} as stored.
	 */
	@Test
	public void testRemoveRelRelationSameObject()
	{
		Assert.assertSame(this.rel, this.store.removeRel(this.rel));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeRel(Relation)}. Tested with
	 * a different {@link Relation}, but the same id.
	 */
	@Test
	public void testRemoveRelRelationSameId()
	{
		Relation otherRel = Mockito.mock(Relation.class);
		Mockito.when(otherRel.getId()).thenReturn(OsmFeatureStoreTest.relId);
		
		Assert.assertSame(this.rel, this.store.removeRel(otherRel));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#removeRel(Relation)}. Tested with
	 * a different {@link Relation} and a different id.
	 */
	@Test
	public void testRemoveRelRelationOtherId()
	{
		Relation otherRel = Mockito.mock(Relation.class);
		Mockito.when(otherRel.getId()).thenReturn(this.newRndId());
		
		Assert.assertSame(null, this.store.removeRel(otherRel));
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#clear()}.
	 */
	@Test
	public void testClear()
	{
		Assert.assertFalse(this.store.isEmpty());
		
		this.store.clear();
		
		Assert.assertTrue(this.store.isEmpty());
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#size()}.
	 */
	@Test
	public void testSize()
	{
		Assert.assertEquals(3, this.store.size());
		
		this.store.removeNode(OsmFeatureStoreTest.nodeId);
		Assert.assertEquals(2, this.store.size());
		
		this.store.removeWay(OsmFeatureStoreTest.wayId);
		Assert.assertEquals(1, this.store.size());
		
		this.store.removeRel(OsmFeatureStoreTest.relId);
		Assert.assertEquals(0, this.store.size());
		
		this.store.addNode(this.node);
		Assert.assertEquals(1, this.store.size());
		
		this.store.addWay(this.way);
		Assert.assertEquals(2, this.store.size());
		
		this.store.addRel(this.rel);
		Assert.assertEquals(3, this.store.size());
	}
	
	/**
	 * Test method for {@link OsmFeatureStore#isEmpty()}.
	 */
	@Test
	public void testIsEmpty()
	{
		Assert.assertFalse(this.store.isEmpty());
		
		this.store.clear();
		
		Assert.assertTrue(this.store.isEmpty());
	}
}