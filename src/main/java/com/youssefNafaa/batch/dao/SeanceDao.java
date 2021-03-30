package com.youssefNafaa.batch.dao;

import java.util.List;

import com.youssefNafaa.batch.domaine.PlanningItem;

public interface SeanceDao {
	int count();

	List<PlanningItem> getByFormateurId(Integer formateurId);
}
