import {
	FieldDescription,
	FieldGroup,
	FieldLegend,
	FieldSet,
} from '@/components/ui/field';
import { withConfigForm } from '@/routes/config/-components/config-form/form-context';
import { defaultValues } from '@/routes/config/-components/config-form/schema';

const PredictionModelSection = withConfigForm({
	defaultValues: defaultValues,
	render: ({ form }) => {
		return (
			<FieldSet>
				<FieldLegend>Prediction Model</FieldLegend>
				<FieldDescription>
					Choose the prediction model for the simulation.
				</FieldDescription>
				<FieldGroup className='@xl/field-group:flex-row'>
					<FieldGroup>
						<form.AppField
							name='predictionModelConfig.name'
							children={(field) => {
								return <field.PredictionModelSelect />;
							}}
						/>
						<form.AppField
							name='predictionModelConfig.minBatteryChargeThreshold'
							children={(field) => {
								return <field.MinBatteryChargeThresholdInput />;
							}}
						/>
					</FieldGroup>
					<FieldGroup>
						<form.AppField
							name='predictionModelConfig.productionSafetyFactor'
							children={(field) => {
								return <field.ProductionSafetyFactorInput />;
							}}
						/>
						<form.AppField
							name='predictionModelConfig.windowSize'
							children={(field) => {
								return <field.WindowSizeInput />;
							}}
						/>
					</FieldGroup>
				</FieldGroup>
			</FieldSet>
		);
	},
});

export { PredictionModelSection };
