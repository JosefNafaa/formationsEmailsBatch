package com.youssefNafaa.batch.processors;

import org.springframework.batch.item.ItemProcessor;

import com.youssefNafaa.batch.dao.SeanceDao;
import com.youssefNafaa.batch.domaine.Planning;

public class PlanningProcessor implements ItemProcessor<Planning, Planning>{

	private final SeanceDao seanceDao;

	public PlanningProcessor(final SeanceDao seanceDao) {
		super();
		this.seanceDao = seanceDao;
	}

	@Override
	public Planning process(final Planning planning) throws Exception {
		planning.setSeances(seanceDao.getByFormateurId(planning.getFormateur().getId()));
		return planning;
	}
}
