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
				<FieldGroup>
					<form.AppField
						name='predictionModelConfig.name'
						children={(field) => {
							return <field.PredictionModelSelect />;
						}}
					/>
					<form.AppField
						// validators={{
						// 	onChange: ({ value }) => {
						// 		const result =
						// 			predictionModelConfigSchema.shape.minBatteryChargeThreshold.safeParse(
						// 				value,
						// 			);
						// 		return !result.success
						// 			? result.error.message
						// 			: undefined;
						// 	},
						// }}
						name='predictionModelConfig.minBatteryChargeThreshold'
						children={(field) => {
							return <field.MinBatteryChargeThresholdInput />;
						}}
					/>
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
			</FieldSet>
		);
	},
});

export { PredictionModelSection };
