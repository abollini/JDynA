package it.cilea.osd.jdyna.service;

import it.cilea.osd.common.model.Selectable;
import it.cilea.osd.jdyna.model.AlberoClassificatorio;
import it.cilea.osd.jdyna.model.Classificazione;
import it.cilea.osd.jdyna.web.tag.JDynATagLibraryFunctions;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.webflow.executor.FlowExecutor;

@RemoteProxy(creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "ajaxService"))
public class AjaxService implements IAjaxDynaService {
	
	protected Log log = LogFactory.getLog(getClass());
	
	protected IPersistenceDynaService applicationService;
	
	protected IPersistenceClassificationService classificationApplicationService;
	
	protected IValidatorDynaService validatorService;	

	protected ISearchDynaService searchService;
	
	protected FlowExecutor flowExecutor;
		
	protected PropertyEditor dateEditor;
	
	protected final String patternPercento = "%1$04.2f";
		
	protected DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	protected Map<String, String> urlMapDetails;
	
	public AjaxService(IPersistenceDynaService applicationService, IValidatorDynaService validatorService, ISearchDynaService searchService) {
		this.applicationService = applicationService;
		this.validatorService = validatorService;
		this.searchService = searchService;
	}

	public void setClassificationApplicationService(
			IPersistenceClassificationService classificationApplicationService) {
		this.classificationApplicationService = classificationApplicationService;
	}

	public Map<String, String> getUrlMapDetails() {
		return urlMapDetails;
	}

	public void setUrlMapDetails(Map<String, String> urlMapDetails) {
		this.urlMapDetails = urlMapDetails;
	}


	public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}


	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}



	public void setDateEditor(PropertyEditor dateEditor) {
		this.dateEditor = dateEditor;
	}



	
	@RemoteMethod
	public Classificazione getClassificazioneByCodice(String codice,String nomeAlbero) {
		//return applicationService.getClassificazioneByCodice(codice);
		return classificationApplicationService.getClassificazioneByCodice(nomeAlbero, codice);
	}
	
	@RemoteMethod
	public String getInfoOnDeleteTipologiaProprieta(String idTipologia) {
		long count = applicationService.countValoriByTipologiaProprieta(Integer.parseInt(idTipologia));
		if(count==0) {
			return "Nessun dato sulla piattaforma dipendente da questo metadato";
		}
		return "Ci sono sulla piattaforma " + applicationService.countValoriByTipologiaProprieta(Integer.parseInt(idTipologia)) + " dato/i dipendenti da questo metadato, cancellando rimuoverai tutte le proprieta' degli oggetti di questa tipologia";				
	}
	
	@RemoteMethod
	public List<Classificazione> getClassificazioni(String nomeAlbero,String codice) {
	
		log.debug("e' partito ajax --------------->codice"+codice);
		log.debug("e' partito ajax -------------->pkey albero"+nomeAlbero);
	//	parserizzo la stringa text
		String resultCodice = "";
		if(!codice.equals("")){
			nomeAlbero = "";
			resultCodice = "";
			int index = codice.lastIndexOf(":");
			
			if(codice.trim().equals("")) {
				return null;
			}
			else {
				 resultCodice = codice.substring(index+1);
				 nomeAlbero = codice.substring(0, index);
			}
		}
		AlberoClassificatorio albero = (AlberoClassificatorio)classificationApplicationService.getAlberoByName(nomeAlbero);
		
        if (resultCodice != null && resultCodice.trim().length() == 0)
        	resultCodice = null;
            
        log.debug("chiamo getClassificazioniPadre con "+albero.getId());            
		List<Classificazione> results =  classificationApplicationService.getSubClassificazioni(albero.getId(), resultCodice,true);
        for (Classificazione result : results)
        {
            log.debug("AJAXSERVICE.java---getClassificazioni---- Class: "+result.getDisplayValue()+ " -- "+result.getIdentifyingValue());
        }
 
        return results;
	}
	
	
	@RemoteMethod
	public <T extends Selectable> List<String> searchCollisioni(String field, String classCollisione,String inputValue,String rootPath) throws IOException, ParseException, ClassNotFoundException{
		
		log.debug("chiamato searchCollisioni con parametri:" + classCollisione +" - "+ inputValue);
		StringBuffer inputToken = new StringBuffer();
		Class<T> classeCollisione = (Class<T>) Class.forName(classCollisione);		
		//devo cercare le collisioni e ritornare 3 stringhe che mi rappresentano il numero di collisioni, il displayValue del primo risultato e il link al dettaglio del primo risultato 
		String collisioneQuery = JDynATagLibraryFunctions.getCollisioniQuery(inputValue);

		int numeroRisultati = searchService.count(field, collisioneQuery, classeCollisione);
		String displayValue = "";
		String identifyingValue = "";
		
		List<Object[]> documentList = searchService.searchWrapperWithProjection(field, collisioneQuery,classeCollisione,1);
		for(Object documentObj[]:documentList){
			//in questo caso ho un solo campo quindi un array con un solo elemento (perche ho fatto la setProjection con un solo parametro
			Document document=(Document)documentObj[0];
			
			Field fieldDisplay=document.getField("displayValue");
			displayValue=fieldDisplay.stringValue();
			Field fieldIdentifying=document.getField("identifyingValue");
			identifyingValue=fieldIdentifying.stringValue();
		}		

		//costruisco il link di dettaglio
		String dettaglio = rootPath;
		
		dettaglio += getUrlMapDetails().get(classeCollisione.getName());
		dettaglio += "?id="+identifyingValue;
		
		//prima stringa: il display value del primo risultato della ricerca  
		List<String> results = new LinkedList<String>();
		results.add(displayValue);
		results.add(dettaglio);
		results.add(String.valueOf(numeroRisultati));
		results.add(collisioneQuery);
		return results;
	}

	
}
