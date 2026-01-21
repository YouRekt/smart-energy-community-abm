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

function MinBatteryChargeThresholdInput() {
	const field =
		useConfigFieldContext<
			z.input<
				typeof predictionModelConfigSchema.shape.minBatteryChargeThreshold
			>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>
				Minimum Battery Threshold
			</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(() => {
						const result =
							predictionModelConfigSchema.shape.minBatteryChargeThreshold.safeParse(
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
			<FieldDescription>
				Minimum battery charge threshold.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { MinBatteryChargeThresholdInput };
