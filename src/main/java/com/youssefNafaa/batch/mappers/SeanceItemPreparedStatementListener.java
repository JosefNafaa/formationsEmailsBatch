package com.youssefNafaa.batch.mappers;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.youssefNafaa.batch.domaine.Seance;

public class SeanceItemPreparedStatementListener implements ItemPreparedStatementSetter<Seance> {

	public static final String SEANCES_INSERT_QUERY = "INSERT INTO seances (code_formation, id_formateur, date_debut,date_fin) VALUES (?,?,?,?);";

	@Override
	public void setValues(final Seance seance, final PreparedStatement ps) throws SQLException {
		ps.setString(1, seance.getCodeFormation());
		ps.setInt(2, seance.getIdFormateur());
		ps.setDate(3, Date.valueOf(seance.getDateDebut()));
		ps.setDate(4, Date.valueOf(seance.getDateFin()));
	}

}
 