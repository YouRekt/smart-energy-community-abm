import { Button } from '@/components/ui/button';
import {
	Field,
	FieldDescription,
	FieldLegend,
	FieldSeparator,
	FieldSet,
} from '@/components/ui/field';

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
		onSubmitInvalid: async () => {
			toast.error('Please fill in all the fields');
		},
	});

	return (
		<FieldSet>
			<FieldLegend>Configuration</FieldLegend>
			<FieldDescription>
				Configure the simulation parameters.
			</FieldDescription>
			<form
				id='config-form'
				onSubmit={(e) => {
					e.preventDefault();
					form.handleSubmit();
				}}>
				<div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
					<div className='space-y-6'>
						<StrategySection form={form} />
						<FieldSeparator />
						<BatterySection form={form} />
						<FieldSeparator />
						<PredictionModelSection form={form} />
					</div>
					<div className='space-y-6'>
						<EnergySourcesSection form={form} />
						<FieldSeparator />
						<HouseholdsSection form={form} />
					</div>
				</div>
			</form>
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
		</FieldSet>
	);
}

export { ConfigForm };
