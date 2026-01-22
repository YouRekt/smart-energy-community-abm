import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { useIsPercentage } from '@/hooks/use-percentage';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { batteryConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

function StartingChargeInput() {
	const { isPercentage } = useIsPercentage();

	const field =
		useConfigFieldContext<
			z.input<typeof batteryConfigSchema.shape.startingCharge>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Starting Charge</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				addon={{ align: 'end', content: isPercentage ? '%' : 'kWh' }}
				min={0}
				max={isPercentage ? 100 : undefined}
				onChange={(e) =>
					field.handleChange(() => {
						const result =
							batteryConfigSchema.shape.startingCharge.safeParse(
								e.target.value,
							);
						return result.success ? result.data : field.state.value;
					})
				}
			/>
			<FieldDescription>
				Initial charge level of the battery.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { StartingChargeInput };
