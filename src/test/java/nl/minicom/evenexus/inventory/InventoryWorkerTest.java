package nl.minicom.evenexus.inventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Provider;

import nl.minicom.evenexus.TestModule;
import nl.minicom.evenexus.persistence.versioning.RevisionExecutor;
import nl.minicom.evenexus.persistence.versioning.StructureUpgrader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class InventoryWorkerTest {
	
	private Provider<InventoryWorker> workerProvider;
	private InventoryTestCasePreparer preparer;
	
	@Before
	public void setup() {
		Injector injector = Guice.createInjector(new TestModule());
		RevisionExecutor executor = injector.getInstance(RevisionExecutor.class);
		workerProvider = injector.getProvider(InventoryWorker.class);
		preparer = injector.getInstance(InventoryTestCasePreparer.class);

		preparer.dropDatabase();
		executor.execute(new StructureUpgrader());
	}
	
	@After
	public void tearDown() {
		preparer.dropDatabase();
	}
	
	@Test
	public void testBuyTransactionsOnly() throws IOException {
		testProfitMatching("/inventory/test-case-01.json");
	}
	
	@Test
	public void testMixedTransactions() throws IOException {
		testProfitMatching("/inventory/test-case-02.json");
	}
	
	@Test
	public void testInvalidlyOrderedTransactions() throws IOException {
		testProfitMatching("/inventory/test-case-03.json");
	}
	
	private void testProfitMatching(String fileName) throws IOException {
		// Read test case from file.
		InventoryTestCase testCase = parseTestCase(fileName);
		
		// Prepare test case in database.
		preparer.prepare(testCase);
		
		// Run InventoryWorker on test case.
		InventoryWorker worker = workerProvider.get();
		worker.initialize(1L);
		worker.run();
		
		// Check results.
		preparer.checkTestCaseResults(testCase);
	}
	
	private InventoryTestCase parseTestCase(String fileName) throws IOException {
		String line = null;
		StringBuilder builder = new StringBuilder();
		InputStream stream = getClass().getResourceAsStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		
		TypeToken<InventoryTestCase> typeToken = new TypeToken<InventoryTestCase>() { };
		return new Gson().fromJson(builder.toString(), typeToken.getType());
	}
	
}
