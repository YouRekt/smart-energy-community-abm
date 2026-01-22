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
	applianceDefaultValues,
	applianceTaskDefaultValues,
	defaultValues,
	householdDefaultValues,
} from '@/routes/config/-components/config-form/schema';
import { Plus, Trash2 } from 'lucide-react';

const HouseholdsSection = withConfigForm({
	defaultValues: defaultValues,
	render: ({ form }) => {
		return (
			<FieldSet className='flex-1'>
				<form.AppField
					name='householdConfigs'
					mode='array'
					children={(field) => {
						const isInvalid =
							field.state.meta.isTouched &&
							!field.state.meta.isValid;

						return (
							<Field data-invalid={isInvalid}>
								<FieldGroup className='@md/field-group:flex-row @md/field-group:items-center @md/field-group:justify-between'>
									<div>
										<FieldLegend>Households</FieldLegend>
										<FieldDescription>
											Configure households.
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
												'householdConfigs',
												householdDefaultValues,
											)
										}>
										<Plus className='mr-2 h-4 w-4' />
										Add Household
									</Button>
								</FieldGroup>
								<ScrollArea className='h-125 pr-4'>
									<FieldGroup>
										{field.state.value.map((_, i) => (
											<FieldSet key={i}>
												<Card className='bg-background rounded-b-[62px]'>
													<CardHeader className='flex items-center justify-between'>
														<div>
															<FieldLegend>
																Household{' '}
																{i + 1}
															</FieldLegend>
															<FieldDescription>
																Configure
																household.
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
														<FieldGroup>
															<form.AppField
																name={`householdConfigs[${i}].householdName`}
																children={(
																	subField,
																) => (
																	<subField.HouseholdNameInput />
																)}
															/>
															<FieldSet>
																<form.AppField
																	name={`householdConfigs[${i}].applianceConfigs`}
																	mode='array'
																	children={(
																		applianceField,
																	) => {
																		const isInvalid =
																			applianceField
																				.state
																				.meta
																				.isTouched &&
																			!applianceField
																				.state
																				.meta
																				.isValid;
																		return (
																			<Field
																				data-invalid={
																					isInvalid
																				}>
																				<div className='flex items-center justify-between'>
																					<div>
																						<FieldLegend>
																							Appliances
																						</FieldLegend>
																						<FieldDescription>
																							Configure
																							appliances.
																						</FieldDescription>
																						{isInvalid && (
																							<FieldError
																								errors={
																									applianceField
																										.state
																										.meta
																										.errors
																								}
																							/>
																						)}
																					</div>
																					<Button
																						type='button'
																						variant='ghost'
																						size='sm'
																						onClick={() =>
																							form.pushFieldValue(
																								`householdConfigs[${i}].applianceConfigs`,
																								applianceDefaultValues,
																							)
																						}>
																						<Plus className='size-3 mr-1' />
																						Add
																						Appliance
																					</Button>
																				</div>
																				<FieldGroup>
																					{applianceField.state.value.map(
																						(
																							_,
																							j,
																						) => (
																							<FieldSet
																								key={
																									j
																								}>
																								<Card className='rounded-b-[38px]'>
																									<CardHeader className='flex items-center justify-between'>
																										<div>
																											<FieldLegend>
																												Appliance{' '}
																												{j +
																													1}
																											</FieldLegend>
																											<FieldDescription>
																												Configure
																												the
																												appliance.
																											</FieldDescription>
																										</div>
																										<Button
																											type='button'
																											variant='ghost'
																											size='icon'
																											onClick={() =>
																												applianceField.removeValue(
																													j,
																												)
																											}>
																											<Trash2 className='text-destructive size-4' />
																										</Button>
																									</CardHeader>
																									<CardContent>
																										<FieldGroup>
																											<form.AppField
																												name={`householdConfigs[${i}].applianceConfigs[${j}].applianceName`}
																												children={(
																													subField,
																												) => (
																													<subField.ApplianceNameInput />
																												)}
																											/>

																											<FieldSet>
																												<form.AppField
																													name={`householdConfigs[${i}].applianceConfigs[${j}].tasks`}
																													mode='array'
																													children={(
																														taskField,
																													) => {
																														const isInvalid =
																															taskField
																																.state
																																.meta
																																.isTouched &&
																															!taskField
																																.state
																																.meta
																																.isValid;

																														return (
																															<Field
																																data-invalid={
																																	isInvalid
																																}>
																																<div className='flex items-center justify-between'>
																																	<div>
																																		<FieldLegend>
																																			Tasks
																																		</FieldLegend>
																																		<FieldDescription>
																																			Define
																																			the
																																			tasks.
																																		</FieldDescription>
																																		{isInvalid && (
																																			<FieldError
																																				errors={
																																					taskField
																																						.state
																																						.meta
																																						.errors
																																				}
																																			/>
																																		)}
																																	</div>
																																	<Button
																																		type='button'
																																		variant='ghost'
																																		size='sm'
																																		onClick={() =>
																																			form.pushFieldValue(
																																				`householdConfigs[${i}].applianceConfigs[${j}].tasks`,
																																				{
																																					...applianceTaskDefaultValues,
																																					taskId:
																																						i *
																																							100 +
																																						j *
																																							10 +
																																						form
																																							.state
																																							.values
																																							.householdConfigs[
																																							i
																																						]
																																							.applianceConfigs[
																																							j
																																						]
																																							.tasks
																																							.length,
																																				},
																																			)
																																		}>
																																		<Plus className='size-3 mr-1' />
																																		Add
																																		Task
																																	</Button>
																																</div>
																																<FieldGroup>
																																	{taskField.state.value.map(
																																		(
																																			_,
																																			k,
																																		) => (
																																			<FieldGroup
																																				key={
																																					k
																																				}>
																																				<FieldSet>
																																					<Card className='bg-muted'>
																																						<CardHeader className='flex items-center justify-between'>
																																							<div>
																																								<FieldLegend>
																																									Task{' '}
																																									{k +
																																										1}
																																								</FieldLegend>
																																								<FieldDescription>
																																									Define
																																									the
																																									task.
																																								</FieldDescription>
																																							</div>
																																							<Button
																																								type='button'
																																								variant='ghost'
																																								size='icon'
																																								onClick={() =>
																																									taskField.removeValue(
																																										k,
																																									)
																																								}>
																																								<Trash2 className='text-destructive size-4' />
																																							</Button>
																																						</CardHeader>
																																						<CardContent>
																																							<FieldGroup>
																																								<FieldGroup className='@xl/field-group:flex-row'>
																																									<form.AppField
																																										name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].taskName`}
																																										children={(
																																											f,
																																										) => (
																																											<f.TaskNameInput />
																																										)}
																																									/>
																																									<form.AppField
																																										name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].postponable`}
																																										children={(
																																											f,
																																										) => (
																																											<f.TaskPostponableInput />
																																										)}
																																									/>
																																								</FieldGroup>
																																								<FieldGroup className='@xl/field-group:flex-row'>
																																									<FieldGroup>
																																										<FieldGroup className='@xl/field-group:flex-row'>
																																											<form.AppField
																																												name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].energyPerTick`}
																																												children={(
																																													f,
																																												) => (
																																													<f.TaskEnergyPerTickInput />
																																												)}
																																											/>
																																											<form.AppField
																																												name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].duration`}
																																												children={(
																																													f,
																																												) => (
																																													<f.TaskDurationInput />
																																												)}
																																											/>
																																										</FieldGroup>
																																										<FieldGroup className='@xl/field-group:flex-row'>
																																											<form.AppField
																																												name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].period`}
																																												children={(
																																													f,
																																												) => (
																																													<f.TaskPeriodInput />
																																												)}
																																											/>
																																											<form.AppField
																																												name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].humanActivationChance`}
																																												children={(
																																													f,
																																												) => (
																																													<f.TaskHumanActivationChanceInput />
																																												)}
																																											/>
																																										</FieldGroup>
																																									</FieldGroup>
																																								</FieldGroup>
																																							</FieldGroup>
																																						</CardContent>
																																					</Card>
																																				</FieldSet>
																																			</FieldGroup>
																																		),
																																	)}
																																</FieldGroup>
																															</Field>
																														);
																													}}
																												/>
																											</FieldSet>
																										</FieldGroup>
																									</CardContent>
																								</Card>
																							</FieldSet>
																						),
																					)}
																				</FieldGroup>
																			</Field>
																		);
																	}}
																/>
															</FieldSet>
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

export { HouseholdsSection };
