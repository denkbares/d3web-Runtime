package de.d3web.dialog2.frequentness;

import java.util.ArrayList;
import java.util.List;

public class DataGroup {

    private String text;

    private List<DataWithFrequentness> dataWithFrequentnessList;

    public DataGroup(String text) {
	this.text = text;
	dataWithFrequentnessList = new ArrayList<DataWithFrequentness>();
    }

    public void addDataWithFrequentness(DataWithFrequentness data) {
	dataWithFrequentnessList.add(data);
    }

    public List<DataWithFrequentness> getDataWithFrequentnessList() {
	return dataWithFrequentnessList;
    }

    public String getText() {
	return text;
    }

}
