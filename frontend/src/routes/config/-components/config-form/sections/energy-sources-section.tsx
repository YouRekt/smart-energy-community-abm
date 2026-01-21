import { Button } from '@/components/ui/button';
import {
	FieldDescription,
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
			<FieldSet>
				<div className='flex items-center justify-between'>
					<div>
						<FieldLegend>Energy Sources</FieldLegend>
						<FieldDescription>
							Define the energy sources in the community.
						</FieldDescription>
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
				</div>

				<FieldGroup>
					<form.AppField
						name='energySourcesConfigs'
						mode='array'
						children={(field) => (
							<ScrollArea className='h-[300px] rounded-md border p-4'>
								<div className='flex flex-col gap-6'>
									{field.state.value.map((_, i) => (
										<div
											key={i}
											className='flex flex-col gap-4 p-4 border rounded-md relative'>
											<div className='absolute right-2 top-2'>
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
											<div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
												<form.AppField
													name={`energySourcesConfigs[${i}].agentName`}
													children={(subField) => (
														<subField.EnergySourceAgentNameInput />
													)}
												/>
												<form.AppField
													name={`energySourcesConfigs[${i}].period`}
													children={(subField) => (
														<subField.EnergySourcePeriodInput />
													)}
												/>
												<form.AppField
													name={`energySourcesConfigs[${i}].maxOutputPower`}
													children={(subField) => (
														<subField.EnergySourceMaxOutputPowerInput />
													)}
												/>
												<form.AppField
													name={`energySourcesConfigs[${i}].peakTick`}
													children={(subField) => (
														<subField.EnergySourcePeakTickInput />
													)}
												/>
												<form.AppField
													name={`energySourcesConfigs[${i}].stdDev`}
													children={(subField) => (
														<subField.EnergySourceStdDevInput />
													)}
												/>
												<form.AppField
													name={`energySourcesConfigs[${i}].variation`}
													children={(subField) => (
														<subField.EnergySourceVariationInput />
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

export { EnergySourcesSection };
