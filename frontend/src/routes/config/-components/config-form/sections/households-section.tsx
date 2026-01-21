import { Button } from '@/components/ui/button';
import {
	FieldDescription,
	FieldGroup,
	FieldLegend,
	FieldSet,
} from '@/components/ui/field';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
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
			<FieldSet>
				<div className='flex items-center justify-between'>
					<div>
						<FieldLegend>Households</FieldLegend>
						<FieldDescription>
							Configure households, appliances, and tasks.
						</FieldDescription>
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
				</div>

				<FieldGroup>
					<form.AppField
						name='householdConfigs'
						mode='array'
						children={(field) => (
							<ScrollArea className='h-[500px] rounded-md border p-4'>
								<div className='flex flex-col gap-6'>
									{field.state.value.map((_, i) => (
										<div
											key={i}
											className='flex flex-col gap-4 border p-4 rounded-md relative'>
											<div className='flex items-center justify-between'>
												<h4 className='text-sm font-medium'>
													Household {i + 1}
												</h4>
												<Button
													type='button'
													variant='ghost'
													size='icon'
													onClick={() =>
														field.removeValue(i)
													}>
													<Trash2 className='text-destructive h-4 w-4' />
												</Button>
											</div>

											<form.AppField
												name={`householdConfigs[${i}].householdName`}
												children={(subField) => (
													<subField.HouseholdNameInput />
												)}
											/>

											<Separator />

											{/* Appliances */}
											<div className='pl-2'>
												<div className='flex items-center justify-between mb-2'>
													<span className='text-xs font-semibold text-muted-foreground uppercase'>
														Appliances
													</span>
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
														<Plus className='h-3 w-3 mr-1' />{' '}
														Add
													</Button>
												</div>

												<form.AppField
													name={`householdConfigs[${i}].applianceConfigs`}
													mode='array'
													children={(
														applianceField,
													) => (
														<div className='flex flex-col gap-3'>
															{applianceField.state.value?.map(
																(_, j) => (
																	<div
																		key={j}
																		className='bg-muted/30 p-3 rounded-md border text-sm'>
																		<div className='flex justify-between items-center mb-2'>
																			<span className='text-xs text-muted-foreground font-mono'>
																				Appliance{' '}
																				{j +
																					1}
																			</span>
																			<Button
																				type='button'
																				variant='ghost'
																				size='icon'
																				className='h-6 w-6'
																				onClick={() =>
																					applianceField.removeValue(
																						j,
																					)
																				}>
																				<Trash2 className='text-destructive h-3 w-3' />
																			</Button>
																		</div>
																		<form.AppField
																			name={`householdConfigs[${i}].applianceConfigs[${j}].applianceName`}
																			children={(
																				subField,
																			) => (
																				<subField.ApplianceNameInput />
																			)}
																		/>

																		{/* Tasks */}
																		<div className='mt-3'>
																			<div className='flex items-center justify-between mb-1'>
																				<span className='text-[10px] font-semibold text-muted-foreground uppercase'>
																					Tasks
																				</span>
																				<Button
																					type='button'
																					variant='ghost'
																					size='sm'
																					className='h-5 text-[10px]'
																					onClick={() =>
																						form.pushFieldValue(
																							`householdConfigs[${i}].applianceConfigs[${j}].tasks`,
																							{
																								...applianceTaskDefaultValues,
																								taskId: Math.floor(
																									Math.random() *
																										100000,
																								),
																							},
																						)
																					}>
																					<Plus className='h-2 w-2 mr-1' />{' '}
																					Add
																				</Button>
																			</div>
																			<form.AppField
																				name={`householdConfigs[${i}].applianceConfigs[${j}].tasks`}
																				mode='array'
																				children={(
																					taskField,
																				) => (
																					<div className='space-y-2'>
																						{taskField.state.value?.map(
																							(
																								_,
																								k,
																							) => (
																								<div
																									key={
																										k
																									}
																									className='flex gap-2 items-end border-b pb-2 last:border-0 last:pb-0'>
																									<div className='grid grid-cols-2 md:grid-cols-4 gap-2 flex-1'>
																										<form.AppField
																											name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].taskName`}
																											children={(
																												f,
																											) => (
																												<f.TaskNameInput />
																											)}
																										/>
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
																										<form.AppField
																											name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].humanActivationChance`}
																											children={(
																												f,
																											) => (
																												<f.TaskHumanActivationChanceInput />
																											)}
																										/>
																									</div>
																									<div className='flex flex-col gap-2'>
																										<form.AppField
																											name={`householdConfigs[${i}].applianceConfigs[${j}].tasks[${k}].postponable`}
																											children={(
																												f,
																											) => (
																												<f.TaskPostponableInput />
																											)}
																										/>
																										<Button
																											type='button'
																											variant='ghost'
																											size='icon'
																											className='h-6 w-6 self-end'
																											onClick={() =>
																												taskField.removeValue(
																													k,
																												)
																											}>
																											<Trash2 className='text-destructive h-3 w-3' />
																										</Button>
																									</div>
																								</div>
																							),
																						)}
																					</div>
																				)}
																			/>
																		</div>
																	</div>
																),
															)}
														</div>
													)}
												/>
											</div>
										</div>
									))}
								</div>
							</ScrollArea>
						)}
					/>
				</FieldGroup>
			</FieldSet>
		);
	},
});

export { HouseholdsSection };
