import { Button } from '@/components/ui/button';
import { Field, FieldGroup, FieldSeparator } from '@/components/ui/field';

import { useConfigForm } from '@/routes/config/-components/config-form/form-context';
import {
	defaultValues,
	formSchema,
} from '@/routes/config/-components/config-form/schema';
import {
	BatterySection,
	EnergySourcesSection,
	HouseholdsSection,
	PredictionModelSection,
	StrategySection,
} from '@/routes/config/-components/config-form/sections';
import { toast } from 'sonner';

function ConfigForm() {
	const form = useConfigForm({
		defaultValues,
		validators: {
			onSubmit: formSchema,
		},
		onSubmit: async ({ value }) => {
			const result = formSchema.safeParse(value);
			if (!result.success) {
				toast.error(result.error.message);
				return;
			}

			toast.success('Config saved successfully');
			console.log(result.data);
		},
		onSubmitInvalid: async ({ value }) => {
			const modifiedValue = {
				...value,
				householdConfigs: [
					...value.householdConfigs.map((householdConfig) => {
						return {
							...householdConfig,
							applianceConfigs:
								householdConfig.applianceConfigs.map(
									(applianceConfig) => {
										return {
											...applianceConfig,
											householdName:
												householdConfig.householdName,
										};
									},
								),
						};
					}),
				],
			};

			toast.error('Invalid configuration');
			console.log(modifiedValue);
		},
	});

	return (
		<form
			id='config-form'
			onSubmit={(e) => {
				e.preventDefault();
				form.handleSubmit();
			}}>
			<FieldGroup>
				{/* <FieldLegend>Configuration</FieldLegend>
			<FieldDescription>
				Configure the simulation parameters.
			</FieldDescription> */}
				<FieldGroup className='@3xl/field-group:flex-row'>
					<FieldGroup className='flex-1'>
						<StrategySection form={form} />
						<FieldSeparator />
						<BatterySection form={form} />
					</FieldGroup>
					<FieldSeparator className='@3xl:hidden' />
					<FieldGroup className='flex-1'>
						<PredictionModelSection form={form} />
					</FieldGroup>
				</FieldGroup>
				<FieldSeparator />
				<FieldGroup className='@4xl/field-group:flex-row'>
					<EnergySourcesSection form={form} />
					<FieldSeparator />
					<HouseholdsSection form={form} />
				</FieldGroup>
				<FieldSeparator />
				<Field orientation='horizontal'>
					<Button
						type='button'
						variant='outline'
						onClick={() => form.reset()}>
						Reset
					</Button>
					<Button type='submit' form='config-form'>
						Submit
					</Button>
				</Field>
			</FieldGroup>
		</form>
	);
}

export { ConfigForm };
