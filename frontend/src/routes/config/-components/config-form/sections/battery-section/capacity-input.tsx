import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { batteryConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

// TODO: Use https://tanstack.com/form/latest/docs/framework/react/guides/form-composition#reusing-groups-of-fields-in-multiple-forms
// for capacity input + is percentage

function CapacityInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof batteryConfigSchema.shape.capacity>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Capacity</FieldLabel>
			<NumberInput
				id={field.name}
				step={0.01}
				value={field.state.value}
				addon={{
					align: 'end',
					content: 'kWh',
				}}
				onChange={(e) =>
					field.handleChange(
						batteryConfigSchema.shape.capacity.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The total capacity of the community battery.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { CapacityInput };
