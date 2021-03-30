package com.youssefNafaa.batch;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.youssefNafaa.batch.dao.SeanceDao;
import com.youssefNafaa.batch.domaine.Planning;
import com.youssefNafaa.batch.mappers.PlanningRowMapper;
import com.youssefNafaa.batch.processors.PlanningProcessor;
import com.youssefNafaa.batch.services.MailContentGenerator;
import com.youssefNafaa.batch.services.MailContentGeneratorImpl;
import com.youssefNafaa.batch.services.PlanningMailSenderService;
import com.youssefNafaa.batch.services.PlanningMailSenderServiceImpl;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateNotFoundException;

@Configuration
public class PlanningStepConfig {

	@Bean()
	public JdbcCursorItemReader<Planning> planningItemReader(final DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Planning>().name("planningItemReader").dataSource(dataSource)
				.sql("select distinct f.* from formateurs f join seances s on f.id=s.id_formateur")
				.rowMapper(new PlanningRowMapper()).build();
	}

	@Bean
	public MailContentGenerator mailContentGenerator(final freemarker.template.Configuration conf)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		return new MailContentGeneratorImpl(conf);
	}

	@Bean
	public PlanningMailSenderService planningMailSenderService(final JavaMailSender javaMailSender) {
		return new PlanningMailSenderServiceImpl(javaMailSender);
	}

	@Bean
	public ItemProcessor<Planning, Planning> planningProcessor(final SeanceDao seanceDao) {
		return new PlanningProcessor(seanceDao);
	}

	@Bean
	public PlanningItemWriter planningWriter(final PlanningMailSenderService planningService,
			final MailContentGenerator mailContentGenerator) {
		return new PlanningItemWriter(planningService, mailContentGenerator);
	}

	@Bean
	public Step planningStep(final StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("planningStep").<Planning, Planning>chunk(10).reader(planningItemReader(null))
				.processor(planningProcessor(null)).writer(planningWriter(null, null)).build();
	}
}
