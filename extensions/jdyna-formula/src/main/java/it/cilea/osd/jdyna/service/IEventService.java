package it.cilea.osd.jdyna.service;

import it.cilea.osd.jdyna.event.IEvent;
import it.cilea.osd.jdyna.event.ISubscriber;

public interface IEventService {
	/**
	 * Wrapper per la registrazione dei subscribers al sistema di notifica
	 * @param <E> la classe di eventi a cui il subscriber e' interessato
	 * @param subscriber
	 * @param eventClass
	 */
	public <E extends IEvent> void addSubscriber(ISubscriber<E> subscriber,
			Class<E> eventClass);
	
	/**
	 * Wrapper per la rimozione dei subscribers dal sistema di notifica
	 * @param <E> la classe di eventi a cui il subscriber non e' piu' interessato
	 * @param subscriber
	 * @param eventClass
	 */
	public <E extends IEvent> void removeSubscriber(ISubscriber<E> subscriber,
			Class<E> eventClass);
}
