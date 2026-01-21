import {
	Field,
	FieldContent,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { Switch } from '@/components/ui/switch';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { batteryConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

function IsPercentageInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof batteryConfigSchema.shape.isPercentage>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field orientation='horizontal' className='max-w-sm'>
			<FieldContent>
				<FieldLabel htmlFor={field.name}>Percentage Mode</FieldLabel>
				<FieldDescription>
					Define starting charge as a percentage of capacity.
				</FieldDescription>
			</FieldContent>
			<Switch
				id={field.name}
				checked={field.state.value}
				onCheckedChange={(checked) => field.handleChange(checked)}
			/>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { IsPercentageInput };
