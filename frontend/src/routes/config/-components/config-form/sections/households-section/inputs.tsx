import { Field, FieldError, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { Switch } from '@/components/ui/switch';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import {
	applianceConfigSchema,
	applianceTaskSchema,
	householdConfigSchema,
} from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

// Household Inputs
export function HouseholdNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof householdConfigSchema.shape.householdName>
		>();
	return (
		<Field>
			<FieldLabel>Household Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

// Appliance Inputs
export function ApplianceNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceConfigSchema.shape.applianceName>
		>();
	return (
		<Field>
			<FieldLabel className='text-xs'>Appliance Name</FieldLabel>
			<Input
				className='h-8 text-sm'
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

// Task Inputs
export function TaskNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.taskName>
		>();
	return (
		<Field>
			<FieldLabel className='sr-only'>Task Name</FieldLabel>
			<Input
				className='h-7 text-xs'
				placeholder='Task Name'
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
		</Field>
	);
}

export function TaskHumanActivationChanceInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.humanActivationChance>
		>();
	return (
		<Field>
			<FieldLabel className='text-[10px] text-muted-foreground uppercase'>
				Activation %
			</FieldLabel>
			<Input
				className='h-7 text-xs'
				type='number'
				placeholder='Chance %'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
		</Field>
	);
}

export function TaskDurationInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.duration>
		>();
	return (
		<Field>
			<FieldLabel className='text-[10px] text-muted-foreground uppercase'>
				Duration
			</FieldLabel>
			<Input
				className='h-7 text-xs'
				type='number'
				placeholder='Duration'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
		</Field>
	);
}

export function TaskEnergyPerTickInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.energyPerTick>
		>();
	return (
		<Field>
			<FieldLabel className='text-[10px] text-muted-foreground uppercase'>
				Power
			</FieldLabel>
			<Input
				className='h-7 text-xs'
				type='number'
				placeholder='Power'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
		</Field>
	);
}

export function TaskPostponableInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof applianceTaskSchema.shape.postponable>
		>();
	return (
		<Field className='flex flex-row items-center gap-2 border p-1 rounded'>
			<FieldLabel className='text-[10px] uppercase mb-0'>
				Postponable
			</FieldLabel>
			<Switch
				className='h-4 w-7'
				checked={field.state.value}
				onCheckedChange={(checked) => field.handleChange(checked)}
			/>
		</Field>
	);
}
