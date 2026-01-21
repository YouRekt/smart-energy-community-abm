import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { predictionModelConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

function ProductionSafetyFactorInput() {
	const field =
		useConfigFieldContext<
			z.input<
				typeof predictionModelConfigSchema.shape.productionSafetyFactor
			>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>
				Production Safety Factor
			</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(() => {
						const result =
							predictionModelConfigSchema.shape.productionSafetyFactor.safeParse(
								e.target.value,
							);

						if (result.success) {
							return result.data;
						}

						switch (result.error.issues[0].code) {
							case 'too_big':
								return 100;
							case 'too_small':
								return 0;
							default:
								return field.state.value;
						}
					})
				}
				min={0}
				max={100}
			/>
			<FieldDescription>Production safety factor.</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { ProductionSafetyFactorInput };
