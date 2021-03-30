package com.youssefNafaa.batch;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.youssefNafaa.batch.deciders.SeancesStepDecider;
import com.youssefNafaa.batch.validators.MyJobParametersValidators;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	@Bean
	public JobParametersValidator defaultJobParametersValidator() {
		DefaultJobParametersValidator bean = new DefaultJobParametersValidator();
		bean.setRequiredKeys(new String[] { "formateursFile", "formationsFile", "seancesFile" });
		bean.setOptionalKeys(new String[] { "run.id" });
		return bean;
	}

	@Bean
	public JobParametersValidator myJobParametersValidators() {
		return new MyJobParametersValidators();
	}

	@Bean
	public JobParametersValidator compositeJobParametersValidator() {
		CompositeJobParametersValidator bean = new CompositeJobParametersValidator();
		bean.setValidators(Arrays.asList(defaultJobParametersValidator(), myJobParametersValidators()));
		return bean;

	}

	@Bean
	public JobExecutionDecider seancesStepDecider() {
		return new SeancesStepDecider();
	}

	@Bean
	public Step step1(final StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("step1").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Flow chargementFormateursFlow(final Step chargementFormateursStep) {
		return new FlowBuilder<Flow>("chargementFormateursFlow")
				.start(chargementFormateursStep)
				.end();
	}

	@Bean
	public Flow chargementFormationsFlow(final Step chargementFormationsStep) {
		return new FlowBuilder<Flow>("chargementFormationsFlow")
				.start(chargementFormationsStep)
				.end();
	}

	@Bean
	public Flow parallelFlow() {
		return new FlowBuilder<Flow>("parallelFlow")
				.split(new SimpleAsyncTaskExecutor())
				.add(chargementFormateursFlow(null), chargementFormationsFlow(null))
				.end();
	}

	@Bean
	public Job job(final JobBuilderFactory jobBuilderFactory, final Step chargementSeancesCsvStep,
			final Step chargementSeancesTxtStep, final Step planningStep) {
		return jobBuilderFactory.get("formations-batch")
				.start(parallelFlow())
				.next(seancesStepDecider()).on("txt").to(chargementSeancesTxtStep)
				.from(seancesStepDecider()).on("csv").to(chargementSeancesCsvStep)
				.from(chargementSeancesTxtStep).on("*").to(planningStep)
				.from(chargementSeancesCsvStep).on("*").to(planningStep)
				.end()
				.validator(compositeJobParametersValidator())
				.incrementer(new RunIdIncrementer())
				.build();
	}
}
