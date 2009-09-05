package de.d3web.dialog2.frequentness;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

public interface FrequentnessInterface {

    public List<DataGroup> getDataGroupWithFrequentnessData();

    public Collection<SelectItem> getSelectData();

    public List<String> getSelectedData();

    public boolean isDataAvailable();

    public boolean isDataSelected();

    public void setSelectedData(List<String> selectedData);
}
