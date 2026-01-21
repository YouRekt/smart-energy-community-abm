import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldGroup,
	FieldLegend,
	FieldSet,
} from '@/components/ui/field';
import { ScrollArea } from '@/components/ui/scroll-area';
import { withConfigForm } from '@/routes/config/-components/config-form/form-context';
import {
	defaultValues,
	energySourceDefaultValues,
} from '@/routes/config/-components/config-form/schema';
import { Plus, Trash2 } from 'lucide-react';

const EnergySourcesSection = withConfigForm({
	defaultValues: defaultValues,
	render: ({ form }) => {
		return (
			<FieldSet className='flex-1'>
				<form.AppField
					name='energySourcesConfigs'
					mode='array'
					children={(field) => {
						const isInvalid =
							field.state.meta.isTouched &&
							!field.state.meta.isValid;
						return (
							<Field data-invalid={isInvalid}>
								<FieldGroup className='@md/field-group:flex-row @md/field-group:items-center @md/field-group:justify-between'>
									<div>
										<FieldLegend>
											Energy Sources
										</FieldLegend>
										<FieldDescription>
											Define the energy sources in the
											community.
										</FieldDescription>
										{isInvalid && (
											<FieldError
												errors={field.state.meta.errors}
											/>
										)}
									</div>
									<Button
										type='button'
										variant='outline'
										size='sm'
										onClick={() =>
											form.pushFieldValue(
												'energySourcesConfigs',
												energySourceDefaultValues,
											)
										}>
										<Plus className='mr-2 h-4 w-4' />
										Add Source
									</Button>
								</FieldGroup>
								<ScrollArea className='h-125 pr-4'>
									<FieldGroup>
										{field.state.value.map((_, i) => (
											<FieldSet key={i}>
												<Card>
													<CardHeader className='flex items-center justify-between'>
														<div>
															<FieldLegend>
																Energy Source{' '}
																{i + 1}
															</FieldLegend>
															<FieldDescription>
																Define the
																energy source.
															</FieldDescription>
														</div>
														<Button
															type='button'
															variant='ghost'
															size='icon'
															onClick={() =>
																field.removeValue(
																	i,
																)
															}>
															<Trash2 className='text-destructive size-4' />
														</Button>
													</CardHeader>
													<CardContent>
														<FieldGroup className='@4xl/field-group:flex-row'>
															<FieldGroup>
																<form.AppField
																	name={`energySourcesConfigs[${i}].agentName`}
																	children={(
																		subField,
																	) => (
																		<subField.EnergySourceAgentNameInput />
																	)}
																/>
																<FieldGroup className='@md/field-group:flex-row'>
																	<form.AppField
																		name={`energySourcesConfigs[${i}].period`}
																		children={(
																			subField,
																		) => (
																			<subField.EnergySourcePeriodInput />
																		)}
																	/>
																	<form.AppField
																		name={`energySourcesConfigs[${i}].maxOutputPower`}
																		children={(
																			subField,
																		) => (
																			<subField.EnergySourceMaxOutputPowerInput />
																		)}
																	/>
																</FieldGroup>
															</FieldGroup>
															<FieldGroup>
																<form.AppField
																	name={`energySourcesConfigs[${i}].peakTick`}
																	children={(
																		subField,
																	) => (
																		<subField.EnergySourcePeakTickInput />
																	)}
																/>
																<FieldGroup className='@md/field-group:flex-row'>
																	<form.AppField
																		name={`energySourcesConfigs[${i}].stdDev`}
																		children={(
																			subField,
																		) => (
																			<subField.EnergySourceStdDevInput />
																		)}
																	/>
																	<form.AppField
																		name={`energySourcesConfigs[${i}].variation`}
																		children={(
																			subField,
																		) => (
																			<subField.EnergySourceVariationInput />
																		)}
																	/>
																</FieldGroup>
															</FieldGroup>
														</FieldGroup>
													</CardContent>
												</Card>
											</FieldSet>
										))}
									</FieldGroup>
								</ScrollArea>
							</Field>
						);
					}}
				/>
			</FieldSet>
		);
	},
});

export { EnergySourcesSection };
