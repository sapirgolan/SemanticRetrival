package technion.ir.se.windows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import technion.ir.se.dao.Document;
import technion.ir.se.dao.Feedback;
import technion.ir.se.dao.Query;
import technion.ir.se.dao.TextWindow;
import technion.ir.se.exception.LocationNotFoundException;

public class BetweenQueryTermsStrategyTest {

	private AbstractStrategy classUnderTest;
	private Feedback feedback;
	private Query query;
	private static final String STORY = "my name is alon i come for moldova which was once part of russia in russia i was called alexy" +
			" but now i'm called alon and i love it";
	
	private static final String SENTANCE = "some window without query terms adir";
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new BetweenQueryTermsStrategy();
		feedback = PowerMockito.mock(Feedback.class);
		query = PowerMockito.mock(Query.class);
		//
		Document mockedDocument = PowerMockito.mock(Document.class);
		PowerMockito.when(feedback.getDocumentOfIndex(Mockito.anyInt())).thenReturn(mockedDocument);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test (expected = IllegalAccessError.class)
	public void testSetWindowSize() {
		classUnderTest.setWindowSize(7);
	}
	
	@Test
	public void testGetWindows_hasSixWindows() {
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		PowerMockito.when(feedback.getNumberOfTerms()).thenReturn(story.size());
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("there should be 6 windows", 6l, windows.size());
	}
	
	@Test
	public void testGetWindows_checkWindowsSizes() {
		List<String> terms = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(terms);
		PowerMockito.when(feedback.getNumberOfTerms()).thenReturn(terms.size());
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("there should be 4 windows", 4l, windows.get(0).getWindowSize());
		Assert.assertEquals("there should be 4 windows", 4l, windows.get(1).getWindowSize());
		Assert.assertEquals("there should be 6 windows", 6l, windows.get(2).getWindowSize());
		Assert.assertEquals("there should be 2 windows", 2l, windows.get(3).getWindowSize());
		Assert.assertEquals("there should be 9 windows", 9l, windows.get(4).getWindowSize());
		Assert.assertEquals("there should be 4 windows", 4l, windows.get(5).getWindowSize());
	}
	
	@Test
	public void testGetWindows_checkWindowsEndPositions() throws LocationNotFoundException {
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		PowerMockito.when(feedback.getNumberOfTerms()).thenReturn(story.size());
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(0).getWindowEnd()));
		Assert.assertEquals("last word in window should be moldova", "moldova", story.get(windows.get(1).getWindowEnd()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(2).getWindowEnd()));
		Assert.assertEquals("last word in window should be russia", "russia", story.get(windows.get(3).getWindowEnd()));
		Assert.assertEquals("last word in window should be alon", "alon", story.get(windows.get(4).getWindowEnd()));
		Assert.assertEquals("last word in window should be it", "it", story.get(windows.get(5).getWindowEnd()));
	}
	
	@Test
	public void testGetWindows_checkWindowsStartPositions() {
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		PowerMockito.when(feedback.getNumberOfTerms()).thenReturn(story.size());
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("russia", "Moldova","alon"));
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		Assert.assertEquals("last word in window should be my", "my", story.get(windows.get(0).getWindowStart()));
		Assert.assertEquals("last word in window should be i", "i", story.get(windows.get(1).getWindowStart()));
		Assert.assertEquals("last word in window should be which", "which", story.get(windows.get(2).getWindowStart()));
		Assert.assertEquals("last word in window should be in", "in", story.get(windows.get(3).getWindowStart()));
		Assert.assertEquals("last word in window should be i", "i", story.get(windows.get(4).getWindowStart()));
		Assert.assertEquals("last word in window should be and", "and", story.get(windows.get(5).getWindowStart()));
	}
	
	@Test
	public void testCalcWindowEnd() throws Exception {
		HashSet<String> queryTerms = new HashSet<String>();
		List<String> story = Arrays.asList(STORY.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(story);
		queryTerms.addAll(Arrays.asList("russia", "Moldova","alon"));
		
		Integer windowEnd = Whitebox.<Integer>invokeMethod(classUnderTest, "calcWindowEnd", queryTerms, feedback, 0);
		Assert.assertEquals("didn't find window end", 3l, windowEnd.longValue());
	}
	
	@Test
	public void testGetWindows_queryTermIsFirstInFeefback_testFirstWindow() {
		List<String> sentaceTokens = Arrays.asList(SENTANCE.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(sentaceTokens);
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("some", "adir"));
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		
		TextWindow firstWindow = windows.get(0);
		Assert.assertEquals("there should be 1 windows", 1l, firstWindow.getWindowSize());
		Assert.assertEquals("First window should contain a single world", firstWindow.getWindowStart(), firstWindow.getWindowEnd());
		Assert.assertEquals("'some' should be only word in first window", "some", sentaceTokens.get(firstWindow.getWindowEnd()));
		Assert.assertEquals("'some' should be only word in first window", "some", sentaceTokens.get(firstWindow.getWindowStart()));
	}
	
	@Test
	public void testGetWindows_queryTermIsFirstInFeefback_testSecondWindow() {
		List<String> sentaceTokens = Arrays.asList(SENTANCE.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(sentaceTokens);
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("some", "adir"));
		List<TextWindow> windows = classUnderTest.getWindows(feedback, query);
		
		TextWindow secondWindows = windows.get(1);
		Assert.assertEquals("there should be 5 windows", 5l, secondWindows.getWindowSize());
		Assert.assertEquals("second window didn't start correctly", 1l, secondWindows.getWindowStart());
		Assert.assertEquals("second window didn't end correctly", 5l, secondWindows.getWindowEnd());
		Assert.assertEquals("'window' should be the starting word in second window", "window", sentaceTokens.get(secondWindows.getWindowStart()) );
		Assert.assertEquals("'adir' should be the ending word in second window", "adir", sentaceTokens.get(secondWindows.getWindowEnd()) );
	}
	
	@Test
	public void testFindNextStartingIndex_windowBeginStartOfSentance() throws Exception {
		List<String> sentaceTokens = Arrays.asList(SENTANCE.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(sentaceTokens);
		
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("some", "without", "adir"));
		
		Integer queryTermIndex = Whitebox.<Integer>invokeMethod(classUnderTest, "findNextStartingIndex", feedback, query, 1);
		Assert.assertEquals("Didn't find the right position of 'without'", 2, queryTermIndex.longValue());
	}
	
	@Test
	public void testFindNextStartingIndex_windowBeginAtMiddleOfSentance() throws Exception {
		List<String> sentaceTokens = Arrays.asList(SENTANCE.split(" "));
		PowerMockito.when(feedback.getTerms()).thenReturn(sentaceTokens);
		
		PowerMockito.when(query.getQueryTerms()).thenReturn(Arrays.asList("some", "without", "adir"));
		
		Integer queryTermIndex = Whitebox.<Integer>invokeMethod(classUnderTest, "findNextStartingIndex", feedback, query, 3);
		Assert.assertEquals("Didn't find the righr position of 'adir'", 5, queryTermIndex.longValue());
	}


}
