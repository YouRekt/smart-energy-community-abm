import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { formSchema } from '@/routes/config/-components/config-form/schema';
import type z from 'zod';

function SeedInput() {
	const field =
		useConfigFieldContext<z.input<typeof formSchema.shape.seed>>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Seed</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						formSchema.shape.seed.safeParse(e.target.value).data ??
							field.state.value,
					)
				}
			/>
			<FieldDescription>
				The seed for the random number generator.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export default SeedInput;
