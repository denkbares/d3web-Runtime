package de.d3web.collections.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.d3web.collections.PartialHierarchyTree;
import de.d3web.collections.PartialHierarchyTree.Node;

public class PartialHierarchyTreeTest {

	@Test
	public void testHierarchyBasicInsert() {
		PartialHierarchyTree<String> tree = new PartialHierarchyTree<String>(
				new StringPrefixHierarchy());

		assertEquals(0, tree.getNodeCount());

		String a = "A";
		String b = "B";
		String ba = "BA";

		// insert A
		tree.insertNode(a);

		assertEquals(1, tree.getNodeCount());

		// insert B
		tree.insertNode(b);

		assertEquals(2, tree.getNodeCount());
		assertEquals(2, tree.getNodes().size());

		Node<String> aNode = tree.find(a);
		assertTrue(aNode != null);
		assertTrue(aNode.getData().equals(a));
		assertEquals(0, aNode.getChildren().size());

		Node<String> bNode = tree.find(b);
		assertTrue(bNode != null);
		assertTrue(bNode.getData().equals(b));
		assertEquals(0, bNode.getChildren().size());

		// insert BA
		tree.insertNode(ba);
		assertEquals(3, tree.getNodeCount());
		assertEquals(3, tree.getNodes().size());

		Node<String> baNode = tree.find(ba);
		assertTrue(baNode != null);
		assertTrue(baNode.getData().equals(ba));
		assertEquals(0, baNode.getChildren().size());
		assertTrue(baNode.getParent().getData().equals(b));

		// has should have BA as child now
		bNode = tree.find(b);
		assertTrue(bNode != null);
		assertTrue(bNode.getData().equals(b));
		List<Node<String>> bChildren = bNode.getChildren();
		assertEquals(1, bChildren.size());
		assertTrue(bChildren.contains(new Node<String>(ba)));

		// A should be still alone
		aNode = tree.find(a);
		assertTrue(aNode != null);
		assertTrue(aNode.getData().equals(a));
		assertEquals(0, aNode.getChildren().size());

	}

	@Test
	public void testHierarchyRestructuring1() {
		PartialHierarchyTree<String> tree = new PartialHierarchyTree<String>(
				new StringPrefixHierarchy());

		String b = "B";
		String ba = "BA";
		String bac = "BAC";

		tree.insertNode(ba);
		tree.insertNode(bac);
		tree.insertNode(b);

		// check correct insertion
		assertEquals(3, tree.getNodeCount());

		Node<String> baNode = tree.find(ba);
		assertTrue(baNode.getParent().equals(tree.find(b)));
		List<Node<String>> baChildren = baNode.getChildren();
		assertEquals(1, baChildren.size());
		assertTrue(baChildren.contains(tree.find(bac)));

		// remove ba
		boolean removed = tree.removeNodeFromTree(ba);
		assertTrue(removed);
		assertFalse(tree.removeNodeFromTree(ba));
		assertEquals(null, tree.find(ba));

		// then BAC should be child of B
		assertEquals(2, tree.getNodeCount());
		Node<String> bacNode = tree.find(bac);
		Node<String> bNode = tree.find(b);
		List<Node<String>> bChildren = bNode.getChildren();
		assertEquals(1, bChildren.size());
		assertTrue(bChildren.contains(bacNode));
		assertTrue(bacNode.getParent().equals(bNode));
	}

	@Test
	public void testHierarchyRestructuring2() {
		PartialHierarchyTree<String> tree = new PartialHierarchyTree<String>(
				new StringPrefixHierarchy());

		String b = "B";
		String ba = "BA";
		String bi = "BI";

		tree.insertNode(ba);
		tree.insertNode(bi);

		// check BA
		Node<String> baNode = tree.find(ba);
		assertTrue(baNode != null);
		assertEquals(0, baNode.getChildren().size());
		assertTrue(baNode.getParent() == null);

		// check BI
		Node<String> biNode = tree.find(bi);
		assertTrue(biNode != null);
		assertEquals(0, biNode.getChildren().size());
		assertTrue(biNode.getParent() == null);

		// now insert B
		tree.insertNode(b);
		assertEquals(3, tree.getNodeCount());

		Node<String> bNode = tree.find(b);
		baNode = tree.find(ba);
		biNode = tree.find(bi);

		// check B
		assertTrue(bNode != null);
		List<Node<String>> bChildren = bNode.getChildren();
		assertEquals(2, bChildren.size());
		assertTrue(bChildren.contains(biNode));
		assertTrue(bChildren.contains(baNode));

		// check BA again
		assertTrue(baNode != null);
		assertEquals(0, baNode.getChildren().size());
		assertTrue(baNode.getParent().equals(bNode));

		// check BI again
		assertTrue(biNode != null);
		assertEquals(0, biNode.getChildren().size());
		assertTrue(biNode.getParent().equals(bNode));

		// remove B
		boolean removedB = tree.removeNodeFromTree(b);
		assertTrue(removedB);
		assertFalse(tree.removeNodeFromTree(b));
		assertEquals(null, tree.find(b));

		// check BA again
		baNode = tree.find(ba);
		assertTrue(baNode != null);
		assertEquals(0, baNode.getChildren().size());
		assertTrue(baNode.getParent() == null);

		// check BI again
		biNode = tree.find(bi);
		assertTrue(biNode != null);
		assertEquals(0, biNode.getChildren().size());
		assertTrue(biNode.getParent() == null);

	}

}
