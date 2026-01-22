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
import { formSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

function StrategySelect() {
	const field =
		useConfigFieldContext<z.input<typeof formSchema.shape.strategyName>>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Strategy</FieldLabel>
			<Select
				name={field.name}
				aria-invalid={isInvalid}
				value={field.state.value}
				onValueChange={(val) =>
					field.handleChange(formSchema.shape.strategyName.parse(val))
				}>
				<SelectTrigger id={field.name}>
					<SelectValue placeholder='Select a strategy' />
				</SelectTrigger>
				<SelectContent position='popper'>
					{formSchema.shape.strategyName
						.unwrap()
						.options.map((option) => (
							<SelectItem key={option} value={option}>
								{option}
							</SelectItem>
						))}
				</SelectContent>
			</Select>
			<FieldDescription>
				Choose the strategy for the simulation.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export { StrategySelect };
