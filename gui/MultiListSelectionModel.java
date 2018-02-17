package sbeam2.gui;

import javax.swing.DefaultListSelectionModel;

public class MultiListSelectionModel extends DefaultListSelectionModel {

	private int sels = 0;
	private int limit = 1;
	private boolean check = true;
	@Override
	public void setSelectionInterval(int index0, int index1) {
		if (index0 != index1) {
			if(sels >= limit && check){
				this.removeSelectionInterval(this.getLeadSelectionIndex(), this.getAnchorSelectionIndex());
				sels--;
			}
			super.setSelectionInterval(index0, index1);
			sels++;
			return;
		}
		if (isSelectedIndex(index0)) {
			super.removeSelectionInterval(index0, index1);
			sels--;
		} else {
			if(sels >= limit && check){
				this.removeSelectionInterval(this.getLeadSelectionIndex(), this.getAnchorSelectionIndex());
				sels--;
			}
			super.addSelectionInterval(index0, index1);
			sels++;
		}
	}
	
	public void setLimit(int n){
		limit = n;
	}
	
	public void setCheck(boolean c){
		check = c;
	}

	public void clearSels(){
		sels = 0;
	}
}
