import { Button } from '@/components/ui/button';
import { Field, FieldGroup, FieldSeparator } from '@/components/ui/field';

import { useConfigureSimulation } from '@/hooks/useSimulation';
import { exampleConfig } from '@/routes/config/-components/config-form/example-data';
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
import { FileDown } from 'lucide-react';
import { toast } from 'sonner';

function ConfigForm() {
	const configureMutation = useConfigureSimulation();

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

			// Call the API to configure the simulation
			await configureMutation.mutateAsync(result.data);
			form.reset();
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

	const handleLoadExample = () => {
		form.reset(exampleConfig);
		toast.success('Example configuration loaded', {
			description: '20 households with various appliances',
		});
	};

	return (
		<form
			onSubmit={(e) => {
				e.preventDefault();
				form.handleSubmit();
			}}>
			<FieldGroup>
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
						type='submit'
						disabled={configureMutation.isPending}>
						{configureMutation.isPending ? 'Saving...' : 'Submit'}
					</Button>
					<Button
						type='button'
						variant='secondary'
						onClick={handleLoadExample}>
						<FileDown className='mr-2 h-4 w-4' />
						Load Example
					</Button>
					<Button
						type='button'
						variant='outline'
						onClick={() => form.reset()}>
						Reset
					</Button>
				</Field>
			</FieldGroup>
		</form>
	);
}

export { ConfigForm };
