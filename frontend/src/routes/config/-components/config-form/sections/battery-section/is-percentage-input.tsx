import {
	Field,
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

	// For boolean fields, standard onChange might expect an event or boolean depending on library
	// Switch usually provides check state change directly

	return (
		<Field className='flex flex-row items-center justify-between rounded-lg border p-4 shadow-sm'>
			<div className='space-y-0.5'>
				<FieldLabel htmlFor={field.name}>Percentage Mode</FieldLabel>
				<FieldDescription>
					Define starting charge as a percentage of capacity.
				</FieldDescription>
			</div>
			<Switch
				id={field.name}
				checked={field.state.value}
				onCheckedChange={(checked) => field.handleChange(checked)}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export { IsPercentageInput };
