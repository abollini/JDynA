package it.cilea.osd.jdyna.validator;


import it.cilea.osd.jdyna.dto.DTOGestioneAlberoClassificatore;
import it.cilea.osd.jdyna.service.IValidatorClassificationService;
import it.cilea.osd.jdyna.service.ValidatorService.ValidationResult;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@RemoteProxy(creator = SpringCreator.class, creatorParams = @Param(name = "beanName", value = "classificazioneValidator"))
public class ClassificazioneValidator extends JDynaBaseValidator {
	
	protected final Log log = LogFactory.getLog(getClass());
    private List<String> messages;

    
    public ClassificazioneValidator(IValidatorClassificationService validatorService) {
		super(validatorService);		
	}
    
	public boolean supports(Class clazz) {
		return DTOGestioneAlberoClassificatore.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object object, Errors errors) {			
		DTOGestioneAlberoClassificatore classificazione = (DTOGestioneAlberoClassificatore) object;
		//valida solo la form di edit del dto e quella della sotto classificazione
		validateEditLevel(object, errors);
		if(classificazione.isCreateSubClassificazione()) {
			validateSubLevel(object, errors);
		}
	}
				 

	
	/** 
	 *  Verify that the code is filled and unique withing the tree. 
	 */
	public void validateTopLevel(Object object,Errors errors)  {
		DTOGestioneAlberoClassificatore classificazione = (DTOGestioneAlberoClassificatore) object;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topNome",
		"error.message.fallita.campo.obbligatorio");			

		Integer albero = classificazione.getId();
		String codice = classificazione.getTopCodice();
		if(codice != null && albero != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "topCodice",
			"error.message.fallita.campo.obbligatorio");			
			ValidationResult result = ((IValidatorClassificationService)getValidatorService()).controllaCodiceClassificazioneByIdAlberoECodice(albero,codice);
			if (!result.isSuccess()) {					
				errors.rejectValue("topCodice",result.getMessage());
			}			
			
		}
	}
	public void validateNome(Object object,Errors errors)  {
		DTOGestioneAlberoClassificatore classificazione = (DTOGestioneAlberoClassificatore) object;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "error.albero.nome.non.inserito");
		if(classificazione.getNome()!=""){
			ValidationResult result = ((IValidatorClassificationService)getValidatorService()).validaNomeAlberoInModifica(classificazione.getNome(),classificazione.getId(),errors);
				if (!result.isSuccess()) {	
					errors.rejectValue("nome","error.albero.nome.esistente");
				}
		}
		
	}
	public void validateEditLevel(Object object,Errors errors)  {
		
	DTOGestioneAlberoClassificatore classificazione = (DTOGestioneAlberoClassificatore) object;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "editNome",
		"error.message.fallita.campo.obbligatorio");			
		
		Integer albero = classificazione.getId();
		String codice = classificazione.getEditCodice();
		if(codice != null && albero != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "editCodice",
			"error.message.fallita.campo.obbligatorio");
			//controllo prima se il vecchio valore della classificazione 
			ValidationResult result = ((IValidatorClassificationService)getValidatorService()).controllaCodiceClassificazioneByIdAlberoIdClassificazioneCodice(albero,classificazione.getEditID(),codice);
			if (!result.isSuccess()) {					
				errors.rejectValue("editCodice",result.getMessage());
			}			
			
		}
	}
	
	public void validateSubLevel(Object object,Errors errors)  {
		
	DTOGestioneAlberoClassificatore classificazione = (DTOGestioneAlberoClassificatore) object;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subNome",
		"error.message.fallita.campo.obbligatorio");			
		
		Integer albero = classificazione.getId();
		String codice = classificazione.getSubCodice();
		if(codice != null && albero != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subCodice",
			"error.message.fallita.campo.obbligatorio");			
			ValidationResult result = ((IValidatorClassificationService)getValidatorService()).controllaCodiceClassificazioneByIdAlberoECodice(albero,codice);
			if (!result.isSuccess()) {					
				errors.rejectValue("subCodice",result.getMessage());
			}			
			
		}
	}
    
	public List<String> getMessages() {
		if (messages == null) return new ArrayList<String>();
        return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
