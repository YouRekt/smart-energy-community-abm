import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from '@/components/ui/select';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { predictionModelConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

function PredictionModelSelect() {
	const field =
		useConfigFieldContext<
			z.input<typeof predictionModelConfigSchema.shape.name>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Prediction Model</FieldLabel>
			<Select
				name={field.name}
				aria-invalid={isInvalid}
				value={field.state.value}
				onValueChange={(val) =>
					field.handleChange(
						predictionModelConfigSchema.shape.name.parse(val),
					)
				}>
				<SelectTrigger id={field.name}>
					<SelectValue placeholder='Select a prediction model' />
				</SelectTrigger>
				<SelectContent position='popper'>
					{predictionModelConfigSchema.shape.name
						.unwrap()
						.options.map((option) => (
							<SelectItem key={option} value={option}>
								{option}
							</SelectItem>
						))}
				</SelectContent>
			</Select>
			<FieldDescription>
				Choose the prediction model for the simulation.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { PredictionModelSelect };
