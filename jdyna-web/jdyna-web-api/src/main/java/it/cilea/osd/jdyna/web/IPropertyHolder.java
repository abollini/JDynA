package it.cilea.osd.jdyna.web;

import it.cilea.osd.common.model.Identifiable;

import java.util.List;


public interface IPropertyHolder<C extends IContainable> extends Identifiable {
	
	public String getTitle();
		
	public List<C> getMask();
		
	public void setMask(List<C> mask);
}
