package com.youssefNafaa.batch;

import static com.youssefNafaa.batch.mappers.SeanceItemPreparedStatementListener.SEANCES_INSERT_QUERY;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;

import com.youssefNafaa.batch.domaine.Seance;
import com.youssefNafaa.batch.listeners.ChargementSeancesStepListener;
import com.youssefNafaa.batch.mappers.SeanceItemPreparedStatementListener;
import com.youssefNafaa.batch.policies.SeancesSkipPolicy;

@Configuration
public class CharegementSeanceStepConfig {

	@Bean
	@StepScope
	public FlatFileItemReader<Seance> seanceCsvItemReader(
			@Value("#{jobParameters['seancesFile']}") final Resource inputFile) {
		return new FlatFileItemReaderBuilder<Seance>().name("seanceCsvItemReader").resource(inputFile).delimited()
				.delimiter(";").names(new String[] { "codeFormation", "idFormateur", "dateDebut", "dateFin" })
				.fieldSetMapper(seanceFieldSetMapper(null)).build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Seance> seanceTxtItemReader(
			@Value("#{jobParameters['seancesFile']}") final Resource inputFile) {
		return new FlatFileItemReaderBuilder<Seance>().name("seanceTxtItemReader").resource(inputFile).fixedLength()
				.columns(new Range[] { new Range(1, 16), new Range(17, 20), new Range(25, 32), new Range(37, 44) })
				.names(new String[] { "codeFormation", "idFormateur", "dateDebut", "dateFin" })
				.fieldSetMapper(seanceFieldSetMapper(null)).build();
	}

	@Bean
	public ConversionService myConversionService() {
		DefaultConversionService dcs = new DefaultConversionService();
		DefaultConversionService.addDefaultConverters(dcs);
		dcs.addConverter(new Converter<String, LocalDate>() {

			@Override
			public LocalDate convert(final String input) {
				DateTimeFormatter df = DateTimeFormatter.ofPattern("ddMMyyyy");
				return LocalDate.parse(input, df);
			}
		});

		return dcs;
	}

	@Bean
	public SkipPolicy seanceSkipPolicy() {
		return new SeancesSkipPolicy();
	}

	@Bean
	public FieldSetMapper<Seance> seanceFieldSetMapper(final ConversionService myConversionService) {
		BeanWrapperFieldSetMapper<Seance> bean = new BeanWrapperFieldSetMapper<>();
		bean.setTargetType(Seance.class);
		bean.setConversionService(myConversionService);
		return bean;
	}

	@Bean
	public JdbcBatchItemWriter<Seance> seanceItemWriter(final DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Seance>().dataSource(dataSource).sql(SEANCES_INSERT_QUERY)
				.itemPreparedStatementSetter(new SeanceItemPreparedStatementListener()).build();
	}

	@Bean
	public Step chargementSeancesCsvStep(final StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("chargementSeancesCsvStep").<Seance, Seance>chunk(10)
				.reader(seanceCsvItemReader(null)).writer(seanceItemWriter(null)).faultTolerant()
				.skipPolicy(seanceSkipPolicy()).listener(chargementSeancesListener()).build();
	}

	@Bean
	public Step chargementSeancesTxtStep(final StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("chargementSeancesCsvStep").<Seance, Seance>chunk(10)
				.reader(seanceTxtItemReader(null)).writer(seanceItemWriter(null)).faultTolerant()
				.skipPolicy(seanceSkipPolicy()).listener(chargementSeancesListener()).build();
	}

	@Bean
	public StepExecutionListener chargementSeancesListener() {
		return new ChargementSeancesStepListener();
	}
}
