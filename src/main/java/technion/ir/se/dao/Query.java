package technion.ir.se.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Query {
	private String id;
	private String content;
	private List<String> terms;
	
	public Query(String id, String content) {
		this.id = id;
		this.content = content;
		this.terms = null;
	}

	public Query(String id, List<String> terms) {
		this.id = id;
		this.terms = terms;
		this.content = StringUtils.join(terms, " ");
	}

	public String getId() {
		return id;
	}

	public String getQueryText() {
		return content;
	}
	
	public List<String> getQueryTerms() {
		if (terms == null) {
			String[] strings = content.split(" ");
			terms = new ArrayList<String>(Arrays.asList(strings));
		}
		return terms;
	}
}
