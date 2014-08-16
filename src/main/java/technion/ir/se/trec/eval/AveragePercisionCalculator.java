package technion.ir.se.trec.eval;

import java.util.List;
import java.util.Set;

import technion.ir.se.dao.QrelsRecord;
import technion.ir.se.exception.RecordsNotExistsException;

public class AveragePercisionCalculator {
	private TrecEvalDataFile qerls;
	private TrecEvalDataFile goldResults;
	
	public AveragePercisionCalculator(TrecEvalDataFile qerls, TrecEvalDataFile goldResults) {
		this.qerls = qerls;
		this.goldResults = goldResults;
	}
	
	public double calcAveragePercisionSocre(String queryID) throws RecordsNotExistsException {
		List<QrelsRecord> qrelRankedDocuments = qerls.getRankedDocuments(queryID);
		Set<QrelsRecord> relevantDocuments = goldResults.getRelevantDocuments(queryID);
		
		double releventDocsFound = 0;
		double docIndex = 1;
		double percisionSum = 0;
		for (QrelsRecord qrelsRecord : qrelRankedDocuments) {
			if (relevantDocuments.contains(qrelsRecord)) {
				releventDocsFound++;
				percisionSum+= releventDocsFound / docIndex;
			}
			docIndex++;
		}
		int numOfRelevantDocs = goldResults.getNumberOfRelevantDocuments(queryID);
		return percisionSum / numOfRelevantDocs; 
	}
	
	

}
