import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldContent,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import {
	applianceConfigSchema,
	applianceTaskSchema,
	householdConfigSchema,
} from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

export function HouseholdNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof householdConfigSchema.shape.householdName>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel>Household Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function ApplianceNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceConfigSchema.shape.applianceName>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel>Appliance Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			<FieldDescription>The name of the appliance.</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function TaskNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.taskName>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel>Task Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function TaskHumanActivationChanceInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.humanActivationChance>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>
				Human Activation Chance
			</FieldLabel>
			<NumberInput
				id={field.name}
				min={0}
				max={100}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						applianceTaskSchema.shape.humanActivationChance.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The % chance of the human activating the task.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function TaskDurationInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.duration>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Duration</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				min={1}
				onChange={(e) =>
					field.handleChange(
						applianceTaskSchema.shape.duration.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>The duration of the task.</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function TaskEnergyPerTickInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.energyPerTick>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Energy Per Tick (kW)</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						applianceTaskSchema.shape.energyPerTick.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The energy per tick of the task.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function TaskPostponableInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.postponable>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field orientation='horizontal' className='max-w-sm'>
			<FieldContent>
				<FieldLabel htmlFor={field.name}>Postponable</FieldLabel>
				<FieldDescription>The task can be postponed.</FieldDescription>
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
