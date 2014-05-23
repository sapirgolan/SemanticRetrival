package technion.ir.se.indri;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RunQueryTest {
	private RunQuery runQuery;

	@Before
	public void setUp() throws Exception {
		runQuery = new RunQuery();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunQuery() throws Exception {
		String[] rules= new String[]{ "method:linear","collectionLambda:0.2","field:title" };
		int retrivedDocumentId = runQuery.runQuery(1, rules, "cocaine police", "docno");
		assertTrue("Didn't retrive document", retrivedDocumentId == 2);
		
	}
	
	@Test
	public void testRunQuery_TFIDF() throws Exception {
		String[] rules= new String[]{ "tfidf", "k1:1.0", "b:0.3" };
		int retrivedDocumentId = runQuery.runQuery(1, rules, "Frank aircraft", "docno");
		assertTrue("Didn't retrive document", retrivedDocumentId == 2);
	}
	
	@Test
	public void testRunQuery_Okapi() throws Exception {
		String[] rules= new String[]{ "Okapi", "k1:1.2", "b:0.75", "k3:7" };
		int retrivedDocumentId = runQuery.runQuery(1, rules, "Frank aircraft", "docno");
		assertTrue("Didn't retrive document", retrivedDocumentId == 2);
	}

}