/*
 * JDynA, Dynamic Metadata Management for Java Domain Object
 *
 * Copyright (c) 2008, CILEA and third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by CILEA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License v3 or any later version, as published 
 * by the Free Software Foundation, Inc. <http://fsf.org/>.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package it.cilea.osd.jdyna.model;

import it.cilea.osd.common.model.IdentifiableObject;
import it.cilea.osd.common.model.Selectable;
import it.cilea.osd.jdyna.widget.Size;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;


/**
 * Classe che rappresenta una tipologia di proprieta
 * 
 * @author pascarelli
 * @author biondo
 * @author bollini
 */
@Entity
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
@DataTransferObject
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class PropertiesDefinition extends IdentifiableObject implements Selectable, Comparable<PropertiesDefinition>, IPropertiesDefinition {
	//FIXME aggiungere campi -valore di default e -valore di test (servono anche per creare il validatore delle formule)
	
	/** Primary key identifier*/
	@Id
	//@GeneratedValue(strategy = GenerationType.TABLE)		
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROPERTIESDEFINITION_SEQ")
    @SequenceGenerator(name = "PROPERTIESDEFINITION_SEQ", sequenceName = "PROPERTIESDEFINITION_SEQ")
	private Integer id;
	
	/** A unique textual identifier */
	@Column (unique = true)
	private String shortName;
	
	/** The fact of being field obligatory */
	private boolean mandatory;
	
	/** Field repeatability*/
	private boolean repeatable;
	
	/** Require new line after it */
	private boolean newline;
	
	/**
	 * Label min size 
	 */
	private int labelMinSize;
	
	/**
	 * Field row and column min size
	 */
	@AttributeOverrides(value = {
			@AttributeOverride(name = "col", column = @Column(name= "fieldmin_col")),
			@AttributeOverride(name = "row", column = @Column(name= "fieldmin_row"))
	})	
	private Size fieldMinSize;
	
	/** Label */	
	private String label;
	
	/**Rendering*/
	@OneToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(unique = true)
	@Cascade (value = {CascadeType.ALL,CascadeType.DELETE_ORPHAN})
	private AWidget rendering;
    
	/**
	 * Definisce se questa tipologia di proprieta' deve essere visualizzata 
	 * nelle tabelle di riepilogo
	 * 
	 **/
	private boolean showInList;
	
	@Column (nullable=true)
	/** Definisce se la tipologia di proprieta deve essere mostrata in
	 * 	modalita' di creazione */
	private boolean onCreation;
	
	/** 
	 * Indica la priorita' con cui deve essere visualizzata nei riepiloghi delle 
	 * anagrafiche. Maggiore e' la priorita' prima sara' visualizzata la tipologia (default 0)
	 **/
	private int priority;
	
	@Type(type="text")
	/**
	 * Testo di help da mostrare durante l'editing di proprieta' di questa tipologia
	 */
	private String help;
	
	/** Indica se la tipologia e' di primo livello o meno (default true)*/	
	private boolean topLevel;
	
	/** Indica se la tipologia proprieta e' da indicizzare per la ricerca semplice*/
	private boolean simpleSearch;

	/** Indica se la tipologia proprieta e' da indicizzare per la ricerca avanzata*/
	private boolean advancedSearch;
	
	public Integer getLabelMinSize() {
		return labelMinSize;
	}

	public void setLabelMinSize(Integer labelMinSize) {
		this.labelMinSize = labelMinSize;
	}

	public Size getFieldMinSize() {
		return fieldMinSize;
	}

	public void setFieldMinSize(Size fieldMinSize) {
		this.fieldMinSize = fieldMinSize;
	}

	public boolean isNewline() {
		return newline;
	}

	public void setNewline(boolean newline) {
		this.newline = newline;
	}

	//costruttore
	public PropertiesDefinition() {
		topLevel = true;
		priority = 0;
		fieldMinSize = new Size();
		fieldMinSize.setCol(0);
		fieldMinSize.setRow(0);
	}
	
	//accessori e setter
	public boolean isOnCreation() {
		return onCreation;
	}
	public void setOnCreation(boolean onCreation) {
		this.onCreation = onCreation;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean obbligatorieta) {
		this.mandatory = obbligatorieta;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public AWidget getRendering() {
		return rendering;
	}
	public void setRendering(AWidget rendering) {
		this.rendering = rendering;		
		//setFkrendering(rendering.getId());
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public boolean isRepeatable() {
		return repeatable;
	}
	public void setRepeatable(boolean ripetibile) {
		this.repeatable = ripetibile;
	}
	public boolean isShowInList() {
		return showInList;
	}
	public void setShowInList(boolean mostraValore) {
		this.showInList = mostraValore;
	}
    
	@Transient
	@RemoteMethod
	public String getDisplayValue() {
		return shortName;
	}
	
	@Override
	public String toString() {
		return super.toString()+":"+shortName;
	}
	
	@Transient
	@RemoteMethod
	public String getIdentifyingValue() {
		return getId().toString();
	}

	@Override
	public boolean equals(Object object)
	{
	    if (object == null) return false;
        if (object instanceof PropertiesDefinition && this.getClass() == object.getClass())
        {
            PropertiesDefinition tipologiaConfronto = (PropertiesDefinition) object;
            if (shortName.equals(tipologiaConfronto.getShortName())) return true;            
        }
	    return false;
	}
	
	@Override
	public int hashCode() {
		return shortName != null ? shortName.hashCode() : 0;
	}
	
	/**
	 * Restituisce l'ordinamento tra due tipologie di proprieta' in base alla priorita'. 
	 * In caso di pari priorita' viene utilizzato l'ordinamento alfabetico degli <code>shortName</code>
	 * 
	 * @return l'ordinamento in base alla priorita'
	 * @see PropertiesDefinition#priority
	 */	
	public int compareTo(PropertiesDefinition secondTip) {		
		if (secondTip == null) return -1;
		if (priority < secondTip.priority) return -1;
		else if (priority > secondTip.priority) return 1;
			 else return shortName.compareTo(secondTip.getShortName());
	}
	
	public boolean isTopLevel() {
		return topLevel;
	}
	
	public void setTopLevel(boolean topLevel) {
		this.topLevel = topLevel;
	}

	public boolean isSimpleSearch() {
		return simpleSearch;
	}

	public void setSimpleSearch(boolean simpleSearch) {
		this.simpleSearch = simpleSearch;
	}

	public boolean isAdvancedSearch() {
		return advancedSearch;
	}

	public void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priorita) {
		this.priority = priorita;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
		
	/** Il metodo restituisce la classe dell'oggetto di riferimento della tipologia proprieta'; 
	 * 	Ad esempio per l'oggetto TipologiaProprietaOpera restituisce Opera.class
	 * */	
	@Transient
	public abstract Class getAnagraficaHolderClass();		
	
	/** Il metodo restituisce la classe della proprieta' di riferimento della tipologia proprieta'; 
	 * 	Ad esempio per l'oggetto TipologiaProprietaOpera restituisce ProprietaOpera.class
	 * */	
	@Transient
	public abstract Class getPropertyHolderClass();
	
	/** Il metodo restituisce il decorator di tipo contenibile per funzionalita' web
	 * */	
	@Transient
	public abstract Class getDecoratorClass();
	
}
