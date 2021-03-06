package technion.ir.se.trec.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class TrecEvalDataFile {
	private static final String MESSAGE_FORMAT = "There are no records for query with ID: %s";
	private Map<String, QueryTrecEvalRecords> dataFile;
	
    static final Logger logger = Logger.getLogger(TrecEvalDataFile.class);

	
	public TrecEvalDataFile() {
		dataFile = new HashMap<String, QueryTrecEvalRecords>();
	}
	
	public QueryTrecEvalRecords getRecordsForQuery(String queryID) throws RecordsNotExistsException {
		QueryTrecEvalRecords queryTrecEvalRecords = dataFile.get(queryID);
		if (queryTrecEvalRecords == null) {
			throw new RecordsNotExistsException(String.format(MESSAGE_FORMAT, queryID));
		}
		return queryTrecEvalRecords;
	}
	
	public boolean doesContainRecordsForQuery(String queryID) {
		return dataFile.containsKey(queryID);
	}
	
	public void addRecordsToQuery(String queryID, QrelsRecord record) {
		if (dataFile.containsKey(queryID)) {
			dataFile.get(queryID).add(record);
		} else {
			QueryTrecEvalRecords trecEvalDataFile = new QueryTrecEvalRecords();
			trecEvalDataFile.add(record);
			dataFile.put(queryID, trecEvalDataFile);
		}
	}
	
	public int getNumberOfRelevantDocuments(String queryID) {
		try {
			return this.getRecordsForQuery(queryID).getRelevenceRecords().size();
		} catch (RecordsNotExistsException e) {
			logger.error("tried to fetch number of relevent document for queryID #" + queryID, e);
			return 0;
		}
	}
	
	public int getNumberOfDocuments(String queryID) {
		try {
			return this.getRecordsForQuery(queryID).getRecords().size();
		} catch (RecordsNotExistsException e) {
			logger.error("tried to fetch number of documents for queryID #" + queryID, e);
			return 0;
		}
	}
	
	public Set<QrelsRecord> getRelevantDocuments(String queryID) throws RecordsNotExistsException {
		return this.getRecordsForQuery(queryID).getRelevenceRecords();
	}
	
	public boolean isDocumentRelevent(QrelsRecord record) throws RecordsNotExistsException {
		String queryID = record.getQueryID();
		Set<QrelsRecord> relevantDocumentsForQuery = this.getRelevantDocuments(queryID);
		for (QrelsRecord releventDoc : relevantDocumentsForQuery) {
			if (releventDoc.getDocumentID().equals(record.getDocumentID())) {
				return true;
			}
		}
		return false;
	}
	
	public List<QrelsRecord> getRankedDocuments(String queryID, int numberOfDos) throws RecordsNotExistsException {
		TreeMap<Integer,QrelsRecord> rankedDocuments = this.getRecordsForQuery(queryID).getRankedDocuments();
		int numberOfDocsReturned = numberOfDos;
		
		if (!rankedDocuments.containsKey(numberOfDos)) {
			numberOfDocsReturned = rankedDocuments.floorKey(numberOfDos);
			String messageFor = "There aren't #%d relevent documents for query: '%s'. Instead returning #%d documents";
			logger.info(String.format(messageFor, numberOfDos, queryID, numberOfDocsReturned) );
		}
		
		NavigableMap<Integer,QrelsRecord> headMap = rankedDocuments.headMap(numberOfDocsReturned, true);
		return new ArrayList<QrelsRecord>(headMap.values());
	}

}
