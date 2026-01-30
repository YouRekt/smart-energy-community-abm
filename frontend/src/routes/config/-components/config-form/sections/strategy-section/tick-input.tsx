import { NumberInput } from '@/components/number-input';
import { ButtonGroup } from '@/components/ui/button-group';
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { InputGroupButton } from '@/components/ui/input-group';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { tickConfigSchema } from '@/routes/config/-components/config-form/schema';
import { useState } from 'react';
import { z } from 'zod';

function TickInput() {
	const field = useConfigFieldContext<z.input<typeof tickConfigSchema>>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	const [tickAmount, setTickAmount] = useState(
		field.state.value.tickAmount ?? 1,
	);

	const handleTickAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const value = e.target.value;
		const parsed = tickConfigSchema.shape.tickAmount.safeParse(value);
		if (parsed.success) {
			setTickAmount(parsed.data);
			field.handleChange({
				...field.state.value,
				tickAmount: parsed.data,
			});
		}
	};

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Simulation Tick</FieldLabel>
			<ButtonGroup>
				<NumberInput
					min={1}
					value={field.state.value.tickAmount}
					onChange={handleTickAmountChange}
					addon={{
						align: 'end',
						content: (
							<DropdownMenu>
								<DropdownMenuTrigger asChild>
									<InputGroupButton>
										{field.state.value.tickUnit +
											(tickAmount > 1 ? 's' : '')}
									</InputGroupButton>
								</DropdownMenuTrigger>
								<DropdownMenuContent>
									{tickConfigSchema.shape.tickUnit
										.unwrap()
										.options.map((option) => (
											<DropdownMenuItem
												key={option}
												onSelect={() => {
													field.handleChange({
														...field.state.value,
														tickUnit: option,
													});
												}}
											>
												{tickAmount > 1
													? option + 's'
													: option}
											</DropdownMenuItem>
										))}
								</DropdownMenuContent>
							</DropdownMenu>
						),
					}}
				/>
			</ButtonGroup>
			<FieldDescription>
				Configure what one simulation tick represents in real time.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export default TickInput;
